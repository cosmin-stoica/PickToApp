package com.example.picktolightapp.Settings.GestioneUtenti.CreaUtente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Event.EventWriter;
import com.example.picktolightapp.Model_DB.Event.TipoEvento;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.Model_DB.User.TipoUser;
import com.example.picktolightapp.Model_DB.User.UserTable;
import com.example.picktolightapp.R;

public class CreaUtenteFragment extends Fragment {

    Button submitButton;
    EditText username, password;
    TextView selectedItemText;


    public CreaUtenteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.crea_utente, container, false);

        MainActivity.setLogoInvisible();
        Spinner accountTypeSpinner = view.findViewById(R.id.account_type_spinner);
        String[] accountTypes = {"Guest", "Operatore", "Coord", "Admin"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                accountTypes
        );

        selectedItemText = view.findViewById(R.id.selected_item_text);
        username = view.findViewById(R.id.username_input);
        password = view.findViewById(R.id.password_input);
        submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.CREATE_USER,true)) {
                    return;
                }

                DialogsHandler.showConfirmDialog(
                        requireContext(),
                        inflater,
                        "Attenzione",
                        "Sei sicuro di voler creare un nuovo utente?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tryCreateAccount();
                            }
                        },
                        null
                );
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accountTypeSpinner.setAdapter(adapter);

        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccountType = accountTypes[position];
                selectedItemText.setText(selectedAccountType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void tryCreateAccount() {
        String usernameToAdd = username.getText().toString();
        String passToAdd = password.getText().toString();

        if(usernameToAdd.isEmpty() || passToAdd.isEmpty()){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Username o password vuoto/a",
                    null
            );
            return;
        }

        if(usernameToAdd.length() <= 2 || passToAdd.length() <= 2){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Username o password devono essere lunghi almeno 3 caratteri",
                    null
            );
            return;
        }

        TipoUser tipo = TipoUser.createTipoByString(selectedItemText.getText().toString());

        if(!UserTable.addUserIfNotExists(requireContext(),tipo, usernameToAdd,passToAdd)){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Errore nella creazione dell'utente",
                    null
            );
            return;
        }

        EventWriter eventWriter = EventWriter.getInstance(requireContext());
        eventWriter.logEvent(TipoEvento.INFO, "Aggiunto nuovo utente " + usernameToAdd + ", password: " + passToAdd);
    }

}
