package com.magicbox.dot.service;

import android.text.Editable;

import com.google.firebase.database.DatabaseReference;
import com.magicbox.dot.model.DiaSemana;
import com.magicbox.dot.model.Template;
import com.magicbox.dot.utils.DateUtils;

import java.util.Date;

/**
 * Criado por eduardo em 28/11/2017.
 */

public class TemplateService {

    private DatabaseReference mDatabase;

    public TemplateService(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }


    public void salvar(DiaSemana dia, Date horario) {
        Template template = new Template(mDatabase);

        template.setSemana(dia);
        template.setHorario(horario);

        mDatabase.child("templates").child(template.getKey()).setValue(template.toMap());
    }
}
