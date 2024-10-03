package com.example.picktolightapp.ComponenteHome;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.R;
import com.squareup.picasso.Picasso;

public class CaricaImmaginiFragment extends Fragment {

    private ImageView imageView;
    private Button selectImageButton,caricaImageButton;
    Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(imageView);
                    caricaImageButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Nessuna immagine selezionata", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.carica_immagini, container, false);

        imageView = view.findViewById(R.id.imageView);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        caricaImageButton = view.findViewById(R.id.caricaImageButton);

        selectImageButton.setOnClickListener(v -> openImageChooser());
        caricaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCaricaImageButton();
            }
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleCaricaImageButton(){
        DialogsHandler.showConfirmDialog(
                requireContext(),
                getLayoutInflater(),
                "Conferma",
                "Sei sicuro di voler eliminare il dispositivo? L'azione è irreversibile",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realHandleCaricaImageButton();
                    }
                },
                null
        );
    }

    private void realHandleCaricaImageButton(){

    }
}
