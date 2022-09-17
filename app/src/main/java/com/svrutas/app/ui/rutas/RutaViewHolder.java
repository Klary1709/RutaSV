package com.svrutas.app.ui.rutas;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrutas.app.R;

public class RutaViewHolder extends RecyclerView.ViewHolder {
    private CardView cardRuta;
    private TextView tvNombreRuta;
    private TextView tvTipoRuta;
    private TextView tvLugaresRuta;

    public RutaViewHolder(@NonNull View itemView) {
        super(itemView);
        this.cardRuta = itemView.findViewById(R.id.cardRuta);
        this.tvNombreRuta = itemView.findViewById(R.id.tvNombreRuta);
        this.tvTipoRuta = itemView.findViewById(R.id.tvTipoRuta);
        this.tvLugaresRuta = itemView.findViewById(R.id.tvLugaresRuta);
    }

    public CardView getCardRuta() {
        return cardRuta;
    }
    public TextView getTvNombreRuta() {
        return tvNombreRuta;
    }
    public TextView getTvTipoRuta() {
        return tvTipoRuta;
    }
    public TextView getTvLugaresRuta() {
        return tvLugaresRuta;
    }
}
