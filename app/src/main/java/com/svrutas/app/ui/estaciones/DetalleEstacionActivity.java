package com.svrutas.app.ui.estaciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.svrutas.app.HomeActivity;
import com.svrutas.app.R;
import com.svrutas.app.data.Estacion;
import com.svrutas.app.data.Estacion_ruta;
import com.svrutas.app.data.Ruta;
import com.svrutas.app.data.Ruta_estacion;
import com.svrutas.app.databinding.ActivityDetalleEstacionBinding;
import com.svrutas.app.databinding.ActivityDetalleRutaBinding;
import com.svrutas.app.ui.rutas.RutaEstacionAdapter;

import java.util.List;

public class DetalleEstacionActivity extends AppCompatActivity {
    ActivityDetalleEstacionBinding binding;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final String estacionesCollection = "estaciones";
    private String id;
    private Estacion estacion;

    private RecyclerView recyclerRutasEstacion;
    private RutaEstacionAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleEstacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerRutasEstacion = binding.recyclerRutasEstacion;

        getPrefs();
        getEstacion();
    }
    private void getPrefs(){
        SharedPreferences prefs = getSharedPreferences(HomeActivity.KEY_DATA_RUTA, Context.MODE_PRIVATE);
        id = prefs.getString(HomeActivity.KEY_ESTACION_ID, "");
    }
    private void getEstacion(){
        database.collection(estacionesCollection)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    estacion = documentSnapshot.toObject(Estacion.class);
                    updateUI(estacion.getRoutes());
                });
    }
    private void updateUI(List<Ruta_estacion> lst){
        populateRecycler(lst);

        TextView tvEstacion = (TextView) findViewById(R.id.tvNombreEstacion);
        TextView tvReferencia = (TextView) findViewById(R.id.tvReferenciaEstacion);
        TextView tvLugaresEstacion = (TextView) findViewById(R.id.tvLugaresEstacion);

        tvEstacion.setText(estacion.getName());
        tvReferencia.setText(estacion.getReference());
        tvLugaresEstacion.setText(estacion.getCity_town()+", "+estacion.getDepartment_state());
    }
    private void populateRecycler(List<Ruta_estacion> lst){
        layoutManager = new LinearLayoutManager(getBaseContext());
        adapter = new RutaEstacionAdapter(lst, this);

        recyclerRutasEstacion.setHasFixedSize(true);
        recyclerRutasEstacion.setLayoutManager(layoutManager);
        recyclerRutasEstacion.setAdapter(adapter);
    }
}