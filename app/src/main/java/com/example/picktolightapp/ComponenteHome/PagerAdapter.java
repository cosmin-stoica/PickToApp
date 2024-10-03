package com.example.picktolightapp.ComponenteHome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;

public class PagerAdapter extends FragmentStateAdapter {


    FragmentActivity fragmentActivity;
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TuoiComponentiFragment(); // Primo tab
            case 1:
                GlobalVariables.getInstance().setComponenteNeedToCreate(true);
                return new CreaComponenteFragment();
            default:
                return new TuoiComponentiFragment(); // Default
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
