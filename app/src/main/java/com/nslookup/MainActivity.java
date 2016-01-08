package com.nslookup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    String url, isip;
    TabActivity[] tabs = new TabActivity[3];
    String[][] result = new String[3][];
    String[] portNum = {"21", "22", "23", "25", "53", "80", "110", "111", "135", "139", "143", "389", "443", "445",
            "587", "1025", "1352", "1433", "1723", "3306", "3389", "5060", "5900", "6001", "8080"};
    String[] portName = {"ftp", "ssh", "telnet", "smtp", "dns", "http", "pop3", "portmapper", "RPC", "netbios", "imap",
            "ldap", "https", "SMB", "outlook", "IIS", "lotus", "SQL", "P2P", "MYSQL", "remote", "SIP", "VR_D",
            "XWindows", "webcache"};
    Intent intent;
    MyProgressBarTask pt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (pt = new MyProgressBarTask(this)).execute();
        Log.d("xx", "zz");
        intent = getIntent();
        url = intent.getStringExtra("url");
        isip = intent.getStringExtra("isip");
        if (isip.equals("2")) {
            try {
                url = new IPConvertTask(url).execute().get();
                Log.d("IPConvert", url);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        parsing();
        Log.d("res", result[0][0]);
        Log.d("tag", "create tabs");
        for (int i = 0; i < 3; i++) {
            if (i == 2) tabs[i] = new TabActivity(result[i]);
            else if (i == 1) tabs[i] = new TabActivity(result[i], true);
            else if (i == 0) tabs[i] = new TabActivity(result[i], url);
        }
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#292933")));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        actionBar.show();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            Tab t = actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this);
            actionBar.addTab(t);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        pt.dismiss();
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent ev) {
        switch (key) {
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return false;
    }

    private void parsing() {
        try {
            Log.d("123", "a");
            parsingIsp();
            Log.d("123", "b");
            parsingDomain();
            Log.d("123", "c");
            parsingPortscan();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void parsingIsp() throws Exception {
        result[0] = new String[1];
        String q = new MyDownloadTask("http://whois.kisa.or.kr/kor/whois.jsc", "query=" + url).execute().get();
        // Log.d("tag", q);
        int x, y;
        if (q.contains("No match!!")) {
            q = "해당 IP를 찾을 수 없습니다.";
            result[0][0] = q;
            return;
        }
        if ((x = q.indexOf("[ 네트워크 할당 정보 ]")) != -1) {
            int t = x;
            if ((x = q.indexOf("할당일자")) != -1) {
                y = q.indexOf("\n", x + 5);
                q = q.substring(t + "[ 네트워크 할당 정보 ]".length(), y);
                q.replaceAll("\t", "");
            }
        }
        if ((x = q.indexOf("NetRange:")) != -1) {
            int t = x;
            if ((x = q.indexOf("OrgTechRef:")) != -1) {
                y = q.indexOf("\n", x + 5);
                q = q.substring(t, y);
                q.replaceAll("\t", "");
            }
        }
        result[0][0] = q;
        Log.d("itemsresult", result[0][0]);
    }

    private static String[] DomainSplit(String str) {
        int x;
        Log.d("DomainSplit", str);
        if ((x = str.indexOf("Array")) != -1)
            str = str.substring(x + 9);
        str = str.replaceAll(",\\s\"\"\\],\\s\\[\"", "");
        str = str.substring(0, str.length() - 8);
        return str.split("\"");
    }

    private void parsingDomain() throws Exception {
        int x;
        String[] tmp = {"naver.com", "www.naver.com", "google.com", "conan.co.jp"};// DomainSplit(q);
        result[1] = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++)
            result[1][i] = tmp[i];
        /*
        Log.d("parsing","DomainStarted");
        String q = new MyDownloadTask("http://domains.yougetsignal.com/domains.php", "remoteAddress=" + url + "&key=&_=").execute().get();
        Log.d("parsing",q);
        if (q.contains("No web sites")) {
            q = "해당 IP를 찾을 수 없습니다.";
            result[1] = new String[1];
            result[1][0] = q;
            return;
        }
        if ((x = q.indexOf("Array")) != -1) {
            String[] tmp = DomainSplit(q);
            result[1] = new String[tmp.length];
            for (int i = 0; i < tmp.length; i++)
                result[1][i] = tmp[i];
        }*/
    }

    private void parsingPortscan() throws Exception {
        result[2] = new String[25];
        String q = new MyDownloadTask("http://mxtoolbox.com/Public/Lookup.aspx/DoLookup2", "{\"inputText\":\"scan:" + url + "\",\"resultIndex\":8}", true).execute().get();
        q = q.replaceAll("\\\\u0027", "").replaceAll("\\\\u003c", "").replaceAll("\\\\u003e", "").substring(1200);
        Log.d("q", "포트스캔 시작");
        Log.d("xx", q);
        for (int i = 0; i < portNum.length; i++) {
            int st, fi;
            String s;
            if (i < portNum.length - 1) {
                st = q.indexOf(portNum[i]);
                s = q.substring(st, q.indexOf(portNum[i + 1], st));
            } else s = q.substring(q.indexOf(portNum[i]));
            if (s.lastIndexOf("Open") != -1)
                result[2][i] = portNum[i] + " - " + portName[i] + " - " + "Open";
            else if (s.lastIndexOf("Filtered") != -1)
                result[2][i] = portNum[i] + " - " + portName[i] + " - " + "Filtered";
            else result[2][i] = portNum[i] + " - " + portName[i] + " - " + "Refused";
        }
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabSelected(Tab t, FragmentTransaction arg1) {
        Log.d("tag", "onTabSelected" + t.getPosition());
        mViewPager.setCurrentItem(t.getPosition());
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Context mContext;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("tag", "getItem" + position);
            return tabs[position];
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
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}