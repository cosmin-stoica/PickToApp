package com.example.picktolightapp.Live;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;
import com.example.picktolightapp.UserPage.UserPageFragment;
import com.squareup.picasso.Picasso;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;



public class LiveVisualizeFragment extends Fragment {

    GridLayout gridLayout;
    private Dispositivo dispositivo;
    private ImageView imageView,qrCodeImageView,imageViewTopRight;
    private TextView textViewNameComponent, textViewQty;
    private Button btnAddQty, btnRemoveQty, btnSeeHistory;
    private CardView qrCodeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivo_live_visualize, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dispositivo = GlobalVariables.getInstance().getDispositivoToSee();

        imageView = view.findViewById(R.id.imageView);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
        imageViewTopRight = view.findViewById(R.id.imageViewTopRight);
        textViewNameComponent = view.findViewById(R.id.textViewNameComponent);
        qrCodeContainer = view.findViewById(R.id.qrCodeContainer);

        btnAddQty = view.findViewById(R.id.btnAddQty);
        btnRemoveQty = view.findViewById(R.id.btnRemoveQty);
        btnSeeHistory = view.findViewById(R.id.btnSeeHistory);

        btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.ADD_DISP_QTY, true)) {
                    return;
                }
                handleAddQty();
            }
        });

        btnRemoveQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.REMOVE_DISP_QTY, true)) {
                    return;
                }
                handleRemoveQty();
            }
        });

        btnSeeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariables.getInstance().setbSeeDispositivoLog(true);
                GlobalVariables.getInstance().setDispositivoIDToSeeLog(dispositivo.getId());
                GlobalVariables.getInstance().setLastDestinationId(R.id.LiveVisualizeFragment);
                NavController navController = NavHostFragment.findNavController(LiveVisualizeFragment.this);
                navController.navigate(R.id.action_to_LogFragment);
            }
        });

        textViewQty = view.findViewById(R.id.textViewQty);

        if(dispositivo.getComponente() != null && dispositivo.getComponente().getId() != -1) {
            textViewNameComponent.setText(dispositivo.getComponente().getName());
            generateQRCode(dispositivo.getComponente().getBarcode());
        }else{
            textViewNameComponent.setText("Dispositivo " + String.valueOf(dispositivo.getId()));
            qrCodeImageView.setVisibility(View.GONE);
            qrCodeContainer.setVisibility(View.GONE);
        }

        handleUpdateValues();

        showDispositivoImage(dispositivo,imageView);
    }

    private void showDispositivoImage(Dispositivo dispositivo, ImageView imageView) {
        if(dispositivo.getComponente() != null) {
            String imageUri = dispositivo.getComponente().getImg();
            if (imageUri != null && !imageUri.isEmpty()) {
                Picasso.get().load(imageUri).into(imageView);
            } else {
                DialogsHandler.showWarningDialog(requireContext(), getLayoutInflater(), "Warning", "Questo componente non ha una immagine", null);
            }
        }
    }

    private void generateQRCode(String text) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddQty(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.changevalue,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);


        EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
        editTextInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("Aggiungi quantità");
        buttonSave.setText("Aggiungi");

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonSave.setOnClickListener(v -> {
            String newValue = editTextInput.getText().toString();
            int newQty = dispositivo.getQty() + Integer.parseInt(newValue);
            dispositivo.setQty(newQty);
            DispositivoTable.updateQtyById(requireContext(),dispositivo.getId(),newQty);
            handleUpdateValues();
            dialog.dismiss();

            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            dispositivo.getId(),
                            "Aggiunto " + newValue + " quantità al dispositivo")
            );
        });

        dialog.show();
    }

    private void handleRemoveQty(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.changevalue,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);


        EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
        editTextInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("Rimuovi quantità");
        buttonSave.setText("Rimuovi");

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonSave.setOnClickListener(v -> {
            String newValue = editTextInput.getText().toString();
            int newQty = dispositivo.getQty() - Integer.parseInt(newValue);

            if(newQty<0){
                DialogsHandler.showErrorDialog(requireContext(),getLayoutInflater(),"Errore", "Hai rimosso più della quantità disponibile, disponibile: " + dispositivo.getQty() + ", da rimuovere: " +  Integer.parseInt(newValue),null );
                return;
            }
            dispositivo.setQty(newQty);
            DispositivoTable.updateQtyById(requireContext(),dispositivo.getId(),newQty);
            handleUpdateValues();
            dialog.dismiss();

            DispositivoStoricoTable.addDispositivoStorico(
                    requireContext(),
                    new DispositivoStorico(
                            dispositivo.getId(),
                            "Rimosso " + newValue + " quantità al dispositivo")
            );
        });

        dialog.show();
    }

    private void handleUpdateValues(){
        textViewQty.setText(String.valueOf(dispositivo.getQty()) + " Quantità");
        if(dispositivo.getQty() <= dispositivo.getQty_min()){
            imageViewTopRight.setVisibility(View.VISIBLE);
        }
        else{
            imageViewTopRight.setVisibility(View.GONE);
        }
    }
}