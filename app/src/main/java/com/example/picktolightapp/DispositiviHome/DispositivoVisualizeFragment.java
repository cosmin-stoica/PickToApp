package com.example.picktolightapp.DispositiviHome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.Model_DB.DispositiviComponenti.DispositiviComponentiTable;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DispositivoVisualizeFragment extends Fragment {

    EditText editTextComponente, editTextQTY, editTextQTYMin;
    Modalita modalita;
    Spinner spinnerComponente;
    private ImageView imageView;
    String[] componentiString;

    enum Modalita {
        AGGIUNGI("Aggiungi"), MODIFICA("Modifica");

        private final String label;

        Modalita(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivo_visualize, container, false);

        editTextComponente = view.findViewById(R.id.editTextComponente);
        editTextQTY = view.findViewById(R.id.editTextQTY);
        editTextQTYMin = view.findViewById(R.id.editTextQTYMin);
        spinnerComponente = view.findViewById(R.id.spinnerComponente);
        imageView = view.findViewById(R.id.imageView);

        List<Componente> componentiList = ComponenteTable.getAllComponenti(requireContext());
        componentiString = new String[componentiList.size() + 1];
        componentiString[0] = "none";

        for (int i = 0; i < componentiList.size(); i++) {
            componentiString[i + 1] = componentiList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, componentiString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComponente.setAdapter(adapter);


        Button btnSubmit = view.findViewById(R.id.btnAddModify);


        if (GlobalVariables.getInstance().isDispositivoNeedToCreate()) {
            modalita = Modalita.AGGIUNGI;
        } else {
            GlobalVariables.getInstance().setDispositivoToSee(DispositivoTable.getDispositivoById(requireContext(), GlobalVariables.getInstance().getDispositivoToSee().getId()));
            modalita = Modalita.MODIFICA;
            editTextComponente.setText(String.valueOf(GlobalVariables.getInstance().getDispositivoToSee().getId()));
            editTextQTY.setText(String.valueOf(GlobalVariables.getInstance().getDispositivoToSee().getQty()));
            editTextQTYMin.setText(String.valueOf(GlobalVariables.getInstance().getDispositivoToSee().getQty_min()));
            editTextComponente.setEnabled(false);
            Log.d("DISPOSITIVO", GlobalVariables.getInstance().getDispositivoToSee().toString());
            showDispositivoImage(GlobalVariables.getInstance().getDispositivoToSee(), imageView);
            setSelectedOption(GlobalVariables.getInstance().getDispositivoToSee());
        }

        btnSubmit.setText(modalita.getLabel());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (modalita == Modalita.MODIFICA) {
                    if (!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_DISP, true)) {
                        return;
                    }
                    handleModifica(GlobalVariables.getInstance().getDispositivoToSee());
                } else if (modalita == Modalita.AGGIUNGI) {
                    if (!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.CREATE_DISP, true)) {
                        return;
                    }
                    handleAggiungi();
                }

            }
        });

        if (modalita == Modalita.MODIFICA) MainActivity.setLogoInvisible();

        return view;
    }

    private void handleAggiungi() {

        if (editTextComponente.getText().toString().isEmpty() || editTextQTY.getText().toString().isEmpty() || editTextQTYMin.getText().toString().isEmpty()) {

            DialogsHandler.showWarningDialog(requireContext(), getLayoutInflater(), "Errore", "Per favore inserisci tutti i campi.", null);

            return;
        }


        int id = Integer.parseInt(editTextComponente.getText().toString());
        int qty = Integer.parseInt(editTextQTY.getText().toString());
        int qtyMin = Integer.parseInt(editTextQTYMin.getText().toString());
        Dispositivo toCreate = new Dispositivo(id, qty, qtyMin);
        Componente componente = null;

        String componenteName = spinnerComponente.getSelectedItem().toString();
        if (componenteName != "none") {
            componente = ComponenteTable.getComponenteByName(requireContext(), componenteName);
            toCreate.setComponente(componente);
        }

        List<Dispositivo> currentDispositivi = GlobalVariables.getInstance().getCurrentDispositivi();

        for (Dispositivo dispositivo : currentDispositivi) {
            if (dispositivo.getId() == id) {
                DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Dispositivo già presente con questo id.", null);
                return;
            }
        }

        GlobalVariables.getInstance().getCurrentDispositivi().add(toCreate);
        DispositivoTable.addDispositivo(requireContext(), toCreate);
        DialogsHandler.showSuccessDialog(requireContext(), getLayoutInflater(), "Successo", "Dispositivo creato con successo!", null);
        editTextComponente.setText("");
        editTextQTY.setText("");
        editTextQTYMin.setText("");

        if (componente != null)
            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            id,
                            "Creato un nuovo dispositivo con QTY " + qty + ",QTY_MIN: " + qtyMin + ", Componente: " + componente.getName())
            );
        else {
            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            id,
                            "Creato un nuovo dispositivo con QTY " + qty + ",QTY_MIN: " + qtyMin + ", Componente: none")
            );
        }

    }

    private void handleModifica(Dispositivo dispositivo) {

        if (editTextComponente.getText().toString().isEmpty() || editTextQTY.getText().toString().isEmpty() || editTextQTYMin.getText().toString().isEmpty()) {

            DialogsHandler.showWarningDialog(requireContext(), getLayoutInflater(), "Errore", "Per favore inserisci tutti i campi.", null);

            return;
        }

        int qty = Integer.parseInt(editTextQTY.getText().toString());
        int qtyMin = Integer.parseInt(editTextQTYMin.getText().toString());
        int prevQty = dispositivo.getQty();
        int prevQtyMin = dispositivo.getQty_min();
        Componente prevComponente = dispositivo.getComponente();
        Componente componente = null;

        String componenteName = spinnerComponente.getSelectedItem().toString();
        if (componenteName != "none") {
            componente = ComponenteTable.getComponenteByName(requireContext(), componenteName);
            dispositivo.setComponente(componente);
        }
        dispositivo.setQty(qty);
        dispositivo.setQty_min(qtyMin);

        DispositivoTable.updateQtyById(requireContext(), dispositivo.getId(), qty);
        DispositivoTable.updateQtyMinById(requireContext(), dispositivo.getId(), qtyMin);
        if (componente != null)
            DispositivoTable.updateComponenteById(requireContext(), dispositivo.getId(), componente.getId());

        DialogsHandler.showSuccessDialog(requireContext(), getLayoutInflater(), "Successo", "Dispositivo modificato con successo!", null);

        if (componente != null && prevComponente != null) {
            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            dispositivo.getId(),
                            "Dispositivo modificato con nuovi valori: " + qty + " Prev: " + prevQty + ",QTY_MIN: " + qtyMin + " Prev: " + prevQtyMin + ", Componente: " + componente.getName() + " Prev: " + prevComponente.getName())
            );
        } else {
            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            dispositivo.getId(),
                            "Dispositivo modificato con nuovi valori: " + qty + " Prev: " + prevQty + ",QTY_MIN: " + qtyMin + " Prev: " + prevQtyMin + ", Componente: none")
            );
        }

    }

    private void showDispositivoImage(Dispositivo dispositivo, ImageView imageView) {
        if (dispositivo.getComponente() != null) {
            String imageUri = dispositivo.getComponente().getImg();
            if (imageUri != null && !imageUri.isEmpty()) {
                Picasso.get().load(imageUri).into(imageView);
            } else {
                DialogsHandler.showWarningDialog(requireContext(), getLayoutInflater(), "Warning", "Questo componente non ha una immagine", null);
            }
        }
    }

    private void setSelectedOption(Dispositivo dispositivoToSee) {
        if (dispositivoToSee.getComponente() == null || dispositivoToSee.getComponente().getName().isEmpty()) {
            DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Il dispositivo non ha associato un componente", null);
            return;
        }

        String componenteName = dispositivoToSee.getComponente().getName();
        int index = -1;
        for (int i = 0; i < componentiString.length; i++) {
            if (componentiString[i].equals(componenteName)) index = i;
        }

        if (index != -1) {
            spinnerComponente.setSelection(index);
        } else {
            DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Il dispositivo non ha associato un componente", null);
        }
    }


}
