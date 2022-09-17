package com.svrutas.app.ui.estaciones;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrutas.app.R;

public class EstacionViewHolder extends RecyclerView.ViewHolder {
    private CardView cardEstacion;
    private TextView tvEstacion;
    private TextView tvRecorrido;

    public EstacionViewHolder(@NonNull View itemView) {
        super(itemView);
        this.cardEstacion = itemView.findViewById(R.id.cardEstacion);
        this.tvEstacion = itemView.findViewById(R.id.tvEstacion);
        this.tvRecorrido = itemView.findViewById(R.id.tvRecorrido);
    }

    public CardView getCardEstacion() {
        return cardEstacion;
    }
    public TextView getTvEstacion() {
        return tvEstacion;
    }
    public TextView getTvRecorrido() {
        return tvRecorrido;
    }
}
