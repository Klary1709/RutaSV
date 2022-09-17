package com.svrutas.app.ui.estaciones;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.svrutas.app.R;
import com.svrutas.app.data.Estacion;
import com.svrutas.app.data.Ruta;
import com.svrutas.app.databinding.FragmentEstacionesBinding;
import com.svrutas.app.ui.rutas.RutaAdapter;

import java.util.ArrayList;
import java.util.List;

public class EstacionesFragment extends Fragment {

    private FragmentEstacionesBinding binding;

    private String estacionesCollection = "estaciones";
    private FirebaseFirestore db;

    private List<Estacion> listaEstaciones = new ArrayList<>();
    private RecyclerView recyclerEstaciones;
    private EstacionAdapter adapter = new EstacionAdapter(listaEstaciones, this);
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEstacionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        recyclerEstaciones = binding.recyclerEstaciones;
        getRutas();

        setHasOptionsMenu(true);

        return root;
    }

    private void getRutas(){
        db.collection(estacionesCollection)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Estacion estacion = document.toObject(Estacion.class);
                               estacion.setId(document.getId());
                               listaEstaciones.add(estacion);
                           }
                           populateRecycler();
                       } else {
                           Log.w("ESTACIONES", "Error getting documents.", task.getException());
                       }
                   }
               }
            );
    }
    private void populateRecycler(){
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        adapter = new EstacionAdapter(listaEstaciones, this);

        recyclerEstaciones.setHasFixedSize(true);
        recyclerEstaciones.setLayoutManager(layoutManager);
        recyclerEstaciones.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_buscar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}