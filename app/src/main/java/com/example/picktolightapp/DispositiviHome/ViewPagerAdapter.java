package com.example.picktolightapp.DispositiviHome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.R;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Restituisci il Fragment corretto per ogni posizione/tab
        switch (position) {
            case 0:
                return new TuoiDispositiviFragment(); // Primo tab
            case 1:
                GlobalVariables.getInstance().setDispositivoToSee(new Dispositivo(0,0,0));
                GlobalVariables.getInstance().setDispositivoNeedToCreate(true);
                GlobalVariables.getInstance().setLastDestinationId(R.id.DispositiviHomeFragment);
                return new DispositivoVisualizeFragment(); // Secondo tab, sostituisci con il tuo Fragment
            default:
                return new TuoiDispositiviFragment(); // Default
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}