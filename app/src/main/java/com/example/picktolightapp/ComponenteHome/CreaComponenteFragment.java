package com.example.picktolightapp.ComponenteHome;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CreaComponenteFragment extends Fragment {

    Button btnChangeImg,btnAddModify;
    Uri imageUri;
    private ImageView imageView;
    Modalita modalita;
    EditText editTextId,editTextNome,editTextBarcode;

    enum Modalita {
        AGGIUNGI("Aggiungi"),
        MODIFICA("Modifica");

        private final String label;

        Modalita(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(imageView);
                } else {
                    Toast.makeText(getContext(), "Nessuna immagine selezionata", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.componente_visualize, container, false);

        btnChangeImg = view.findViewById(R.id.btnChangeImg);
        imageView = view.findViewById(R.id.imageView);
        btnAddModify = view.findViewById(R.id.btnAddModify);
        editTextId = view.findViewById(R.id.editTextId);
        editTextNome = view.findViewById(R.id.editTextNome);
        editTextBarcode = view.findViewById(R.id.editTextBarcode);

        if (GlobalVariables.getInstance().isComponenteNeedToCreate()) {
            modalita = Modalita.AGGIUNGI;
        } else {
            modalita = Modalita.MODIFICA;
            editTextId.setText(String.valueOf(GlobalVariables.getInstance().getComponenteToSee().getId()));
            editTextNome.setText(String.valueOf(GlobalVariables.getInstance().getComponenteToSee().getName()));
            editTextBarcode.setText(String.valueOf(GlobalVariables.getInstance().getComponenteToSee().getBarcode()));
            showComponenteImage(GlobalVariables.getInstance().getComponenteToSee(),imageView);
            editTextId.setEnabled(false);
        }

        if(modalita == Modalita.MODIFICA)
            MainActivity.setLogoInvisible();


        btnAddModify.setText(modalita.getLabel());

        btnChangeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_COMPONENT,true)) {
                    return;
                }
                openImageChooser();
            }
        });

        btnAddModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modalita == Modalita.AGGIUNGI) {
                    if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.CREATE_COMPONENT,true)) {
                        return;
                    }
                    handleAggiungi();
                }
                else if(modalita == Modalita.MODIFICA) {
                    if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.MODIFY_COMPONENT,true)) {
                        return;
                    }
                    Componente toModify = GlobalVariables.getInstance().getComponenteToSee();
                    handleModifica(toModify);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleAggiungi(){

        if (editTextId.getText().toString().isEmpty() ||
                editTextNome.getText().toString().isEmpty() ||
                editTextBarcode.getText().toString().isEmpty()) {

            DialogsHandler.showWarningDialog(requireContext(),getLayoutInflater(),"Errore","Per favore inserisci tutti i campi.",null);

            return;
        }


        int id = Integer.parseInt(editTextId.getText().toString());
        String name = editTextNome.getText().toString();
        String barcode = editTextBarcode.getText().toString();
        String imgUri = (imageUri != null) ? imageUri.toString() : "";

        List<Componente>  current = ComponenteTable.getAllComponenti(requireContext());

        for(Componente componente : current){
            if(componente.getId() == id){
                DialogsHandler.showErrorDialog(requireContext(),getLayoutInflater(),"Errore","Componente già presente con questo id.",null);
                return;
            }
        }

        Componente toCreate = new Componente(id,imgUri,name,barcode);
        ComponenteTable.addComponente(requireContext(),toCreate);
        DialogsHandler.showSuccessDialog(requireContext(),getLayoutInflater(),"Successo","Componente creato con successo!",null);
        editTextId.setText("");
        editTextNome.setText("");
        editTextBarcode.setText("");
        imageView.setImageDrawable(null);
    }

    private void handleModifica(Componente componente){

        if (editTextId.getText().toString().isEmpty() ||
                editTextNome.getText().toString().isEmpty() ||
                editTextBarcode.getText().toString().isEmpty()) {

            DialogsHandler.showWarningDialog(requireContext(),getLayoutInflater(),"Errore","Per favore inserisci tutti i campi.",null);

            return;
        }

        String name = editTextNome.getText().toString();
        String barcode = editTextBarcode.getText().toString();
        //String imgUri = (imageUri != null) ? imageUri.toString() : "";
        String imgUri = componente.getImg();
        String newImgUri = (imageUri != null) ? imageUri.toString() : "";
        if(!newImgUri.isEmpty()){
            imgUri = newImgUri;
        }

        componente.setName(name);
        componente.setBarcode(barcode);
        componente.setImg(imgUri);
        Componente toSet = new Componente(componente.getId(),imgUri,name,barcode);

        ComponenteTable.updateComponente(requireContext(),toSet);

        DialogsHandler.showSuccessDialog(requireContext(),getLayoutInflater(),"Successo","Componente modificato con successo!",null);

    }

    public void showComponenteImage(Componente componente, ImageView imageView) {
        String imageUri = componente.getImg();
        if (imageUri != null && !imageUri.isEmpty()) {
            Picasso.get().load(imageUri).into(imageView);
        } else {
            DialogsHandler.showWarningDialog(requireContext(),getLayoutInflater(),"Warning","Questo componente non ha una immagine",null);
        }
    }
}
