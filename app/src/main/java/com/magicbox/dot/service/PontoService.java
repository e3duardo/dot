package com.magicbox.dot.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.magicbox.dot.model.DiaSemana;
import com.magicbox.dot.model.Ponto;
import com.magicbox.dot.model.Template;
import com.magicbox.dot.utils.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Criado por eduardo em 28/11/2017.
 */

public class PontoService {

    private DatabaseReference mDatabase;

    public PontoService(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }


    public Ponto salvar(Template template, Date data, Date hora) {


        Ponto ponto = new Ponto(mDatabase);

        ponto.setData(data);
        ponto.setHora(hora);

        ponto.setTemplate(template);

        mDatabase.child("pontos").child(ponto.getKey()).setValue(ponto.toMap());

        return ponto;
    }
}
