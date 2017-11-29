package com.magicbox.dot;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PontoFragment extends Fragment {


    private DatabaseReference mDatabase;

    public PontoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ponto, container, false);


        SparkView sparkView = (SparkView) view.findViewById(R.id.sparkview);
        float[] spark = {0, 0, 0f, 2f, -5f, 0, 0};

        sparkView.setAdapter(new MinhaSemanaAdapter(spark));



        final ImageView image = (ImageView) view.findViewById(R.id.imgevW);

        this.mDatabase = FirebaseDatabase.getInstance().getReference();


        //RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pontos);

        //List<Ponto> livros = Arrays.asList(new Ponto(new Date(), new Date()), new Ponto(new Date(), new Date())); // recupera do banco de dados ou webservice

        // recyclerView.setAdapter(new PontoAdapter(getContext(), livros));

        //RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //recyclerView.setLayoutManager(layout);


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
                                                    // Successfully downloaded data to local file
                                                    // ...

                                                    Uri uri = Uri.fromFile(localFile);

                                                    image.setImageURI(uri);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle failed download
                                            // ...
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });


//
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

                    ListView templatesList = (ListView) view.findViewById(R.id.templatesListView);
                    ArrayAdapter<Template> adapterTemplates = new ArrayAdapter<Template>(getContext(), android.R.layout.simple_list_item_1, Collections.unmodifiableList(templates));
                    templatesList.setAdapter(adapterTemplates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }


}
