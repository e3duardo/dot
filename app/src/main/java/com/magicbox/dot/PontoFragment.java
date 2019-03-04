package com.magicbox.dot;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.magicbox.dot.adapter.MinhaSemanaAdapter;
import com.magicbox.dot.model.Ponto;
import com.magicbox.dot.model.Template;
import com.magicbox.dot.utils.DateUtils;
import com.robinhood.spark.SparkView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PontoFragment extends Fragment {


    private DatabaseReference mDatabase;

    private static final String TAG = "MainActivity";

    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_ponto, container, false);

        float[] spark = {0, 0, 0f, 2f, -5f, 0, 0};

        SparkView sparkView = (SparkView) view.findViewById(R.id.sparkview);
        sparkView.setAdapter(new MinhaSemanaAdapter(spark));

        //final ImageView image = (ImageView) view.findViewById(R.id.imgevW);

        this.mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("pontos")
        .orderByChild("data")
        .equalTo(DateUtils.dataParaString(new Date()))
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> mapa = (Map<String, Object>) dataSnapshot.getValue();

                    final List<Ponto> pontos = new LinkedList<>();
                    for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                        Ponto ponto = Ponto.fromMap(entry.getValue());
                        ponto.setKey(entry.getKey());
                        pontos.add(ponto);
                    }

                    ListView pontosList = (ListView) view.findViewById(R.id.pontosListView);
                    ArrayAdapter<Ponto> adapterPontos = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, Collections.unmodifiableList(pontos));
                    pontosList.setAdapter(adapterPontos);

                    pontosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                            Ponto ponto = pontos.get(i);

                            if(ponto.temFoto()) {

                                try {
                                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                    final File localFile = File.createTempFile(ponto.getKey(), ".jpg", storageDir);

                                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(ponto.getFoto());


                                    reference.getFile(localFile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    Uri uri = Uri.fromFile(localFile);

                                                    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_foto, null);

                                                    ImageView imageView = (ImageView) view.findViewById(R.id.imagew);
                                                    imageView.setImageURI(uri);

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("Titulo");
                                                    builder.setView(view);

                                                    final AlertDialog alerta = builder.create();
                                                    alerta.show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        mDatabase.child("templates")
        .orderByChild("semana")
        .equalTo(DateUtils.dataParaDiaSemana(new Date()).name())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> mapa = (Map<String, Object>) dataSnapshot.getValue();

                    List<Template> templates = new LinkedList<>();
                    for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                        Template template = Template.fromMap(entry.getValue());
                        template.setKey(entry.getKey());
                        templates.add(template);
                    }

                    Collections.sort(templates, new Comparator<Template>() {
                        public int compare(Template t1, Template t2) {
                            return t1.getHorario().compareTo(t2.getHorario());
                        }
                    });

                    ListView templatesList = (ListView) view.findViewById(R.id.templatesListView);
                    ArrayAdapter<Template> adapterTemplates = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, Collections.unmodifiableList(templates));
                    templatesList.setAdapter(adapterTemplates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        MobileAds.initialize(getActivity(), "ca-app-pub-4739730263274550~6836861623");

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }
}