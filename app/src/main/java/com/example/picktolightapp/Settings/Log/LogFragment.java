package com.example.picktolightapp.Settings.Log;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.Model_DB.Event.EventWriter;
import com.example.picktolightapp.Model_DB.Event.Evento;
import com.example.picktolightapp.Model_DB.Event.EventoTable;
import com.example.picktolightapp.Model_DB.Event.TipoEvento;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.Model_DB.User.UserTable;
import com.example.picktolightapp.R;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

public class LogFragment extends Fragment {

    private TableLayout logTableLayout;
    private Button chooseDateButton,visualizeLogBtn,eliminaLogBtn;
    private Calendar selectedDate;
    private Timestamp selectedTimestamp;
    private TextView tipoColumn;
    private int dispositivoID = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.log, container, false);
        EventWriter eventWriter = EventWriter.getInstance(requireContext());

        MainActivity.setLogoInvisible();

        logTableLayout = view.findViewById(R.id.logTableLayout);
        visualizeLogBtn = view.findViewById(R.id.visualizeLog);
        tipoColumn = view.findViewById(R.id.TipoColumn);
        Button clearLogBtn = view.findViewById(R.id.clearLog);
        eliminaLogBtn = view.findViewById(R.id.eliminaLog);


        selectedDate = Calendar.getInstance();
        chooseDateButton = view.findViewById(R.id.chooseDateButton);


        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        visualizeLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLogTableGeneral();
            }
        });

        clearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogTable();
            }
        });

        eliminaLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DELETE_LOG,true)) {
                    return;
                }

                if(selectedTimestamp == null){
                    DialogsHandler.showWarningDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Warning",
                            "Inserisci prima una data",
                            null
                    );
                    return;
                }

                //eventWriter.clearAll();
                if(EventoTable.deleteEventsByTimestamp(requireContext(),selectedTimestamp)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String timestampStr = dateFormat.format(selectedTimestamp);
                    eventWriter.logEvent(TipoEvento.INFO, "Cancellato il log di " + timestampStr);
                    updateLogTableGeneral();
                }
                else{
                    DialogsHandler.showWarningDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Warning",
                            "La data selezionata non contiene log",
                            null
                    );
                    return;
                }
            }
        });

        //updateLogTable();

        if(!GlobalVariables.getInstance().isSeeDispositivoLog()){
            setDateToToday();
            updateLogTableGeneral();
        }
        else{
            dispositivoID = GlobalVariables.getInstance().getDispositivoIDToSeeLog();
            if(dispositivoID != -1)
                updateLogTableDispositivo();
            setToVisualizeDispositivoLog();
        }

        return view;
    }

    private void updateLogTableGeneral() {
        if (logTableLayout != null) {
            logTableLayout.removeViews(1, logTableLayout.getChildCount() - 1);  // Rimuove tutte le righe tranne la prima (intestazioni)

            if(selectedTimestamp == null){
                DialogsHandler.showWarningDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Warning",
                        "Inserisci prima una data",
                        null
                        );
                return;
            }

            //List<Evento> eventi = EventoTable.getAllEvents(requireContext());
            //Timestamp myTimestamp = Timestamp.valueOf("2024-09-02 00:00:00");
            List<Evento> eventi = EventoTable.getEventsByTimestamp(requireContext(),selectedTimestamp);
            if(eventi == null || eventi.isEmpty()){
                DialogsHandler.showWarningDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Warning",
                        "La data selezionata non contiene log",
                        null
                );
                return;
            }

            boolean isFirstRow = true;  // Variabile per tracciare la prima riga

            for (Evento evento : eventi) {
                TableRow row = new TableRow(requireContext());

                TextView tipoView = new TextView(requireContext());
                tipoView.setText(evento.getTipo().toString());
                tipoView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                tipoView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                switch (evento.getTipo()) {
                    case DEBUG:
                        tipoView.setBackgroundColor(getResources().getColor(R.color.debug_color));
                        break;
                    case INFO:
                        tipoView.setBackgroundColor(getResources().getColor(R.color.info_color));
                        break;
                    case WARNING:
                        tipoView.setBackgroundColor(getResources().getColor(R.color.warning_color));
                        break;
                    case ERROR:
                        tipoView.setBackgroundColor(getResources().getColor(R.color.error_color));
                        break;
                    case SERVER:
                        tipoView.setBackgroundColor(getResources().getColor(R.color.server_color));
                        break;
                }

                TextView timestampView = new TextView(requireContext());
                timestampView.setText(evento.getTimestamp().toString());
                timestampView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                TextView descrizioneView = new TextView(requireContext());
                descrizioneView.setText(evento.getDescrizione());
                descrizioneView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 5f));

                row.addView(tipoView);
                row.addView(timestampView);
                row.addView(descrizioneView);


                // Aggiungi una View per il bordo nero sotto la prima riga
                if (isFirstRow) {
                    View borderView = new View(requireContext());
                    borderView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                    borderView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    logTableLayout.addView(borderView);

                    isFirstRow = false;
                }

                logTableLayout.addView(row);

            }
        }
    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void setDateToToday() {
        Calendar today = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
        selectedDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
        selectedDate.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        updateDateButtonText();
    }


    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        chooseDateButton.setText(dateFormat.format(selectedDate.getTime()));
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);
        selectedTimestamp = new Timestamp(selectedDate.getTimeInMillis());
    }


    private void clearLogTable() {
        if (logTableLayout != null) {
            logTableLayout.removeViews(1, logTableLayout.getChildCount() - 1);  // Rimuove tutte le righe tranne la prima (intestazioni)
        }
    }

    private void setToVisualizeDispositivoLog(){
        tipoColumn.setVisibility(View.GONE);

        chooseDateButton.setText("Scegli dispositivo");
        visualizeLogBtn.setVisibility(View.GONE);
        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickDispositivoID();
            }
        });
        eliminaLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DELETE_DISP_HISTORY,true)) {
                    return;
                }
                if(dispositivoID == -1){
                    DialogsHandler.showWarningDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Warning",
                            "Inserisci prima un'id dispositivo",
                            null
                    );
                    return;
                }
                DispositivoStoricoTable.clearAllByID(requireContext(),dispositivoID);
                updateLogTableDispositivo();
            }
        });
    }

    private void showPickDispositivoID(){
        DialogsHandler.showChangeValueDialog(
                requireContext(),
                getLayoutInflater(),
                "Scegli ID del dispositivo",
                "Inserisci l'id del dispositivo da visualizzare",
                InputType.TYPE_CLASS_NUMBER,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText =  ((View) v.getParent()).findViewById(R.id.editTextInput);
                        String newValue = editText.getText().toString().trim();
                        dispositivoID = Integer.parseInt(newValue);

                        updateLogTableDispositivo();
                    }
                },
                null
        );
    }

    private void updateLogTableDispositivo() {
        if (logTableLayout != null) {
            logTableLayout.removeViews(1, logTableLayout.getChildCount() - 1);  // Rimuove tutte le righe tranne la prima (intestazioni)

            if(dispositivoID == -1){
                DialogsHandler.showWarningDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Warning",
                        "Inserisci un id dispositivo",
                        null
                );
                return;
            }
            if(DispositivoTable.getDispositivoById(requireContext(),dispositivoID) == null){
                DialogsHandler.showWarningDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Warning",
                        "Il dispositivo non esiste",
                        null
                );
                return;
            }


            List<DispositivoStorico> eventi = DispositivoStoricoTable.getAllStorici(requireContext());
            if(eventi == null || eventi.isEmpty()){
                DialogsHandler.showWarningDialog(
                        requireContext(),
                        getLayoutInflater(),
                        "Warning",
                        "Il dispositivo selezionato non contiene nessun evento",
                        null
                );
                return;
            }

            boolean isFirstRow = true;  // Variabile per tracciare la prima riga

            for (DispositivoStorico dispositivoStorico : eventi) {
                TableRow row = new TableRow(requireContext());

                TextView timestampView = new TextView(requireContext());
                timestampView.setText(dispositivoStorico.getTimestamp());
                timestampView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                TextView descrizioneView = new TextView(requireContext());
                descrizioneView.setText(dispositivoStorico.getOperation());
                descrizioneView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 5f));

                row.addView(timestampView);
                row.addView(descrizioneView);


                // Aggiungi una View per il bordo nero sotto la prima riga
                if (isFirstRow) {
                    View borderView = new View(requireContext());
                    borderView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                    borderView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    logTableLayout.addView(borderView);

                    isFirstRow = false;
                }

                logTableLayout.addView(row);

            }
        }
    }
}
