package com.example.picktolightapp.Settings.Log;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.Event.EventWriter;
import com.example.picktolightapp.Model.Event.Evento;
import com.example.picktolightapp.Model.Event.EventoTable;
import com.example.picktolightapp.Model.Event.TipoEvento;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.R;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

public class LogFragment extends Fragment {

    private TableLayout logTableLayout;
    private Button chooseDateButton;
    private Calendar selectedDate;
    private Timestamp selectedTimestamp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.log, container, false);
        EventWriter eventWriter = EventWriter.getInstance(requireContext());

        MainActivity.setLogoInvisible();

        logTableLayout = view.findViewById(R.id.logTableLayout);
        Button visualizeLogBtn = view.findViewById(R.id.visualizeLog);
        Button clearLogBtn = view.findViewById(R.id.clearLog);
        Button eliminaLogBtn = view.findViewById(R.id.eliminaLog);

        chooseDateButton = view.findViewById(R.id.chooseDateButton);
        selectedDate = Calendar.getInstance();
        setDateToToday();
        updateLogTable();

        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        visualizeLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLogTable();
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
                    updateLogTable();
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

        return view;
    }

    private void updateLogTable() {
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

}
