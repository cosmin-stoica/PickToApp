package com.example.picktolightapp.Settings.GestioneUtenti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.Event.EventWriter;
import com.example.picktolightapp.Model.Event.TipoEvento;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.Model.User.User;
import com.example.picktolightapp.Model.User.UserTable;
import com.example.picktolightapp.R;

import java.util.ArrayList;
import java.util.List;

public class GestioneUtentiFragment extends Fragment {

    private TableLayout userTableLayout;
    private ArrayList<User> selectedUsers = new ArrayList<>();
    Button visualizzaUserBtn;
    Button eliminaUserBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gestione_utenti, container, false);

        MainActivity.setLogoInvisible();
        NavController navController = NavHostFragment.findNavController(this);


        //LinearLayout indietroBtn = view.findViewById(R.id.back_button_layout);


        userTableLayout = view.findViewById(R.id.userTableLayout);
        visualizzaUserBtn = view.findViewById(R.id.visualizeUserBtn);
        eliminaUserBtn = view.findViewById(R.id.eliminaUserBtn);

        updateUserTable();

        Button visualizzaUtenti = view.findViewById(R.id.visualizeUsers);
        Button eliminaUtenti = view.findViewById(R.id.deleteUsers);

        visualizzaUtenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserTable();
            }
        });

        visualizzaUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_USERS,true)) {
                    return;
                }

                if(selectedUsers.size() > 1){
                    DialogsHandler.showErrorDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Errore",
                            "Puoi visualizzare solo un utente alla volta",
                            null
                    );
                }
                else{
                    showUserStats(selectedUsers.get(0));
                }

            }
        });

        eliminaUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DELETE_USERS,true)) {
                    return;
                }

                DialogsHandler.showConfirmDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Conferma",
                        "Sei sicuro di voler eliminare gli utenti selezionati?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminateUsers(selectedUsers);
                                selectedUsers.clear();
                                updateUserTable();
                                EventWriter eventWriter = EventWriter.getInstance(requireContext());
                                eventWriter.logEvent(TipoEvento.INFO, "Eliminati alcuni utenti dal db");
                            }
                        },
                        null
                );

            }
        });

        eliminaUtenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DELETE_USERS,true)) {
                    return;
                }

                DialogsHandler.showConfirmDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Conferma",
                        "Sei sicuro di voler eliminare tutti gli utenti? L'azione è irreversibile",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserTable.clearAll(requireContext());
                                updateUserTable();
                                EventWriter eventWriter = EventWriter.getInstance(requireContext());
                                eventWriter.logEvent(TipoEvento.INFO, "Eliminati tutti gli utenti dal db");
                            }
                        },
                        null
                );
            }
        });

        return view;
    }

    private void updateUserTable() {
        if (userTableLayout != null) {
            userTableLayout.removeViews(1, userTableLayout.getChildCount() - 1);  // Rimuove tutte le righe tranne la prima (intestazioni)

            List<User> users = UserTable.getAllUsers(requireContext());

            boolean isFirstRow = true;  // Variabile per tracciare la prima riga

            for (User user : users) {
                TableRow row = new TableRow(requireContext());

                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            if (!selectedUsers.contains(user)) {
                                selectedUsers.add(user);
                            }
                        } else {
                            selectedUsers.remove(user);
                        }
                    }
                });

                TextView tipoView = new TextView(requireContext());
                tipoView.setText(user.getTipo().getCodiceString());
                tipoView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.15f));

                TextView usernameView = new TextView(requireContext());
                usernameView.setText(user.getUsername());
                usernameView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));

                TextView passwordView = new TextView(requireContext());
                passwordView.setText(user.getPassword());
                passwordView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                row.addView(checkBox);
                row.addView(tipoView);
                row.addView(usernameView);
                row.addView(passwordView);


                // Aggiungi una View per il bordo nero sotto la prima riga
                if (isFirstRow) {
                    View borderView = new View(requireContext());
                    borderView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                    borderView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    userTableLayout.addView(borderView);

                    isFirstRow = false;
                }

                userTableLayout.addView(row);

            }
        }
    }


    private void clearUserTable() {
        if (userTableLayout != null) {
            userTableLayout.removeViews(1, userTableLayout.getChildCount() - 1);  // Rimuove tutte le righe tranne la prima (intestazioni)
        }
    }

    private boolean eliminateUsers(List<User> list){

        for (User user : list){
            if(!UserTable.deleteUser(requireContext(),user.getUsername()))
                return false;
        }

        return true;

    }

    private void showUserStats(User user){
        GlobalVariables.getInstance().setUserToSee(user);
        GlobalVariables.getInstance().setLastDestinationId(R.id.gestioneUtentiFragment);
        NavController navController = NavHostFragment.findNavController(GestioneUtentiFragment.this);
        navController.navigate(R.id.action_to_UserPageFragment);
    }

}
