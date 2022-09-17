package com.svrutas.app.ui.rutas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.svrutas.app.data.Ruta;
import com.svrutas.app.databinding.FragmentRutasBinding;

import java.util.ArrayList;
import java.util.List;

public class RutasFragment extends Fragment {
    private FragmentRutasBinding binding;

    private FirebaseFirestore db;
    private final String rutasCollection = "rutas";

    private List<Ruta> listaRutas = new ArrayList<>();
    private RecyclerView recyclerRutas;
    private RutaAdapter adapter = new RutaAdapter(listaRutas, this);
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRutasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        recyclerRutas = binding.recyclerRutas;
        getRutas();

        setHasOptionsMenu(true);

        return root;
    }

    private void getRutas(){
        db.collection(rutasCollection)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Ruta ruta = document.toObject(Ruta.class);
                               ruta.setId(document.getId());
                               listaRutas.add(ruta);
                           }
                           populateRecycler();
                       } else {
                           Log.w("RUTAS", "Error getting documents.", task.getException());
                       }
                   }
               }
            );
    }
    private void populateRecycler(){
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        adapter = new RutaAdapter(listaRutas, this);

        recyclerRutas.setHasFixedSize(true);
        recyclerRutas.setLayoutManager(layoutManager);
        recyclerRutas.setAdapter(adapter);
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