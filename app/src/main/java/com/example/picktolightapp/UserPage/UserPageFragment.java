package com.example.picktolightapp.UserPage;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Login.HandleAccount;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.Model.User.User;
import com.example.picktolightapp.Model.User.UserTable;
import com.example.picktolightapp.R;

public class UserPageFragment extends Fragment {


    public UserPageFragment() {
    }

    Button modificaUsernameBtn;
    Button modificaPasswordBtn;
    Button vediUtentiBtn;
    Button logoutBtn;
    TextView codiceOperatore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.userpage, container, false);

        TextView profileInitial = view.findViewById(R.id.profile_initial);
        codiceOperatore = view.findViewById(R.id.codiceOperatore);
        TextView tipoOperatore = view.findViewById(R.id.tipoOperatore);

        User userToSee = GlobalVariables.getInstance().getUserToSee();
        char Initial = userToSee.getUsername().charAt(0);
        profileInitial.setText(String.valueOf(Initial));
        codiceOperatore.setText(userToSee.getUsername());
        tipoOperatore.setText(userToSee.getTipo().getCodiceString());

        modificaUsernameBtn = view.findViewById(R.id.modificaUsername);
        modificaPasswordBtn = view.findViewById(R.id.modificaPassword);

        logoutBtn = view.findViewById(R.id.logoutBtn);
        if(!isCurrentUser(userToSee)){
            logoutBtn.setVisibility(View.INVISIBLE);
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandleAccount.logoutUser(requireContext());
            }
        });

        modificaUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_OWN_USERNAME,true)) {
                    return;
                }

                DialogsHandler.showChangeValueDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Modifica Username",
                        "Inserisci il nuovo username",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newValue = ((EditText) ((View) v.getParent()).findViewById(R.id.editTextInput)).getText().toString().trim();
                                if (UserTable.changeUsernameByUsername(requireContext(), (String) codiceOperatore.getText(), newValue)) {

                                    if(userToSee.getUsername().equals(CurrentUser.getInstance().getUsername())){
                                        CurrentUser.getInstance().setUsername(newValue);
                                    }

                                    userToSee.setUsername(newValue);
                                    codiceOperatore.setText(userToSee.getUsername());
                                    char Initial = userToSee.getUsername().charAt(0);
                                    profileInitial.setText(String.valueOf(Initial));

                                    DialogsHandler.showSuccessDialog(
                                            requireContext(),
                                            getLayoutInflater(),
                                            "Successo",
                                            "Username cambiato con successo",
                                            null
                                    );
                                } else {
                                    DialogsHandler.showErrorDialog(
                                            requireContext(),
                                            getLayoutInflater(),
                                            "Errore",
                                            "Errore nel cambiamento dell'username",
                                            null
                                    );
                                }
                            }
                        },
                        null
                );
            }
        });

        modificaPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_OWN_PASSWORD,true)) {
                    return;
                }

                DialogsHandler.showChangeValueDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Modifica Password",
                        "Inserisci una nuova password",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newValue = ((EditText) ((View) v.getParent()).findViewById(R.id.editTextInput)).getText().toString().trim();
                                if (UserTable.changePasswordByUsername(requireContext(), (String) codiceOperatore.getText(), newValue)) {
                                    userToSee.setPassword(newValue);

                                    if(userToSee.getUsername().equals(CurrentUser.getInstance().getUsername())){
                                        CurrentUser.getInstance().setPassword(newValue);
                                    }

                                    DialogsHandler.showSuccessDialog(
                                            requireContext(),
                                            getLayoutInflater(),
                                            "Successo",
                                            "Password cambiata con successo",
                                            null
                                    );
                                } else {
                                    DialogsHandler.showErrorDialog(
                                            requireContext(),
                                            getLayoutInflater(),
                                            "Errore",
                                            "Errore nel cambiamento della password",
                                            null
                                    );
                                }
                            }
                        },
                        null
                );
            }
        });

        vediUtentiBtn = view.findViewById(R.id.vediUtenti);

        //if(isCurrentUser(userToSee)){
        vediUtentiBtn.setVisibility(View.GONE);
        //}

        vediUtentiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.getInstance().setUserToSee(CurrentUser.getInstance());
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showLoader();
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.getInstance().setLastDestinationId(R.id.settingsFragment);
                        NavController navController = NavHostFragment.findNavController(UserPageFragment.this);
                        navController.navigate(R.id.action_to_GestioneUtentiFragment);
                    }
                }, 1000); // Ritarda di 200 millisecondi
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isCurrentUser(User user){
        if(user.getUsername().equals(CurrentUser.getInstance().getUsername())){
            return true;
        }
        else return false;
    }

}
