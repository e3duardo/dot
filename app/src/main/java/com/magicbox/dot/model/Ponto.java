package com.magicbox.dot.model;

import com.google.firebase.database.DatabaseReference;
import com.magicbox.dot.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Criado por eduardo em 11/09/17.
 */

public class Ponto {

    private String key;

    private Date data;
    private Date hora;
    private Template template;

    private String foto;

    public Ponto(){
    }


    public Ponto(DatabaseReference mDatabase) {
        key = mDatabase.child("pontos").push().getKey();
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Map toMap(){
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("key", key);
        mapa.put("data", DateUtils.dataParaString(data));
        mapa.put("hora", DateUtils.dataParaHoraString(hora));
        mapa.put("template", template.toMap());

        return mapa;
    }

    public static Ponto fromMap(Object map) {
        Map<String, Object> mapa = (Map<String, Object>) map;

        Ponto ponto = new Ponto();

        if(mapa.containsKey("key"))
            ponto.setKey((String) mapa.get("key"));

        if(mapa.containsKey("data"))
            ponto.setData(DateUtils.stringParaData((String) mapa.get("data")));

        if(mapa.containsKey("hora"))
            ponto.setHora(DateUtils.horaStringParaData((String) mapa.get("hora")));

        if(mapa.containsKey("template"))
            ponto.setTemplate(Template.fromMap(mapa.get("template")));

        if(mapa.containsKey("foto"))
            ponto.setFoto((String) mapa.get("foto"));

        return ponto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ponto ponto = (Ponto) o;

        return key.equals(ponto.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return DateUtils.dataParaHoraString(this.hora) + "(" + this.getDiferencaEmMinutosOuHoras() + ")";
    }

    public String getDiferencaEmMinutosOuHoras() {
        long diferencaEmMinutos = DateUtils.diferencaEmMinutos(this.getHora(), this.getTemplate().getHorario());

        if(Math.abs(diferencaEmMinutos) < 60)
            return diferencaEmMinutos + "m";
        else
            return (diferencaEmMinutos/60) + "h";
    }


    public boolean temFoto() {
        return this.foto != null && !this.foto.isEmpty();
    }
}
