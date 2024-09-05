package com.example.picktolightapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.picktolightapp.Connectivity.Server;
import com.example.picktolightapp.Model.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model.Event.EventWriter;
import com.example.picktolightapp.Model.Event.TipoEvento;
import com.example.picktolightapp.Model.Global.GlobalTable;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.Operation.OperationTable;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.Model.User.TipoUser;
import com.example.picktolightapp.Model.User.UserTable;
import com.example.picktolightapp.Toolbar.ButtonHandler;
import com.example.picktolightapp.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private boolean Dev = false; // Imposta a true per la modalità dev (skip Login e user root)
    private DatabaseHelper dbHelper;
    static ImageView logo;
    static LinearLayout back_button_layout;
    private View loadingView;
    static NavController navController;

    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String username = "";
        if (isRunningOnPC()) {
            ConfigManager configManager = new ConfigManager(this);
            Dev = configManager.getDevMode();

            //GlobalVariables.getInstance().setPort(configManager.getPort());
            //GlobalVariables.getInstance().setIP(configManager.getIp());
            username = configManager.getLogin();
        } else {
            Dev = false;
        }

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

        // Inflate il loader una volta e lo aggiungi al root layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadingView = inflater.inflate(R.layout.loading, null);
        FrameLayout rootLayout = findViewById(R.id.rootLayout); // Assicurati di avere un FrameLayout o simile nel tuo layout
        rootLayout.addView(loadingView);
        loadingView.setVisibility(View.GONE);

        // Find the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(@NonNull NavController controller,
                                                 @NonNull NavDestination destination,
                                                 @Nullable Bundle arguments) {
                    // Nascondi il loader una volta raggiunta la nuova destinazione
                    loadingView.setVisibility(View.GONE);
                }
            });

            // Avvia la navigazione iniziale in base alla modalità Dev
            if (Dev) {
                navController.navigate(R.id.homepageFragment);
            } else {
                navController.navigate(R.id.loginFragment);
            }
        } else {
            Log.e("MainActivity", "NavHostFragment is null");
        }


        CurrentUser currentUser = CurrentUser.getInstance();

        /*TEST*/
        dbHelper = new DatabaseHelper(this);
        dbHelper.refreshTables();
        initAllOperations();
        initAllPermissionsOperations();
        EventWriter eventWriter = EventWriter.getInstance(this);

        GlobalVariables.getInstance().setCurrentDispositivi(DispositivoTable.getAllDispositivi(this));
        GlobalVariables.getInstance().setPort(GlobalTable.getPort(this));
        GlobalVariables.getInstance().setIP(GlobalTable.getIp(this));

        // Aggiungi un nuovo utente admin se non esistente
        UserTable.addUserIfNotExists(this, TipoUser.ROOT,"Root", "scanteq");
        UserTable.addUserIfNotExists(this, TipoUser.OPERATORE,"OP1", "op1");
        UserTable.addUserIfNotExists(this, TipoUser.OPERATORE,"OP2", "op2");
        UserTable.addUserIfNotExists(this, TipoUser.OPERATORE,"OP3", "op3");
        UserTable.addUserIfNotExists(this, TipoUser.COORD,"Coord1", "coord1");
        UserTable.addUserIfNotExists(this, TipoUser.COORD,"Coord2", "coord2");
        UserTable.addUserIfNotExists(this, TipoUser.ADMIN,"Admin1", "admin1");

        //FORCE LOGIN
        if(Dev) {
            if(!Objects.equals(username, "")) {
                if (UserTable.isUserOnDB(this, username)) {
                    currentUser.setUsername(username);
                    currentUser.setPassword(UserTable.getPasswordbyUser(this, username));
                    currentUser.setTipo(UserTable.getTipoUserbyUser(this, username));
                }
                else{
                    DialogsHandler.showErrorDialog(
                            this,
                            getLayoutInflater(),
                            "ERRORE",
                            "Attenzione, l'utente selezionato dal config non esiste, il programma crasherà.",
                            null
                    );
                }
            }
            else {
                currentUser.setTipo(TipoUser.ROOT);
                currentUser.setUsername("Root");
                currentUser.setPassword("scanteq");
            }
            eventWriter.logEvent(TipoEvento.INFO, "Logging automatico come " + currentUser.getUsername());
            GlobalVariables.getInstance().setUserToSee(currentUser);
        }

        /*SETTINGS*/

        String [] settingsOptions = getResources().getStringArray(R.array.settings_options);
        GlobalVariables.getInstance().setLastUserManagementOptions(settingsOptions);
        GlobalVariables.getInstance().setLastOptionGroupName("Main");

        ImageButton settingsBtn = findViewById(R.id.settingsBtn);
        ImageButton notificheBtn = findViewById(R.id.notificheBtn);
        TextView accountBtn = findViewById(R.id.accountBtn);
        logo = findViewById(R.id.left_logo);
        back_button_layout = findViewById(R.id.back_button_layout);

        ButtonHandler buttonHandler = new ButtonHandler(this);
        buttonHandler.setButtonListeners(settingsBtn, notificheBtn, accountBtn, logo, back_button_layout);

        startServer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stopServer();
            Log.d("MAIN", "Server fermato.");
        }
    }
    

    public static void setLogoVisible(){
        logo.setVisibility(View.VISIBLE);
        back_button_layout.setVisibility(View.INVISIBLE);
    }

    public static void setLogoInvisible(){
        logo.setVisibility(View.INVISIBLE);
        back_button_layout.setVisibility(View.VISIBLE);
    }

    public void showLoader() {
        if (loadingView != null)
            loadingView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
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


    private void initAllOperations() {
        OperationTable.clearAll(this);
        OperationTable.addAllOperations(this);

    }

    private void initAllPermissionsOperations(){
        PermissionOperationsTable.clearAll(this);
        for (Operation op : Operation.values()) {
            PermissionOperationsTable.addPermissionOperation(this,"Root",op);
        }
    }

    public static void goToLogin(){
        navController.navigate(R.id.action_to_LoginFragment);
    }

    private boolean startServer() {
        int port = 8010;
        server = Server.getInstance(port,this);

        try {
            server.startServer();
            return true;
        } catch (Exception e) {
            Log.e("MAIN", "Errore durante l'avvio del server: " + e.getMessage());
            DialogsHandler.showErrorDialog(
                    this,
                    getLayoutInflater(),
                    "Errore",
                    "Server non inizializzato " + e.getMessage(),
                    null
            );
            return false;
        }
    }

    private boolean isRunningOnPC() {
        return "robolectric".equals(Build.FINGERPRINT)
                || System.getProperty("os.name").toLowerCase().contains("windows")
                || System.getProperty("os.name").toLowerCase().contains("mac")
                || System.getProperty("os.name").toLowerCase().contains("nux");
    }

}
