package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    int numberOfTabs;
    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numberOfTabs=behavior;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Members();
            case 1:
                return new Projects();
            case 2:
                return new OpenTasks();
            case 3:
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
