package com.svrutas.app.ui.rutas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.svrutas.app.HomeActivity;
import com.svrutas.app.R;
import com.svrutas.app.data.Estacion;
import com.svrutas.app.data.Estacion_ruta;
import com.svrutas.app.data.Ruta;
import com.svrutas.app.databinding.ActivityDetalleRutaBinding;
import com.svrutas.app.databinding.ActivityHomeBinding;
import com.svrutas.app.databinding.FragmentRutasBinding;
import com.svrutas.app.ui.estaciones.EstacionAdapter;
import com.svrutas.app.ui.estaciones.EstacionRutaAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetalleRutaActivity extends AppCompatActivity {
    ActivityDetalleRutaBinding binding;
    private String id;
    private Ruta ruta;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final String rutasCollection = "rutas";

    private RecyclerView recyclerEstacionesRuta;
    private EstacionRutaAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerEstacionesRuta = binding.recyclerEstacionesRuta;

        getPrefs();
        getRuta();
    }
    private void getPrefs(){
        SharedPreferences prefs = getSharedPreferences(HomeActivity.KEY_DATA_RUTA, Context.MODE_PRIVATE);
        id = prefs.getString(HomeActivity.KEY_RUTA_ID, "");
    }
    private void getRuta(){
        database.collection(rutasCollection)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ruta = documentSnapshot.toObject(Ruta.class);
                    updateUI(ruta.getStops());
                });
    }
    private void updateUI(List<Estacion_ruta> lst){
        populateRecycler(lst);

        TextView tvRuta = (TextView) findViewById(R.id.tvRuta);
        TextView tvTipo = (TextView) findViewById(R.id.tvTipo);
        TextView tvRecorrido = (TextView) findViewById(R.id.tvRecorrido);
        ExtendedFloatingActionButton fabMapa = (ExtendedFloatingActionButton) findViewById(R.id.fabMapa);
        TextView tvMin = (TextView) findViewById(R.id.tvMin);
        TextView tvMax = (TextView) findViewById(R.id.tvMax);
        TextView tvSeparator = (TextView) findViewById(R.id.tvSeparator);

        tvRuta.setText(ruta.getName());
        tvTipo.setText(ruta.getType());

        String newRecorrido = ruta.getCities_towns() != null ? String.join(" - ", ruta.getCities_towns()) : "-";
        tvRecorrido.setText(newRecorrido);

        tvMin.setText(String.format(java.util.Locale.US,"%.2f", ruta.getMin_price()));
        if(!ruta.getMin_price().equals(ruta.getMax_price())){
            tvMax.setText(String.format(java.util.Locale.US,"%.2f", ruta.getMax_price()));
            tvMax.setVisibility(View.VISIBLE);
            tvSeparator.setVisibility(View.VISIBLE);
        }

        fabMapa.setOnClickListener(view -> {;
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }
    private void populateRecycler(List<Estacion_ruta> lst){
        layoutManager = new LinearLayoutManager(getBaseContext());
        adapter = new EstacionRutaAdapter(lst, this);

        recyclerEstacionesRuta.setHasFixedSize(true);
        recyclerEstacionesRuta.setLayoutManager(layoutManager);
        recyclerEstacionesRuta.setAdapter(adapter);
    }
}