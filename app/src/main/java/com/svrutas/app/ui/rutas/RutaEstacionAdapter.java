package com.svrutas.app.ui.rutas;

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
import com.svrutas.app.data.Ruta;
import com.svrutas.app.data.Ruta_estacion;

import java.util.ArrayList;
import java.util.List;

public class RutaEstacionAdapter extends RecyclerView.Adapter<RutaViewHolder> implements Filterable {
    private static List<Ruta_estacion> listaRutas;
    private static List<Ruta_estacion> listaAllRutas;
    private Activity parent;

    public RutaEstacionAdapter(List<Ruta_estacion> listaRutas, Activity parent) {
        this.listaRutas = listaRutas;
        this.listaAllRutas = new ArrayList(listaRutas);
        this.parent = parent;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ruta, parent, false);
        RutaViewHolder vhRuta = new RutaViewHolder(vista);
        return vhRuta;
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        CardView card = holder.getCardRuta();
        holder.getTvNombreRuta().setText(listaRutas.get(position).getName());
        holder.getTvTipoRuta().setText(listaRutas.get(position).getType());

        String type = listaRutas.get(position).getType();
        List<String> lugares = new ArrayList<>();

        if(type.equals("INTERURBANO") || type.equals("URBANO")) {
            List<String> municipios = listaRutas.get(position).getCities_towns();

            for(String m: municipios){
                lugares.add(m);
            }
        }else if(type.equals("INTERDEPARTAMENTAL")){
            List<String> departamentos = listaRutas.get(position).getCities_towns();

            for(String d: departamentos){
                lugares.add(d);
            }
        }

        holder.getTvLugaresRuta().setText(String.join(" - ", lugares));

        card.setOnClickListener(view -> {
            abrirMapa(holder, position, view);
        });
    }

    private void abrirMapa(RutaViewHolder holder, int pos, View view){
        SharedPreferences.Editor editor = holder.itemView.getContext().getSharedPreferences(
                HomeActivity.KEY_DATA_RUTA,
                Context.MODE_PRIVATE)
                .edit();
        editor.putString(HomeActivity.KEY_RUTA_ID, listaRutas.get(pos).getId());
        editor.apply();

        Intent intent = new Intent(parent, DetalleRutaActivity.class);
        ContextCompat.startActivity(parent, intent, null);
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }
}
