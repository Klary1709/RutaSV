package com.svrutas.app.ui.rutas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.svrutas.app.HomeActivity;
import com.svrutas.app.R;
import com.svrutas.app.data.Ruta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class RutaAdapter extends RecyclerView.Adapter<RutaViewHolder> implements Filterable {
    private static List<Ruta> listaRutas;
    private static List<Ruta> listaAllRutas;
    private Fragment parent;

    public RutaAdapter(List<Ruta> listaRutas, Fragment parent) {
        this.listaRutas = listaRutas;
        this.listaAllRutas = new ArrayList(listaRutas);
        this.parent = parent;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Ruta> filteredList = new ArrayList<>();

            if(charSequence.toString().isEmpty()){ //si no hay caracteres que buscar
                filteredList = listaAllRutas; //usa la lista completa
            }else{
                for(Ruta r: listaAllRutas){
                    String noTildes = cleanTildes(charSequence.toString().toLowerCase());
                    String lowerName = r.getName().toLowerCase();
                    boolean containsCityTown = false;
                    boolean containsDepartmentState = false;

                    for(String city: r.getCities_towns()){
                        String cleanCity = cleanTildes(city).toLowerCase();
                        if(cleanCity.contains(noTildes)){ //si la ciudad/municipio contiene el termino buscado
                            containsCityTown = true;
                            break;
                        }
                    }
                    for(String dep: r.getDepartments_states()){
                        String cleanDep = cleanTildes(dep).toLowerCase();
                        if(cleanDep.contains(noTildes)){ //si el departamento contiene el termino buscado
                            containsDepartmentState = true;
                            break;
                        }
                    }

                    if(lowerName.contains(noTildes) || containsCityTown || containsDepartmentState){
                        filteredList.add(r);
                    }
                }
            }

            FilterResults resultList = new FilterResults();
            resultList.values = filteredList;
            return resultList;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listaRutas.clear();
            listaRutas.addAll((Collection<? extends Ruta>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    private String cleanTildes(String input){
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for(int i=0; i<original.length(); i++){
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
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

        Intent intent = new Intent(parent.getContext(), DetalleRutaActivity.class);
        ContextCompat.startActivity(parent.getContext(), intent, null);
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }
}
