package com.nslookup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    private JobDoneTest jdt;
    private ViewPager mViewPager;
    private String url, domain;
    private String mailDomain = " - KNOWN -", mailPortscan = " - KNOWN -", mailServer = " - KNOWN -", mailIsp = " - KNOWN -";
    private String mailServerTmp = "", mailPortscanTmp = "";
    private String[][] result = new String[4][];
    private String[] portNum = {"21", "22", "23", "25", "53", "80", "110", "111", "135", "139", "143", "389", "443", "445",
            "587", "1025", "1352", "1433", "1723", "3306", "3389", "5060", "5900", "6001", "8080"};
    private String[] portName = {"ftp", "ssh", "telnet", "smtp", "dns", "http", "pop3", "portmapper", "RPC", "netbios", "imap",
            "ldap", "https", "SMB", "outlook", "IIS", "lotus", "SQL", "P2P", "MYSQL", "remote", "SIP", "VR_D",
            "XWindows", "webcache"};
    private boolean isMailchked = false, isIp;
    private static ProgressDialog pd;
    private Double la, lo;
    private ActionBar actionBar;
    private Handler sendMailhandler = new Handler();
    private EditText sendMailinput;
    private boolean working = true;

    public static void MailingEnded(Context ct, boolean successed) {
        if (successed) displayToast(ct, "리포트 메일이 전송되었습니다.");
        else displayToast(ct, "리포트 메일 전송 실패!\n잠시후 다시 시도해주세요");
        pd.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        domain = url = intent.getStringExtra("url");
        isIp = intent.getBooleanExtra("isIp", false);
        isMailchked = intent.getBooleanExtra("email", true);

        TabSectionAdapter.tab_isp = new TabFragment_ISP();
        TabSectionAdapter.tab_domain = new TabFragment_Domain();
        TabSectionAdapter.tab_portscan = new TabFragment_Portscan();
        TabSectionAdapter.tab_server = new TabFragment_Server();

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#032137")));
        actionBar.show();

        TabSectionAdapter mTabSectionAdapter = new TabSectionAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabSectionAdapter);
        mViewPager.setOffscreenPageLimit(mTabSectionAdapter.getCount());
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mTabSectionAdapter.getCount(); i++) {
            Tab t = actionBar.newTab().setText(mTabSectionAdapter.getPageTitle(i)).setTabListener(this);
            actionBar.addTab(t);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        pd = new ProgressDialog(this);
        pd.setProgress(5);
        pd.setTitle("검색중...");
        pd.setMessage("잠시만 기다려주세요(최대 1분)");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        jdt = new JobDoneTest();
        working = true;
        try {
            (new IPconvertThread()).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayToast(Context ct, String str) {
        Toast toast = Toast.makeText(ct, "리포트 메일이 전송되었습니다.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void errorProcess() {
        displayToast(getApplicationContext(), "검색 도중 오류가 발생했습니다.\n다시 시도해주세요");
        this.finish();
    }

    class JobDoneTest {
        private Boolean[] finishedJob;
        private Boolean[] errJob;

        public JobDoneTest() {
            finishedJob = new Boolean[]{true, false, false, false, false, false};
            errJob = new Boolean[]{false, false, false, false, false, false};
        }

        public void setError(int n) {
            errJob[n] = true;
        }

        public synchronized void finished(int n) {
            finishedJob[n] = true;
            if (finishedJob[0] && finishedJob[1] && finishedJob[2] && finishedJob[3] && finishedJob[4] && finishedJob[5])
                doNextjob();
        }

        private void doNextjob() {
            sendMailinput = new EditText(MainActivity.this);
            if (errJob[1] || errJob[2] || errJob[3] || errJob[4] || errJob[5]) {
                String errMsg = "";
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                if (errJob[1]) errMsg += "ISP 정보 확인 불가!\n";
                if (errJob[2]) errMsg += "RDNS(도메인) 정보 확인 불가!\n";
                if (errJob[3]) errMsg += "서버 정보 확인 불가!\n";
                if (errJob[4]) errMsg += "포트스캔 확인불가!\n";
                if (errJob[5]) errMsg += "ISP 지리 정보 추출 실패!\n";
                errMsg += "실패한 항목에 대해서는 나중에 다시 시도하세요.";
                builder1.setTitle("오류!");
                builder1.setMessage(errMsg);
                final Dialog dlgErr = builder1.create();
                builder1.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dlgErr.cancel();
                            }
                        });
                dlgErr.show();
            }
            if (!errJob[1]) {
                mailIsp = result[0][0];
                TabSectionAdapter.tab_isp.addItem(result[0][0]);
                TabSectionAdapter.tab_isp.setTextview("Search IP : " + url);
                TabSectionAdapter.tab_isp.Update();
            }
            if (!errJob[2]) {
                mailDomain = "";
                for (int i = 0; i < result[1].length; i++) {
                    TabSectionAdapter.tab_domain.addItem(result[1][i]);
                    mailDomain += result[1][i] + "\n";
                }
                TabSectionAdapter.tab_domain.Update();
            }
            if (!errJob[3]) {
                TabSectionAdapter.tab_server.Update();
                mailServer = mailServerTmp;
            }
            if (!errJob[4]) {
                TabSectionAdapter.tab_portscan.Update();
                mailPortscan = mailPortscanTmp;
            }
            if (!errJob[5])
                TabSectionAdapter.tab_isp.setGis(la, lo);

            pd.dismiss();
            if (isMailchked) {
                Dialog dlgMail;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("이메일을 입력하세요");
                builder.setView(sendMailinput);
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(sendMailinput.getWindowToken(), 0);
                                pd.setTitle("메일 발신 중...");
                                pd.show();
                                sendMailhandler.postDelayed(sendMailTask, 1300); // 3초후에 실행
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dlgMail = builder.create();
                dlgMail.show();
            }
            working = false;
        }


        private Runnable sendMailTask = new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("cyberuspolice@gmail.com", "cxsfxaosbhscfnkq", getApplicationContext());
                    sender.sendMail("[보고서]Cyber inspector - " + domain,
                            "1. URL : " + domain + "\n2. ISP Information\n" + mailIsp
                                    + "\n\n3. Linked Domain\n"
                                    + mailDomain + "\n" +
                                    "4. Server Information\n" + mailServer +
                                    "\n5. Portscan\n" + mailPortscan,
                            "cyberuspolice@gmail.com",
                            sendMailinput.getText().toString(), TabSectionAdapter.tab_isp.getCapture());
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        };

    }
    class IPconvertThread extends AsyncTask<String, Void, String> {
        private Boolean errorOccured = false;

        @Override
        protected String doInBackground(String... params) {
            if (!isIp) {
                try {
                    url = new IPConvertTask(url).Convert();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorOccured = true;
                    jdt.setError(0);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            if (errorOccured) {
                errorProcess();
                return;
            }
            (new IspThread()).execute(url);
            (new DomainThread()).execute(url);
            (new ServerThread()).execute(url);
            (new PortscanThread()).execute(url);
            (new GpsThread()).execute(url);
        }
    }

    class IspThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingIsp();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError(1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(1);
        }
    }

    class DomainThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingDomain();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError(2);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(2);
        }
    }

    class ServerThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingServer();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError(3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(3);
        }
    }

    class PortscanThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingPortscan();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError(4);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(4);
        }

    }

    class GpsThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingGps();
            } catch (Exception e) {
                e.printStackTrace();
                jdt.setError(5);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            jdt.finished(5);
        }
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent ev) {
        switch (key) {
            case KeyEvent.KEYCODE_BACK:
                if(working){
                    displayToast(getApplicationContext(), "검색중에는 취소할 수 없습니다.");
                    break;
                }
                setResult(RESULT_OK);
                finish();
                break;
        }
        return false;
    }

    private void parsingServer() throws Exception {
        int col;
        Log.d("domain_server", domain);
        String q = new MyDownloadTask("http://toolbar.netcraft.com/site_report?url=" + domain, "", 20).GetString();
        if (q == null) {
            jdt.setError(3);
            return;
        }
        int a = q.indexOf("<h2>Hosting History</h2>");
        int b = q.indexOf("<h2>Security</h2>");
        q = q.substring(a, b);
        q = q.substring(q.indexOf("tbody"));
        col = q.split("<tr").length - 1;
        mailServerTmp = "Date / IP / OS / Server\n";
        for (int i = 0; i < col; i++) {
            String tmp[] = new String[4];
            String t1 = q.substring(q.indexOf("<td>") + 10);
            String tmpstr = t1.substring(t1.indexOf("</td>") + 5, t1.indexOf("</tr>"));
            for (int j = 0; j < 4; j++) {
                int p = tmpstr.indexOf("</td>");
                tmp[j] = tmpstr.substring(tmpstr.indexOf("<td") + 4, p);
                tmpstr = tmpstr.substring(p + 4);
            }
            TabSectionAdapter.tab_server.addItem(tmp[0], tmp[1], tmp[2], tmp[3]);
            mailServerTmp += tmp[3] + " - " + tmp[0] + " - " + tmp[1] + " - " + tmp[2] + "\n";
            q = q.substring(q.indexOf("</tr>") + 5);
        }
    }

    private void parsingGps() throws Exception {
        String q = new MyDownloadTask("http://whatismyipaddress.com/ip/" + url, "", 20).GetString();
        if (q == null) {
            jdt.setError(5);
            return;
        }
        q = q.substring(q.indexOf("Latitude:"));
        la = Double.parseDouble(q.substring(19, q.indexOf("nbsp") - 1));
        q = q.substring(q.indexOf("Longitude:"));
        lo = Double.parseDouble(q.substring(20, q.indexOf("nbsp") - 1));
    }

    private void parsingIsp() throws Exception {
        result[0] = new String[1];
        String q = new MyDownloadTask("http://whois.kisa.or.kr/kor/whois.jsc", "query=" + url, 10).GetString();
        if (q == null) {
            jdt.setError(1);
            return;
        }
        int x, y;
        if (q.contains("No match!!")) {
            Log.d("isp", "service1");
            q = "해당 IP를 찾을 수 없습니다.";
            result[0][0] = q;
            return;
        }
        if ((x = q.indexOf("afrinic")) != -1) {
            q = q.substring(q.indexOf("</a>", x) + 5);
            if ((x = q.lastIndexOf("</pre>")) != -1) {
                q = q.substring(0, x - 2);
            }
        }
        if ((x = q.indexOf("[ 네트워크 할당 정보 ]")) != -1) {
            int t = x;
            if ((x = q.indexOf("할당일자")) != -1) {
                y = q.indexOf("\n", x + 5);
                q = q.substring(t + "[ 네트워크 할당 정보 ]".length(), y);
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
        if (q.contains("서비스가 원할하지")) {
            Log.d("isp", "service2");
            result[0][0] = "";
            q = new MyDownloadTask("http://whatismyipaddress.com/ip/" + url, "", 5).GetString();
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
                            }
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

    private void parsingDomain2() {
        String q = "";
        try {
            q = new MyDownloadTask("http://www.ipfingerprints.com/scripts/getReverseIP.php", "remoteHost=" + domain, 15).GetString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        q = q.substring(30, q.length() - 2);
        while (true) {
            int a, b;
            if ((a = q.indexOf('<')) != -1) {
                if ((b = q.indexOf('>')) != -1) {
                    if (a > 0) q = q.substring(0, a) + q.substring(b + 1);
                    else q = q.substring(b + 1);
                }
            } else break;
        }
        if (q.charAt(q.length() - 1) == '\"')
            q = q.substring(0, q.length() - 1);
        String[] tmp = q.split("www");
        result[1] = new String[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) {
            if (tmp[i + 1].length() > 0 && tmp[i + 1].charAt(0) == '.')
                result[1][i] = tmp[i + 1].substring(1);
            else result[1][i] = tmp[i + 1];
        }
    }

    private void parsingDomain() {
        String q;
        try {
            q = new MyDownloadTask("http://domains.yougetsignal.com/domains.php", "remoteAddress=" + domain + "&key=&_=", 8).GetString();
        } catch (Exception e) {
            e.printStackTrace();
            parsingDomain2();
            return;
        }
        if (q.contains("Daily reverse IP check") || q.contains("No web sites")) {
            parsingDomain2();
        } else if (q.contains("Array")) {
            String[] tmp = DomainSplit(q);
            result[1] = new String[tmp.length - 1];
            for (int i = 0; i < tmp.length - 1; i++)
                result[1][i] = tmp[i + 1];
        }
    }

    private void parsingPortscan() throws Exception {
        String q = new MyDownloadTask("http://mxtoolbox.com/Public/Lookup.aspx/DoLookup2", "{\"inputText\":\"scan:" + url + "\",\"resultIndex\":8}", 5, true).GetString();
        q = q.replaceAll("\\\\u0027", "").replaceAll("\\\\u003c", "").replaceAll("\\\\u003e", "").substring(1200);
        mailPortscanTmp = "Port / Protocol / Status\n";
        for (int i = 0; i < portNum.length; i++) {
            int st;
            String s;
            if (i < portNum.length - 1) {
                st = q.indexOf(portNum[i]);
                s = q.substring(st, q.indexOf(portNum[i + 1], st));
            } else s = q.substring(q.indexOf(portNum[i]));
            if (s.lastIndexOf("Open") != -1) {
                TabSectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Open");
                mailPortscanTmp += portNum[i] + " - " + portName[i] + " - " + "Open\n";
            } else if (s.lastIndexOf("Filtered") != -1) {
                TabSectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Filtered");
                mailPortscanTmp += portNum[i] + " - " + portName[i] + " - " + "Filtered\n";
            } else if (s.lastIndexOf("Close") != -1) {
                TabSectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Closed");
                mailPortscanTmp += portNum[i] + " - " + portName[i] + " - " + "Closed\n";
            } else {
                TabSectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Refused");
                mailPortscanTmp += portNum[i] + " - " + portName[i] + " - " + "Refused\n";
            }
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
    }
}