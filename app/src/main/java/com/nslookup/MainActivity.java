package com.nslookup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    MySectionAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    String url, isip;
    String[][] result = new String[3][];
    String[] portNum = {"21", "22", "23", "25", "53", "80", "110", "111", "135", "139", "143", "389", "443", "445",
            "587", "1025", "1352", "1433", "1723", "3306", "3389", "5060", "5900", "6001", "8080"};
    String[] portName = {"ftp", "ssh", "telnet", "smtp", "dns", "http", "pop3", "portmapper", "RPC", "netbios", "imap",
            "ldap", "https", "SMB", "outlook", "IIS", "lotus", "SQL", "P2P", "MYSQL", "remote", "SIP", "VR_D",
            "XWindows", "webcache"};
    Intent intent;
    ProgressDialog pd;
    ActionBar actionBar;
    long nStart = 0;
    JobDoneTest jdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(this);
        intent = getIntent();
        url = intent.getStringExtra("url");
        Log.d("find url", url);
        isip = intent.getStringExtra("isip");
        MySectionAdapter.tab_isp = new TabFragment_ISP();
        MySectionAdapter.tab_domain = new TabFragment_Domain();
        MySectionAdapter.tab_portscan = new TabFragment_Portscan();
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#292933")));
        mSectionsPagerAdapter = new MySectionAdapter(getSupportFragmentManager());
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
        nStart = System.currentTimeMillis();
        pd.setProgress(5);
        pd.setTitle("NSlooking...");
        pd.setMessage("검색중입니다.\n잠시만 기다려주세요");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        jdt = new JobDoneTest();
        try {
            (new IPConvert_thread()).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class JobDoneTest {
        Boolean[] finishjob;
        Boolean errorOccured = false;

        public JobDoneTest() {
            finishjob = new Boolean[]{true, false, false, false};
        }

        public void setError(){
            errorOccured = true;
        }

        public synchronized void finished(int n) {
            Log.d("finish", Integer.toString(n));
            finishjob[n] = true;
            if (finishjob[0] == false || finishjob[1] == false || finishjob[2] == false || finishjob[3] == false)
                return;
            else{
                if(errorOccured) {
                    errorProcess();
                    return;
                }
                doNextjob();
            }
        }

        private void doNextjob() {
            MySectionAdapter.tab_isp.addItem(result[0][0]);
            MySectionAdapter.tab_isp.setTextview("Search IP : " + url);
            for (int i = 0; i < result[1].length; i++)
                MySectionAdapter.tab_domain.addItem(result[1][i]);
            MySectionAdapter.tab_portscan.addItem(result[2][0]);
            MySectionAdapter.tab_isp.Update();
            MySectionAdapter.tab_domain.Update();
            MySectionAdapter.tab_portscan.Update();
            pd.dismiss();
            Log.d("Time", Long.toString(System.currentTimeMillis() - nStart));
        }
    }

    private void errorProcess() {
        Log.d("Error","proc");
        Toast toast = Toast.makeText(getApplicationContext(), "검색 도중 오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        finish();
    }

    class IPConvert_thread extends AsyncTask<String, Void, String> {
        Boolean errorOccured = false;
        @Override
        protected String doInBackground(String... params) {
            if (isip.equals("2")) {
                try {
                    url = new IPConvertTask(url).Convert();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorOccured = true;
                    jdt.setError();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            if(errorOccured) {
                errorProcess();
                return;
            }
            (new ISPparsing_thread()).execute(url);
            (new Domainparsing_thread()).execute(url);
            (new PortScanparsing_thread()).execute(url);
        }
    }

    class ISPparsing_thread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingIsp();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(1);
        }
    }

    class Domainparsing_thread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingDomain();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(2);
        }
    }

    class PortScanparsing_thread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingPortscan();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(3);
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

    private void parsingIsp() throws Exception {
        result[0] = new String[1];
        String q = new MyDownloadTask("http://whois.kisa.or.kr/kor/whois.jsc", "query=" + url).GetString();
        if(q == null){
            jdt.setError();
            return;
        }
        int x, y;
        Log.d("here","come");
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
                q = q.replaceAll("\t", "");
            }
        }
        if (q.indexOf("서비스가 원할하지") != -1) {
            result[0][0] = "";
            Log.d("ISP", "whatismyipaddress reparsing");
            q = new MyDownloadTask("http://whatismyipaddress.com/ip/" + url, "").GetString();
            q = q.substring(q.indexOf("General IP Information</h2>"), q.indexOf("Geolocation Map</h2>"));
            while (true) {
                int n;
                if ((n = q.indexOf("</th><td>")) != -1) {
                    String s1 = q.substring(q.substring(0, n).lastIndexOf("<th>") + "<th>".length(), n - 1);
                    String s2 = q.substring(n + "</th><td>".length(), q.indexOf("</td>", n + "</th><td>".length()));
                    while (true) {
                        int a, b;
                        if ((a = s2.indexOf('<')) != -1) {
                            if ((b = s2.indexOf('>')) != -1) {
                                if (a > 0) s2 = s2.substring(0, a) + s2.substring(b + 1);
                                else s2 = s2.substring(b + 1);
                            } else Log.d("fatal", "err");
                        } else break;
                    }
                    if (s1.equals("Latitude"))
                        s2 = s2.substring(1, s2.indexOf('&') - 1);
                    else if (s1.equals("Longitude"))
                        s2 = s2.substring(1, s2.indexOf('&') - 1);
                    result[0][0] += s1 + " : " + s2 + "\n";
                    q = q.substring(n + "</th><td>".length());
                } else break;
            }
        } else result[0][0] = q;
    }

    private static String[] DomainSplit(String str) {
        int x;
        if ((x = str.indexOf("Array")) != -1)
            str = str.substring(x + 9);
        str = str.replaceAll("\"1\"", "\"\"");
        str = str.replaceAll(",\\s\"\"\\],\\s\\[\"", "");
        str = str.substring(0, str.length() - 8);
        return str.split("\"");
    }

    private void parsingDomain() throws Exception {
        int x;
        //횟수 제한에 따른 디버깅용
        String[] tmp = {"naver.com", "www.naver.com", "google.com", "conan.co.jp"};// DomainSplit(q);
        result[1] = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            result[1][i] = tmp[i];
        }
        /*
        String q = new MyDownloadTask("http://domains.yougetsignal.com/domains.php", "remoteAddress=" + url + "&key=&_=").GetString();

        if (q.contains("Daily reverse IP check")) {
            result[1] = new String[1];
            result[1][0] = "IP reverse 기능 1일 이용 횟수가 초과되었습니다!";
            return;
        } else if (q.contains("No web sites")) {
            result[1] = new String[1];
            result[1][0] = "해당 IP를 찾을 수 없습니다!";
            return;
        } else if ((x = q.indexOf("Array")) != -1) {
            String[] tmp = DomainSplit(q);
            result[1] = new String[tmp.length];
            for (int i = 0; i < tmp.length; i++)
                result[1][i] = tmp[i];
        }*/
    } 
    private void parsingPortscan() throws Exception {
        result[2] = new String[1];
        result[2][0] = "";
        String q = new MyDownloadTask("http://mxtoolbox.com/Public/Lookup.aspx/DoLookup2", "{\"inputText\":\"scan:" + url + "\",\"resultIndex\":8}", true).GetString();
        q = q.replaceAll("\\\\u0027", "").replaceAll("\\\\u003c", "").replaceAll("\\\\u003e", "").substring(1200);
        for (int i = 0; i < portNum.length; i++) {
            int st, fi;
            String s;
            if (i < portNum.length - 1) {
                st = q.indexOf(portNum[i]);
                s = q.substring(st, q.indexOf(portNum[i + 1], st));
            } else s = q.substring(q.indexOf(portNum[i]));
            if (s.lastIndexOf("Open") != -1)
                result[2][0] += portNum[i] + " - " + portName[i] + " - " + "Open" + "\n";
            else if (s.lastIndexOf("Filtered") != -1)
                result[2][0] += portNum[i] + " - " + portName[i] + " - " + "Filtered" + "\n";
            else result[2][0] += portNum[i] + " - " + portName[i] + " - " + "Refused" + "\n";
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
}