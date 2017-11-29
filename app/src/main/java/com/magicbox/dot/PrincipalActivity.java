package com.magicbox.dot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.magicbox.dot.adapter.MinhaSemanaAdapter;
import com.magicbox.dot.adapter.PontoAdapter;
import com.magicbox.dot.adapter.TemplateAdapter;
import com.magicbox.dot.model.Ponto;
import com.magicbox.dot.model.Template;
import com.magicbox.dot.service.PontoService;
import com.magicbox.dot.service.TemplateService;
import com.magicbox.dot.utils.DateUtils;
import com.robinhood.spark.SparkView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrincipalActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private DatabaseReference mDatabase;
    private PontoService pontoService;

    private Template nextTemplate;

    private FloatingActionButton fab;
    private String mCurrentPhotoPath;
    private String mCurrentPontoKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.pontoService = new PontoService(mDatabase);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ponto salvo = pontoService.salvar(nextTemplate, new Date(), new Date());

                mCurrentPontoKey = salvo.getKey();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                dispatchTakePictureIntent(salvo);

                carregarTemplates();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Template template = Template.fromMap(dataSnapshot.getValue());
//        templates.add(template);


        carregarTemplates();
    }

    private void carregarTemplates() {



        fab.setVisibility(View.INVISIBLE);

        mDatabase.child("pontos").orderByChild("data").equalTo(DateUtils.dataParaString(new Date())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Template> templatesJaMarcadosHoje = new LinkedList<>();

                if(dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> mapa = (Map<String, Object>) dataSnapshot.getValue();

                    List<Ponto> pontos = new LinkedList<>();
                    for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                        Ponto ponto = Ponto.fromMap(entry.getValue());
                        ponto.setKey(entry.getKey());
                        pontos.add(ponto);
                        templatesJaMarcadosHoje.add(ponto.getTemplate());
                    }
                }

                mDatabase.child("templates")
                .orderByChild("semana")
                .equalTo(DateUtils.dataParaDiaSemana(new Date()).name())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() > 0){
                            Map<String, Object> mapa = (Map<String, Object>) dataSnapshot.getValue();

                            List<Template> templates = new LinkedList<>();

                            for (Map.Entry<String, Object> entry : mapa.entrySet()){
                                Template template = Template.fromMap(entry.getValue());
                                template.setKey(entry.getKey());
                                templates.add(template);
                            }

                            templates.removeAll(templatesJaMarcadosHoje);

                            if(templates.size() > 0) {
                                Collections.sort(templates, new Comparator<Template>() {
                                    public int compare(Template t1, Template t2) {
                                        return t1.getHorario().compareTo(t2.getHorario());
                                    }
                                });

                                nextTemplate = templates.get(0);

                                fab.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_configuracoes){
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_sair) {
            //LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    //https://developer.android.com/training/camera/photobasics.html
    // cria foto
    private File createImageFile(final Ponto ponto) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = ponto.getKey(); //"JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();


        return image;
    }

    //inicia intent
    private void dispatchTakePictureIntent(Ponto ponto) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(ponto);
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.print(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");



           // mImageView.setImageBitmap(imageBitmap);

            //https://developer.android.com/training/camera/photobasics.html




            StorageReference reference = FirebaseStorage.getInstance().getReference();
            Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
            StorageReference riversRef = reference.child("pontos").child(mCurrentPontoKey+".jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            mDatabase.child("pontos").child(mCurrentPontoKey).child("foto").setValue(downloadUrl.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });


        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PontoFragment();
                case 1:
                    return new GraficosFragment();
                case 2:
                    return new NoticiasFragment();
            }
            return new PontoFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PONTO";
                case 1:
                    return "GRÁFICOS";
                case 2:
                    return "NOTÍCIAS";
            }
            return null;
        }
    }
}
