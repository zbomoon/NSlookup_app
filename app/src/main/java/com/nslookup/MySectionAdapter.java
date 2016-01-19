package com.nslookup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class MySectionAdapter extends FragmentPagerAdapter {
    public static TabFragment_ISP tab_isp = null;
    public static TabFragment_MAP tab_map = null;
    public static TabFragment_Domain tab_domain = null;
    public static TabFragment_Portscan tab_portscan = null;
    public static TabFragment_Server tab_server = null;
    public MySectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return tab_isp;
            case 1:
                return tab_map;
            case 2:
                return tab_domain;
            case 3:
                return tab_server;
            case 4:
                return tab_portscan;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "ISP".toUpperCase(l);
            case 1:
                return "GPS".toUpperCase(l);
            case 2:
                return "Domain".toUpperCase(l);
            case 3:
                return "Server Info".toUpperCase(l);
            case 4:
                return "Portscan".toUpperCase(l);
        }
        return null;
    }
}