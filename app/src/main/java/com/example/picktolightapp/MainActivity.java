package com.example.picktolightapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.picktolightapp.Model.User.UserTable;
import com.example.picktolightapp.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private boolean Dev = true; // Imposta a true per la modalità dev (skip Login e user root)
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemBars();

        // Manually find the toolbar using findViewById
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove the default title from the Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Find the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Naviga al frammento corretto in base al valore di Dev
            if (Dev) {
                navController.navigate(R.id.homepageFragment); // Naviga alla homepage
            } else {
                navController.navigate(R.id.loginFragment); // Naviga alla pagina di login
            }
        } else {
            Log.e("MainActivity", "NavHostFragment is null");
        }


        /*TEST*/
        dbHelper = new DatabaseHelper(this);

        // Aggiungi un nuovo utente
        UserTable.addUser(this, "John Doe", 30);

        // Ottieni tutti gli utenti
        List<String> users = UserTable.getAllUsers(this);
        for (String user : users) {
            Log.d("User Record", user);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Pianifica di nascondere le barre di sistema quando si ritorna a fuoco
            scheduleHideSystemBars();
        }
    }


    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.systemBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private final Runnable hideBarsRunnable = new Runnable() {
        @Override
        public void run() {
            hideSystemBars();
        }
    };

    private final Handler handler = new Handler();

    private void scheduleHideSystemBars() {
        handler.removeCallbacks(hideBarsRunnable);
        handler.postDelayed(hideBarsRunnable, 3000); // 3000 millisecondi = 3 secondi
    }
}
