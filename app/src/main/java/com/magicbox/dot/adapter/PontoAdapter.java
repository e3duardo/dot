package com.magicbox.dot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.magicbox.dot.R;
import com.magicbox.dot.model.Ponto;
import com.magicbox.dot.utils.DateUtils;

import java.util.List;

/**
 * Criado por eduardo em 11/09/17.
 */

public class PontoAdapter extends RecyclerView.Adapter<PontoViewHolder> {


    private Context context;
    private List<Ponto> pontos;

    public PontoAdapter(Context context, List<Ponto> pontos) {
        this.context = context;
        this.pontos = pontos;
    }

    @Override
    public PontoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_ponto, parent, false);

        return new PontoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PontoViewHolder holder, int position) {
        Ponto ponto = this.pontos.get(position);

        holder.abertura.setText(DateUtils.dataParaHoraString(ponto.getData()));
        holder.fechamento.setText(DateUtils.dataParaHoraString(ponto.getHora()));
    }

    @Override
    public int getItemCount() {
        return this.pontos.size();
    }
}

class PontoViewHolder extends RecyclerView.ViewHolder{

    final TextView abertura;
    final TextView fechamento;

    public PontoViewHolder(View view) {
        super(view);

        abertura = (TextView) view.findViewById(R.id.abertura);
        fechamento = (TextView) view.findViewById(R.id.fechamento);
    }

}
