package com.example.picktolightapp.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Event.EventWriter;
import com.example.picktolightapp.Model_DB.Event.TipoEvento;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.Model_DB.User.TipoUser;
import com.example.picktolightapp.Model_DB.User.UserTable;
import com.example.picktolightapp.R;
import com.example.picktolightapp.Toolbar.ButtonHandler;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Usa il layout di login.xml per questo Fragment
        View view = inflater.inflate(R.layout.login, container, false);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        EventWriter eventWriter = EventWriter.getInstance(requireContext());

        // Inizializza le viste
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);

        // Configura l'azione del pulsante di login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    //Toast.makeText(requireContext(), "Inserisci username e password", Toast.LENGTH_SHORT).show();
                    DialogsHandler.showWarningDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Warning",
                            "Inserisci username e password",
                            null
                    );
                } else {

                    if(!UserTable.isUserOnDB(requireContext(),username)){
                        //Toast.makeText(requireContext(), "Username o password sbagliato/a", Toast.LENGTH_SHORT).show();
                        DialogsHandler.showErrorDialog(
                                requireContext(),
                                getLayoutInflater(),
                                "Error",
                                "Username o password sbagliato/a",
                                null
                        );
                        return;
                    }
                    TipoUser tipoUser = UserTable.getTipoUserbyUser(requireContext(), username);

                    //Toast.makeText(requireContext(), "Login eseguito", Toast.LENGTH_SHORT).show();
                    DialogsHandler.showSuccessDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Successo!",
                            "Benvenuto " +username,
                            null
                    );
                    eventWriter.logEvent(TipoEvento.INFO, "Logging con username " + username + ", password " + password + " e tipo " + tipoUser.getCodiceString());

                    CurrentUser currentUser = CurrentUser.getInstance();
                    currentUser.setUsername(username);
                    currentUser.setPassword(password);
                    currentUser.setTipo(UserTable.getTipoUserbyUser(requireContext(),username));
                    GlobalVariables.getInstance().setUserToSee(currentUser);
                    ButtonHandler.handleAggiornaToolbar();
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_loginFragment_to_homepageFragment);
                }
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Mostra la Toolbar quando si esce dal LoginFragment
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }
}
