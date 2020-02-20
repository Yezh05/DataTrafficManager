package edu.yezh.datatrafficmanager.ui.main;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.io.PrintStream;

import edu.yezh.datatrafficmanager.MyFragment1;
import edu.yezh.datatrafficmanager.MyFragment2;
import edu.yezh.datatrafficmanager.MyFragment3;
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

    private MyFragment1 myFragment1 = null;

    private MyFragment2 myFragment2 = null;

    private MyFragment3 myFragment3 = null;

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
        this.myFragment1 = new MyFragment1();
        this.myFragment2 = new MyFragment2();
        this.myFragment3 = new MyFragment3();
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
                fragment = myFragment1;
                break;
            case 1:
                fragment = myFragment2;
                break;
            case 2:
                fragment = myFragment3;
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