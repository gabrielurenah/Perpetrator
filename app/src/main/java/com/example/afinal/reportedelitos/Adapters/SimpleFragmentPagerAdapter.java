package com.example.afinal.reportedelitos.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.afinal.reportedelitos.Fragments.PublishFragment;
import com.example.afinal.reportedelitos.R;
import com.example.afinal.reportedelitos.Fragments.ReportsFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new ReportsFragment();
            case 1: return new PublishFragment();
            default: return null;
        }
    }

    //Number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getString(R.string.category_reports);
            case 1: return mContext.getString(R.string.category_publish);
            default: return null;
        }
    }
}
