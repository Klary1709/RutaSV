package com.svrutas.app.ui.estaciones;

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
import com.svrutas.app.data.Estacion;
import com.svrutas.app.data.Ruta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EstacionAdapter extends RecyclerView.Adapter<EstacionViewHolder> implements Filterable {
    private static List<Estacion> listaEstaciones;
    private static List<Estacion> listaAllEstaciones;
    private Fragment parent;

    public EstacionAdapter(List<Estacion> listaEstaciones, Fragment parent) {
        this.listaEstaciones = listaEstaciones;
        this.listaAllEstaciones = new ArrayList(listaEstaciones);
        this.parent = parent;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Estacion> filteredList = new ArrayList<>();

            if(charSequence.toString().isEmpty()){ //si no hay caracteres que buscar
                filteredList = listaAllEstaciones; //usa la lista completa
            }else{
                for(Estacion r: listaAllEstaciones){
                    String noTildes = cleanTildes(charSequence.toString().toLowerCase());
                    String lowerName = r.getName().toLowerCase();

                    String cleanCity = cleanTildes(r.getCity_town()).toLowerCase();
                    boolean containsCityTown = cleanCity.contains(noTildes);

                    String cleanDep = cleanTildes(r.getDepartment_state()).toLowerCase();
                    boolean containsDepartmentState = cleanDep.contains(noTildes);

                    String cleanRef = cleanTildes(r.getReference()).toLowerCase();
                    boolean containsRef = cleanRef.contains(noTildes);

                    if(lowerName.contains(noTildes)
                            || containsCityTown
                            || containsDepartmentState
                            || containsRef
                    ){
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
            listaEstaciones.clear();
            listaEstaciones.addAll((Collection<? extends Estacion>) filterResults.values);
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
    public EstacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_estacion, parent, false);
        EstacionViewHolder vhEstacion = new EstacionViewHolder(vista);
        return vhEstacion;
    }

    @Override
    public void onBindViewHolder(@NonNull EstacionViewHolder holder, int position) {
        CardView card = holder.getCardEstacion();
        holder.getTvEstacion().setText(listaEstaciones.get(position).getName());

        String places = listaEstaciones.get(position).getCity_town() + ", " + listaEstaciones.get(position).getDepartment_state();
        holder.getTvRecorrido().setText(places);


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

        Intent intent = new Intent(parent.getContext(), DetalleEstacionActivity.class);
        ContextCompat.startActivity(parent.getContext(), intent, null);
    }

    @Override
    public int getItemCount() {
        return listaEstaciones.size();
    }
}
