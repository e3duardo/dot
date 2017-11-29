package com.magicbox.dot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.magicbox.dot.R;
import com.magicbox.dot.model.DiaSemana;
import com.magicbox.dot.model.Template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Criado por eduardo em 11/09/17.
 */

public class TemplateAdapter extends RecyclerView.Adapter<TemplateViewHolder> {


    private Context context;
    private List<DiaSemana> dias = Arrays.asList(DiaSemana.values());
    private Map<DiaSemana, List<Template>> mapa;

    public TemplateAdapter(Context context){//, List<Template> templates) {
        this.context = context;

        this.mapa = new HashMap<>();

        for(DiaSemana d : DiaSemana.values()){
            this.mapa.put(d, new LinkedList<Template>());
        }
    }

    @Override
    public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_template, parent, false);

        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TemplateViewHolder holder, int position) {
        DiaSemana dia = this.dias.get(position);
        List<Template> templates = mapa.get(dia);

        holder.semana.setText(dia.getSigla());

        ArrayAdapter<Template> adapter = new ArrayAdapter<Template>(this.context, R.layout.item_hora, templates);
        holder.horarios.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return this.dias.size();
    }

    public void add(Template template) {
        this.mapa.get(template.getSemana()).add(template);
        this.notifyDataSetChanged();
    }
}

class TemplateViewHolder extends RecyclerView.ViewHolder{

    final TextView semana;
    final ListView horarios;

    public TemplateViewHolder(View view) {
        super(view);

        semana = (TextView) view.findViewById(R.id.semana);
        horarios = (ListView) view.findViewById(R.id.lista_horarios);
    }

}
