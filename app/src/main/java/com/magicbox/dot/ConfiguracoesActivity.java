package com.magicbox.dot;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magicbox.dot.adapter.TemplateAdapter;
import com.magicbox.dot.model.DiaSemana;
import com.magicbox.dot.model.Template;
import com.magicbox.dot.service.TemplateService;
import com.magicbox.dot.utils.DateUtils;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Button buttonAdicionar;
    private Spinner diaSemana;
    private EditText entrada;
    private EditText saida;
    private RecyclerView listaTemplatesDia;

    private DatabaseReference mDatabase;
    private TemplateService templateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.templateService = new TemplateService(mDatabase);

        this.diaSemana = (Spinner) findViewById(R.id.dia_semana);
        this.entrada = (EditText) findViewById(R.id.entrada);
        this.saida = (EditText) findViewById(R.id.saida);
        this.buttonAdicionar = (Button) findViewById(R.id.adicionar);

        this.diaSemana.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DiaSemana.values()));




        final TemplateAdapter adapter = new TemplateAdapter(this);


        this.mDatabase.child("templates").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Template template = Template.fromMap(dataSnapshot.getValue());

                adapter.add(template);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Template template = Template.fromMap(dataSnapshot.getValue());

                adapter.remove(template);

                adapter.add(template);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Template template = Template.fromMap(dataSnapshot.getValue());

                adapter.remove(template);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        this.listaTemplatesDia = (RecyclerView) findViewById(R.id.lista_horarios);
        this.listaTemplatesDia.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        this.listaTemplatesDia.setAdapter(adapter);

        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                templateService.salvar((DiaSemana) diaSemana.getSelectedItem(), DateUtils.horaStringParaData(entrada.getText()));
                templateService.salvar((DiaSemana) diaSemana.getSelectedItem(), DateUtils.horaStringParaData(saida.getText()));

                resetView();
                Snackbar.make(view, "Template adicionado.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void resetView(){
        this.entrada.setText("");
        this.saida.setText("");
        //this.diaSemana.setSelection(0);
        //this.horario.setfo
    }
}
