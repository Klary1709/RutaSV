package com.svrutas.app.ui.estaciones;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.svrutas.app.HomeActivity;
import com.svrutas.app.R;
import com.svrutas.app.data.Estacion;
import com.svrutas.app.data.Estacion_ruta;

import java.util.List;

public class EstacionRutaAdapter extends RecyclerView.Adapter<EstacionViewHolder> implements Filterable {
    private static List<Estacion_ruta> listaEstaciones;
    private static List<Estacion_ruta> listaAllEstaciones;
    private Activity parent;

    public EstacionRutaAdapter(List<Estacion_ruta> lst, Activity parent){
        listaAllEstaciones = lst;
        listaEstaciones = lst;
        this.parent = parent;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public EstacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_estacion, parent, false);
        EstacionViewHolder vhEstacion = new EstacionViewHolder(vista);
        return vhEstacion;
    }

    @Override
    public void onBindViewHolder(@NonNull EstacionViewHolder holder, int position) {
        CardView card = holder.getCardEstacion();
        holder.getTvEstacion().setText(listaEstaciones.get(position).getName());

        holder.getTvRecorrido().setVisibility(View.GONE);

        card.setOnClickListener(view -> {
            abrirDetalleEstacion(holder, position, view);
        });
    }

    private void abrirDetalleEstacion(EstacionViewHolder holder, int pos, View view){
        SharedPreferences.Editor editor = holder.itemView.getContext().getSharedPreferences(
                HomeActivity.KEY_DATA_RUTA,
                Context.MODE_PRIVATE)
                .edit();
        editor.putString(HomeActivity.KEY_ESTACION_ID, listaEstaciones.get(pos).getId());
        editor.apply();

        Intent intent = new Intent(parent, DetalleEstacionActivity.class);
        ContextCompat.startActivity(parent, intent, null);
    }

    @Override
    public int getItemCount() {
        return listaEstaciones.size();
    }
}
