package com.svrutas.app;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.svrutas.app.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    public static String KEY_DATA_RUTA = "KEY_DATA_RUTA";
    public static String KEY_RUTA_ID = "KEY_RUTA_ID";
    public static String KEY_ESTACION_ID = "KEY_ESTACION_ID";

    private int exit_count = 0;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadNavigation(binding);

        binding.navView.setOnNavigationItemSelectedListener(item -> { //captura el evento cuando se selecciona una pestaña
            if(item.getItemId() != binding.navView.getSelectedItemId()){ //si no es la misma pestaña
                NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
                NavigationUI.onNavDestinationSelected(item, navHost.getNavController()); //sigue el flujo normal del navigation
            }
            return true;
        });
    }

    private void loadNavigation(ActivityHomeBinding binding){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.navigation_mapa, R.id.navigation_rutas, R.id.navigation_estaciones)
                .build();
        setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(binding.navView, navController);
    }
}