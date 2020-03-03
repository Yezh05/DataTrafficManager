package edu.yezh.datatrafficmanager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.io.PrintStream;

import edu.yezh.datatrafficmanager.ui.MyFragmentMobilePage;
import edu.yezh.datatrafficmanager.ui.MyFragmentWifiPage;
import edu.yezh.datatrafficmanager.ui.MyFragmentToolsPage;
import edu.yezh.datatrafficmanager.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private final int PAGER_COUNT = 3;

    private MyFragmentMobilePage myFragmentMobilePage = null;

    private MyFragmentWifiPage myFragmentWifiPage = null;

    private MyFragmentToolsPage myFragmentToolsPage = null;

    /*public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }*/

    public SectionsPagerAdapter(FragmentManager paramFragmentManager, Context paramContext) {
        super(paramFragmentManager);
        this.myFragmentMobilePage = new MyFragmentMobilePage();
        this.myFragmentWifiPage = new MyFragmentWifiPage();
        this.myFragmentToolsPage = new MyFragmentToolsPage();
        this.mContext = paramContext;
    }

    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("position Destory");
        stringBuilder.append(paramInt);
        printStream.println(stringBuilder.toString());
        super.destroyItem(paramViewGroup, paramInt, paramObject);
    }


    public Fragment getItem(int paramInt) {
        Fragment fragment = null;

        switch (paramInt) {
            case 0:
                fragment = myFragmentMobilePage;
                break;
            case 1:
                fragment = myFragmentWifiPage;
                break;
            case 2:
                fragment = myFragmentToolsPage;
                break;
        }

        return fragment;
    }

    public CharSequence getPageTitle(int paramInt) {
        return this.mContext.getResources().getString(TAB_TITLES[paramInt]);
    }

    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt) {
        return super.instantiateItem(paramViewGroup, paramInt);
    }


    @Override
    public int getCount() {
        // Show total pages.
        return PAGER_COUNT;
    }
}