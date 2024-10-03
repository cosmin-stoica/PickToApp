package com.example.picktolightapp.ComponenteHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.picktolightapp.DispositiviHome.ViewPagerAdapter;
import com.example.picktolightapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ComponenteHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.componente_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutComponente);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerComponente);

        PagerAdapter adapter = new PagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("I tuoi componenti");
                    break;
                case 1:
                    tab.setText("Crea componente");
                    break;
            }

            // Imposta una vista personalizzata per ogni tab
            View customTab = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
            TextView tabText = customTab.findViewById(R.id.tabText);
            tabText.setText(tab.getText());
            tab.setCustomView(customTab);

        }).attach();
    }
}
