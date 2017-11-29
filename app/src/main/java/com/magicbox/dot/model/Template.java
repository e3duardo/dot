package com.magicbox.dot.model;

import com.google.firebase.database.DatabaseReference;
import com.magicbox.dot.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Criado por eduardo em 06/10/17.
 */

public class Template{

    private String key;
    private DiaSemana semana;
    private Date horario;

    public Template(){
    }


    public Template(DatabaseReference mDatabase) {
        key = mDatabase.child("templates").push().getKey();
    }

    public Template(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DiaSemana getSemana() {
        return semana;
    }

    public void setSemana(DiaSemana semana) {
        this.semana = semana;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }



    public Map toMap(){
        Map<String, String> mapa = new HashMap<>();

        mapa.put("key", key);
        mapa.put("semana", semana.name());
        mapa.put("horario", DateUtils.dataParaHoraString(horario));

        return mapa;
    }

    public static Template fromMap(Object map) {
        Map<String, Object> mapa = (Map<String, Object>) map;

        Template template = new Template();

        if(mapa.containsKey("key"))
            template.setKey((String) mapa.get("key"));

        if(mapa.containsKey("semana"))
            template.setSemana(DiaSemana.valueOf((String) mapa.get("semana")));

        if(mapa.containsKey("horario"))
            template.setHorario(DateUtils.horaStringParaData((String) mapa.get("horario")));

        return template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        return key.equals(template.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return DateUtils.dataParaHoraString(this.horario);
    }
}
