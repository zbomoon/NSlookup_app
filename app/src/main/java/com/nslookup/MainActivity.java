package com.nslookup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
    ProgressDialog pd;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(this);

        Log.d("xx", "zz");
        intent = getIntent();
        url = intent.getStringExtra("url");
        isip = intent.getStringExtra("isip");
        for (int i = 0; i < 3; i++) {
            if (i == 0) tabs[i] = new TabActivity(true);
            else tabs[i] = new TabActivity();
            Log.d("tabs", "init" + i);
        }
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#292933")));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        actionBar.show();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
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
        try {
            (new BackgroundTask()).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("async", "pre");
            pd.setProgress(5);
            pd.setTitle("NSlooking...");
            pd.setMessage("검색중입니다.\n잠시만 기다려주세요");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (isip.equals("2")) {
                try {
                    url = new IPConvertTask(url).Convert();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            parsing();
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tabs[0].addItem(result[0][0]);
            Log.d("urlurl", "Search IP : "+ url);
            tabs[0].setTextview("Search IP : " + url);
            tabs[1].addListener();
            for (int i = 0; i < result[1].length; i++)
                tabs[1].addItem(result[1][i]);
            for (int i = 0; i < result[2].length; i++) {
                Log.d("tab2", result[2][i]);
                tabs[2].addItem(result[2][i]);
            }
            tabs[0].Update();
            tabs[1].Update();
            tabs[2].Update();
            pd.dismiss();
        }
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
            parsingIsp();
            parsingDomain();
            parsingPortscan();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void parsingIsp() throws Exception {
        result[0] = new String[1];
        Log.d("x", "11");
        String q = new MyDownloadTask("http://whois.kisa.or.kr/kor/whois.jsc", "query=" + url).doInBackground();
        // Log.d("tag", q);
        int x, y;
        Log.d("x", "12");
        if (q.contains("No match!!")) {
            q = "해당 IP를 찾을 수 없습니다.";
            result[0][0] = q;
            return;
        }
        Log.d("x", "13");
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
        str = str.replaceAll("\"1\"","\"\"");
        Log.d("DomainSplit", str);
        str = str.replaceAll(",\\s\"\"\\],\\s\\[\"", "");
        str = str.substring(0, str.length() - 8);
        return str.split("\"");
    }

    private void parsingDomain() throws Exception {
        int x;
        //String[] tmp = {"naver.com", "www.naver.com", "google.com", "conan.co.jp"};// DomainSplit(q);
        //result[1] = new String[tmp.length];
        //for (int i = 0; i < tmp.length; i++) {
        //    result[1][i] = tmp[i];
        //}
        //Log.d("parsing","DomainStarted");
        String q = new MyDownloadTask("http://domains.yougetsignal.com/domains.php", "remoteAddress=" + url + "&key=&_=").doInBackground();
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
        }
    }

    private void parsingPortscan() throws Exception {
        result[2] = new String[25];
        String q = new MyDownloadTask("http://mxtoolbox.com/Public/Lookup.aspx/DoLookup2", "{\"inputText\":\"scan:" + url + "\",\"resultIndex\":8}", true).doInBackground();
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
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
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
}