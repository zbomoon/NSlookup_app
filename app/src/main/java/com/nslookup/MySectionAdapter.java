package com.nslookup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.Locale;

public class MySectionAdapter extends FragmentPagerAdapter {
    Context mContext;
    String url;
    Boolean isDomainInfo_second_frgment = false;
    public static TabFragment_ISP tab_isp = null;
    public static TabFragment_Domain tab_domain = null;
    public static TabFragment_Portscan tab_portscan = null;
    private FragmentManager fm;
    private Fragment changeFragment;
    public MySectionAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public void set_DomainInfo_tab(Boolean t, String s) {
        isDomainInfo_second_frgment = t;
        this.url = s;
        Log.d("domain","true");
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return tab_isp;
            case 1:
                return tab_domain;
            case 2:
                return tab_portscan;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "ISP".toUpperCase(l);
            case 1:
                return "Domain".toUpperCase(l);
            case 2:
                return "Portscan".toUpperCase(l);
        }
        return null;
    }
}