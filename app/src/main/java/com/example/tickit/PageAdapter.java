package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PageAdapter extends FragmentStatePagerAdapter {
    int numberOfTabs;

    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm);
        this.numberOfTabs=behavior;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Dashboard();
            case 1:
                return new Members();
            case 2:
                return new Projects();
            case 3:
                return new OpenTasks();
            case 4:
                return new MyTasks();
                default:
                    return null;

        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
