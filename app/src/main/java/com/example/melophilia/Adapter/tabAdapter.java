package com.example.melophilia.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.melophilia.Home.songLocalFragment;
import com.example.melophilia.Home.songUploadFragment;

public class tabAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public tabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                songUploadFragment songUploadFragment= new songUploadFragment();
                return songUploadFragment;
            case 1:
                songLocalFragment songLocalFragment = new songLocalFragment();
                return songLocalFragment;

            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
