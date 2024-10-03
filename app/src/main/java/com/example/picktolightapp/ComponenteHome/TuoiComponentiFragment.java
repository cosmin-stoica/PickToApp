package com.example.picktolightapp.ComponenteHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.R;

import java.util.List;

public class TuoiComponentiFragment extends Fragment {

    ComponenteAdapter componenteAdapter;
    ListView listView;
    Button btnAggiornaComponenti;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tuoi_componenti, container, false);
        listView = view.findViewById(R.id.listViewComponenti);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        btnAggiornaComponenti = view.findViewById(R.id.btnAggiornaComponenti);

        btnAggiornaComponenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateComponents();
            }
        });

        componenteAdapter = new ComponenteAdapter(requireContext(),navController);
        listView.setAdapter(componenteAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateComponents(){
        List<Componente> newList = ComponenteTable.getAllComponenti(requireContext());
        componenteAdapter.updateData(newList);
    }
}
