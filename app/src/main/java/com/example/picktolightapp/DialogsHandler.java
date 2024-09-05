package com.example.picktolightapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogsHandler {

    public static void showConfirmDialog(Context context, LayoutInflater inflater, String title, String message,
                                  View.OnClickListener confirmListener,
                                  View.OnClickListener cancelListener) {
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        btnConfirm.setOnClickListener(confirmListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.onClick(v); // Esegui l'azione di conferma
                alertDialog.dismiss();      // Chiudi il dialogo
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();  // Default action: just dismiss
            }
        });


        btnCancel.setOnClickListener(cancelListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelListener.onClick(v); // Esegui l'azione di conferma
                alertDialog.dismiss();      // Chiudi il dialogo
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();  // Default action: just dismiss
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static void showWarningDialog(Context context, LayoutInflater inflater, String title, String message,
                                         View.OnClickListener confirmListener) {
        View dialogView = inflater.inflate(R.layout.dialog_warning, null);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        btnConfirm.setOnClickListener(confirmListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.onClick(v); // Esegui l'azione di conferma
                alertDialog.dismiss();      // Chiudi il dialogo
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();  // Default action: just dismiss
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public static void showErrorDialog(Context context, LayoutInflater inflater, String title, String message,
                                       View.OnClickListener confirmListener) {
        // Inflate the error dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_error, null);

        // Create the AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Initialize the views from the dialog layout
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);

        // Set the title and message
        tvTitle.setText(title);
        tvMessage.setText(message);

        // Set the click listener for the "OK" button
        btnConfirm.setOnClickListener(confirmListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.onClick(v); // Execute the confirm action
                alertDialog.dismiss();      // Dismiss the dialog
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();  // Default action: just dismiss
            }
        });

        // Show the dialog
        alertDialog.show();

        // Set the dialog size
        alertDialog.getWindow().setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public static void showChangeValueDialog(Context context, LayoutInflater inflater, String titleText, String editPrev,
                                             View.OnClickListener saveListener, View.OnClickListener cancelListener) {
        // Crea il dialog
        View dialogView = inflater.inflate(R.layout.changevalue, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(dialogView);

        // Inizializza le viste del dialog
        TextView title = dialogView.findViewById(R.id.title);
        EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);

        // Imposta il titolo e il suggerimento del campo di input
        title.setText(titleText);
        editTextInput.setHint(editPrev);

        // Crea il dialogo
        AlertDialog dialog = builder.create();

        // Imposta il listener per il pulsante "Cancel"
        buttonCancel.setOnClickListener(cancelListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelListener.onClick(v);
                dialog.dismiss(); // Chiudi il dialogo
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Chiudi il dialogo di default
            }
        });

        // Imposta il listener per il pulsante "Save"
        buttonSave.setOnClickListener(saveListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveListener.onClick(dialogView); // Passa il dialogView per trovare il campo
                dialog.dismiss(); // Chiudi il dialogo
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newValue = editTextInput.getText().toString().trim();
                if (!newValue.isEmpty()) {
                    dialog.dismiss(); // Chiudi il dialogo se non vuoto
                } else {
                    editTextInput.setError("Questo campo non può essere vuoto");
                }
            }
        });

        // Mostra il dialog
        dialog.show();
        dialog.getWindow().setLayout(900, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static void showSuccessDialog(Context context, LayoutInflater inflater, String title, String message,
                                         View.OnClickListener confirmListener) {
        // Crea il dialog
        View dialogView = inflater.inflate(R.layout.dialog_success, null); // Assicurati che il nome del layout sia corretto
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(dialogView);

        // Inizializza le viste del dialog
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        // Imposta il titolo e il messaggio
        tvTitle.setText(title);
        tvMessage.setText(message);

        // Imposta il listener per il pulsante "OK"
        btnConfirm.setOnClickListener(confirmListener != null ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.onClick(v); // Esegui l'azione di conferma
                AlertDialog dialog = (AlertDialog) v.getTag(); // Recupera il dialogo dal tag
                if (dialog != null) {
                    dialog.dismiss(); // Chiudi il dialogo
                }
            }
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (AlertDialog) v.getTag(); // Recupera il dialogo dal tag
                if (dialog != null) {
                    dialog.dismiss(); // Chiudi il dialogo
                }
            }
        });

        // Crea e mostra il dialogo
        AlertDialog alertDialog = builder.create();
        btnConfirm.setTag(alertDialog); // Imposta il dialogo come tag sul pulsante per il dismiss
        alertDialog.show();
        alertDialog.getWindow().setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
