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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    MySectionAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    String url, isip, domain;
    String[][] result = new String[4][];
    Double la, lo;
    String[] portNum = {"21", "22", "23", "25", "53", "80", "110", "111", "135", "139", "143", "389", "443", "445",
            "587", "1025", "1352", "1433", "1723", "3306", "3389", "5060", "5900", "6001", "8080"};
    String[] portName = {"ftp", "ssh", "telnet", "smtp", "dns", "http", "pop3", "portmapper", "RPC", "netbios", "imap",
            "ldap", "https", "SMB", "outlook", "IIS", "lotus", "SQL", "P2P", "MYSQL", "remote", "SIP", "VR_D",
            "XWindows", "webcache"};
    Intent intent;
    static ProgressDialog pd, pd1;
    ActionBar actionBar;
    long nStart = 0;
    JobDoneTest jdt;
    boolean checked = false;
    boolean mail_res = false;
    String mail_domain = " - KNOWN -", mail_portscan = " - KNOWN -", mail_server = " - KNOWN -", mail_isp = " - KNOWN -";
    String mail_server_tmp = "", mail_portscan_tmp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(this);
        pd1 = new ProgressDialog(this);
        intent = getIntent();
        url = intent.getStringExtra("url");
        domain = url;
        isip = intent.getStringExtra("isip");
        if (intent.getIntExtra("email", 0) == 1)
            checked = true;
        MySectionAdapter.tab_isp = new TabFragment_ISP();
        MySectionAdapter.tab_map = new TabFragment_MAP();
        MySectionAdapter.tab_domain = new TabFragment_Domain();
        MySectionAdapter.tab_portscan = new TabFragment_Portscan();
        MySectionAdapter.tab_server = new TabFragment_Server();
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#032137")));
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
        pd.setTitle("검색중...");
        pd.setMessage("잠시만 기다려주세요(최대 1분)");
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

    public static void MailingEnded(boolean sw, Context ct) {
        if (sw) {
            Toast toast = Toast.makeText(ct, "리포트 메일이 전송되었습니다.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            pd1.dismiss();
        } else {
            Toast toast = Toast.makeText(ct, "리포트 메일 전송 실패!\n잠시후 다시 시도해주세요", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            pd1.dismiss();
        }
    }

    class JobDoneTest {
        Boolean[] finishjob;
        Boolean[] errjob;

        public JobDoneTest() {
            finishjob = new Boolean[]{true, false, false, false, false, false};
            errjob = new Boolean[]{false, false, false, false, false, false};
        }

        public void setError(int n) {
            errjob[n] = true;
        }

        public synchronized void finished(int n) {
            finishjob[n] = true;
            if (finishjob[0] && finishjob[1] && finishjob[2] && finishjob[3] && finishjob[4] && finishjob[5])
                doNextjob();
        }

        private void doNextjob() {
            final Dialog dialog, dialog1;
            AlertDialog.Builder builder = null;
            AlertDialog.Builder builder1 = null;
            final EditText input = new EditText(MainActivity.this);
            if (errjob[1] || errjob[2] || errjob[3] || errjob[4] || errjob[5]) {
                String errMsg = "";
                builder1 = new AlertDialog.Builder(MainActivity.this);
                if (errjob[1])
                    errMsg += "ISP 정보 확인 불가!\n";
                if (errjob[2])
                    errMsg += "RDNS(도메인) 정보 확인 불가!\n";
                if (errjob[3])
                    errMsg += "서버 정보 확인 불가!\n";
                if (errjob[4])
                    errMsg += "포트스캔 확인불가!\n";
                if (errjob[5])
                    errMsg += "ISP 지리 정보 추출 실패!\n";
                errMsg += "실패한 항목에 대해서는 나중에 다시 시도하세요.";
                builder1.setTitle("오류 발생!");
                builder1.setMessage(errMsg);
                // Setting Positive "Yes" Button
                dialog1 = builder1.create();
                builder1.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog1.cancel();
                            }
                        });
                dialog1.show();
            }
            if (checked) { //Emailing
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("이메일을 입력하세요");
                builder.setView(input);
                // Setting Positive "Yes" Button
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                pd1.setProgress(5);
                                pd1.setTitle("메일 발신 중...");
                                pd1.setMessage("잠시만 기다려주세요(최대 1분)");
                                pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pd1.setCanceledOnTouchOutside(false);
                                pd1.show();
                                Log.d("mail", "mail start");
                                try {
                                    GMailSender sender = new GMailSender("cyberuspolice@gmail.com", "cxsfxaosbhscfnkq", getApplicationContext());

                                    Log.d("mail", "mail start");
                                    sender.sendMail("[보고서]Cyber inspector - " + domain,
                                            "1. URL : " + domain + "\n2. ISP Information\n" + result[0][0] + "\n\n3. Linked Domain\n"
                                                    + mail_domain + "\n" +
                                                    "4. Server Information\n" + mail_server +
                                                    "\n5. Portscan\n" + mail_portscan,
                                            "cyberuspolice@gmail.com",
                                            input.getText().toString(), MySectionAdapter.tab_map.getCapture());
                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }
                                Log.d("mail", "mail ended");
                            }
                        });
                // Setting Negative "NO" Button
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
            }

            if (!errjob[1]) {
                mail_isp = result[0][0];
                MySectionAdapter.tab_isp.addItem(result[0][0]);
                MySectionAdapter.tab_isp.setTextview("Search IP : " + url);
                MySectionAdapter.tab_isp.Update();
            }

            if (!errjob[2])

            {
                mail_domain = "";
                for (int i = 0; i < result[1].length; i++) {
                    MySectionAdapter.tab_domain.addItem(result[1][i]);
                    mail_domain += result[1][i] + "\n";
                }
                MySectionAdapter.tab_domain.Update();
            }

            if (!errjob[3])

            {
                MySectionAdapter.tab_server.Update();
                mail_server = mail_server_tmp;
            }

            if (!errjob[4])

            {
                MySectionAdapter.tab_portscan.Update();
                mail_portscan = mail_portscan_tmp;
            }

            if (!errjob[5])

            {
                Log.d("go la", Double.toString(la));
                Log.d("go lo", Double.toString(lo));
                MySectionAdapter.tab_map.setGis(la, lo);
            }
            pd.dismiss();
            if (checked) {
                dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void errorProcess() {
        Toast toast = Toast.makeText(getApplicationContext(), "검색 도중 오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT);
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
            (new ISPparsing_thread()).execute(url);
            (new Domainparsing_thread()).execute(url);
            (new Serverparsing_thread()).execute(url);
            (new PortScanparsing_thread()).execute(url);
            (new Gpsparsing_thread()).execute(url);
        }
    }

    class ISPparsing_thread extends AsyncTask<String, Void, String> {
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

    class Domainparsing_thread extends AsyncTask<String, Void, String> {
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

    class Gpsparsing_thread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingGps();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ex", "gps");
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

    class Serverparsing_thread extends AsyncTask<String, Void, String> {
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

    class PortScanparsing_thread extends AsyncTask<String, Void, String> {
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

    private void parsingServer() throws Exception {
        int col;
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
        mail_server_tmp = "Date / IP / OS / Server\n";
        for (int i = 0; i < col; i++) {
            String tmp[] = new String[4];
            String t1 = q.substring(q.indexOf("<td>") + 10);
            String tmpstr = t1.substring(t1.indexOf("</td>") + 5, t1.indexOf("</tr>"));
            for (int j = 0; j < 4; j++) {
                int p = tmpstr.indexOf("</td>");
                tmp[j] = tmpstr.substring(tmpstr.indexOf("<td") + 4, p);
                tmpstr = tmpstr.substring(p + 4);
            }
            MySectionAdapter.tab_server.addItem(tmp[0], tmp[1], tmp[2], tmp[3]);
            mail_server_tmp = tmp[3] + " - " + tmp[0] + " - " + tmp[1] + " - " + tmp[2] + "\n";
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
        Log.d("q", q);
        Log.d("fi", Integer.toString(q.indexOf("nbsp")));
        Log.d("str", q.substring(19, q.indexOf("nbsp") - 1));
        la = Double.parseDouble(q.substring(19, q.indexOf("nbsp") - 1));
        Log.d("lati", q.substring(19, q.indexOf("nbsp") - 1));
        q = q.substring(q.indexOf("Longitude:"));
        Log.d("q", q);
        Log.d("long", q.substring(20, q.indexOf("nbsp") - 1));
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
            q = "해당 IP를 찾을 수 없습니다.";
            result[0][0] = q;
            return;
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
        } catch (Exception e0) {
            e0.printStackTrace();
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
            //q = " {\"status\":\"Success\", \"resultsMethod\":\"database\", \"lastScrape\":\"2015-12-02 06:36:11\", \"domainCount\":\"726\", \"remoteAddress\":\"199.59.243.120\", \"remoteIpAddress\":\"199.59.243.120\", \"domainArray\":[[\"0-a.com\", \"\"], [\"000webhost.org\", \"\"], [\"007guard.com\", \"\"], [\"0098.0xhost.net\", \"\"], [\"01.land4persian.com\", \"\"], [\"029.29ol.com\", \"\"], [\"02kmky1xgzbmsdfx.com\", \"\"], [\"0interestcreditcard.net\", \"\"], [\"1000dhamaka.com\", \"\"], [\"1000orgasmos.com\", \"\"], [\"12371.com\", \"\"], [\"123buzz.net\", \"\"], [\"12chan.org\", \"\"], [\"130220lc.10001mb.com\", \"\"], [\"140.206.83.19.cn\", \"\"], [\"18ie.com\", \"\"], [\"19871.com\", \"\"], [\"1weby.com\", \"\"], [\"222988.com\", \"\"], [\"24mb.com\", \"\"], [\"24n.org\", \"\"], [\"26th.com\", \"\"], [\"2y2z.com\", \"\"], [\"3055.org\", \"\"], [\"360.gigfa.com\", \"\"], [\"3cks.com\", \"\"], [\"3skypephone.com\", \"\"], [\"41840.aceboard.fr\", \"\"], [\"4ko.net\", \"\"], [\"4lx.com\", \"\"], [\"4sequence.com\", \"\"], [\"50pm.net\", \"\"], [\"51361.y5y5.info\", \"\"], [\"51avi.com\", \"\"], [\"55bbb.com\", \"\"], [\"58407.bodis.com\", \"\"], [\"69tv.com\", \"\"], [\"6groupon.com\", \"\"], [\"7000tv.com\", \"\"], [\"700megs.com\", \"\"], [\"723.qudoushuang.com\", \"\"], [\"7mintosuccess.com\", \"\"], [\"7mirror.rh9.in\", \"\"], [\"7ro.info\", \"\"], [\"7ye.cc\", \"\"], [\"8850taobao.com\", \"\"], [\"9188.yun12.com\", \"\"], [\"95kvartal.com\", \"\"], [\"987wl.com\", \"\"], [\"a.im404.com\", \"\"], [\"aaadx.com\", \"\"], [\"aarroyalresidency.com\", \"\"], [\"abdullah.info\", \"\"], [\"acaphapram.cuccfree.com\", \"\"], [\"actioncycle.in\", \"\"], [\"addpost.com\", \"\"], [\"ademaydiner.22web.org\", \"\"], [\"ademdurak.com\", \"\"], [\"admyntra.in\", \"\"], [\"adwords-direct.com\", \"\"], [\"ah56.totalh.net\", \"\"], [\"aiyi8.com\", \"\"], [\"alamazagak.eb2a.com\", \"\"], [\"albert5.i34u.com\", \"\"], [\"alcoyana.com\", \"\"], [\"alexa.xueaddddxnet-www.zhjade.com\", \"\"], [\"alexiawebapp.com\", \"\"], [\"alicun.com\", \"\"], [\"alkhalas.net\", \"\"], [\"alldogbreeds.000space.com\", \"\"], [\"allmode.com\", \"\"], [\"alluscamgirls.com\", \"\"], [\"alproplus.ru\", \"\"], [\"alraytworeq.0fees.net\", \"\"], [\"alsayde.com\", \"\"], [\"altoia.com\", \"\"], [\"amazongiftcardgenerators.com\", \"\"], [\"amefcu.org\", \"\"], [\"amoory.co\", \"\"], [\"amoory.net\", \"\"], [\"anchorminot.com\", \"\"], [\"andychary143.com\", \"\"], [\"ani1me.eb2a.com\", \"\"], [\"animedownloadonline.com\", \"\"], [\"antiguoscueros.com\", \"\"], [\"apmed.pl\", \"\"], [\"apriljuggs.com\", \"\"], [\"aranta.in\", \"\"], [\"arfi.zheyangdang.com\", \"\"], [\"article99.com\", \"\"], [\"aspect.capital\", \"\"], [\"auctionhints.com\", \"\"], [\"aunt.xxx\", \"1\"], [\"autocraftindustries.in\", \"\"], [\"avatarcasino.com\", \"\"], [\"avonhost.com\", \"\"], [\"b5b.net\", \"\"], [\"b9i.net\", \"\"], [\"baixali.com.br\", \"\"], [\"balagadoom.0fees.net\", \"\"], [\"bankff.com\", \"\"], [\"batikmurahnya.com\", \"\"], [\"bbm.net\", \"\"], [\"bbnetwork.com\", \"\"], [\"bbs.se8x.cc\", \"\"], [\"bbs.ty91.com\", \"\"], [\"bbs.weshareit.net\", \"\"], [\"bcw.in\", \"\"], [\"bdmp3song.com\", \"\"], [\"be.my.bb.com\", \"\"], [\"beatsbydre.co.in\", \"\"], [\"become.com\", \"\"], [\"belive.uk.tn\", \"\"], [\"benaught.com\", \"\"], [\"beta.facebookc.com\", \"\"], [\"bezaafe.com\", \"\"], [\"bikegrass.biz\", \"\"], [\"bilpris.nu\", \"\"], [\"bing.com\", \"\"], [\"bisep.edu.pk\", \"\"], [\"bjpudupi.com\", \"\"], [\"blagoveschenski.byethost33.com\", \"\"], [\"blizoo.com\", \"\"], [\"blogdasgalinhas.com\", \"\"], [\"blogstars.com.br\", \"\"], [\"board.gooseed.com\", \"\"], [\"booksamillon.com\", \"\"], [\"boothedogs.com\", \"\"], [\"botinkipyablf.zh.md\", \"\"], [\"brabackless.my-php.net\", \"\"], [\"burger-imperia.com\", \"\"], [\"bvick.qhealthbeauty.com\", \"\"], [\"bytewizecomputers.com\", \"\"], [\"c9k.net\", \"\"], [\"ca8.net\", \"\"], [\"cangbaogejy.com\", \"\"], [\"carinsurancecomnewyork.com\", \"\"], [\"cascanolahack.org\", \"\"], [\"cashinonmail.com\", \"\"], [\"ce.cc\", \"\"], [\"cgi.godsview.com\", \"\"], [\"cgiscriptmarket.com\", \"\"], [\"chaussuresnmax.fr\", \"\"], [\"checkproxy.com\", \"\"], [\"chengqikd.com\", \"\"], [\"chicagodowntownhotels.net\", \"\"], [\"chirurgie-bariatrique.aceboard.fr\", \"\"], [\"chronosfire.com.ar\", \"\"], [\"cipha.org\", \"\"], [\"classifieds.indiawebsite.in\", \"\"], [\"clix.com\", \"\"], [\"cnapest46.eb2a.com\", \"\"], [\"cochacks.xyz\", \"\"], [\"comcadt.net\", \"\"], [\"comicp.xgmm.com\", \"\"], [\"comingsoondomain.com\", \"\"], [\"conditionzebra.com\", \"\"], [\"cooktown.com\", \"\"], [\"coolmmathgames.com\", \"\"], [\"cr7.net\", \"\"], [\"crosereviews.podserver.info\", \"\"],";
        } catch (Exception e) {
            e.printStackTrace();
            parsingDomain2();
            return;
        }
        if (q.contains("Daily reverse IP check") || q.contains("No web sites")) {
            parsingDomain2();
        } else if (q.contains("Array")) { // Normal!!
            String[] tmp = DomainSplit(q);
            result[1] = new String[tmp.length - 1];
            for (int i = 0; i < tmp.length - 1; i++)
                result[1][i] = tmp[i + 1];
        }
    }

    private void parsingPortscan() throws Exception {
        String q = new MyDownloadTask("http://mxtoolbox.com/Public/Lookup.aspx/DoLookup2", "{\"inputText\":\"scan:" + url + "\",\"resultIndex\":8}", 5, true).GetString();
        q = q.replaceAll("\\\\u0027", "").replaceAll("\\\\u003c", "").replaceAll("\\\\u003e", "").substring(1200);
        mail_portscan_tmp = "Port / Protocol / Status\n";
        for (int i = 0; i < portNum.length; i++) {
            int st;
            String s;
            if (i < portNum.length - 1) {
                st = q.indexOf(portNum[i]);
                s = q.substring(st, q.indexOf(portNum[i + 1], st));
            } else s = q.substring(q.indexOf(portNum[i]));
            if (s.lastIndexOf("Open") != -1) {
                MySectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Open");
                mail_portscan_tmp += portNum[i] + " - " + portName[i] + " - " + "Open\n";
            } else if (s.lastIndexOf("Filtered") != -1) {
                MySectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Filtered");
                mail_portscan_tmp += portNum[i] + " - " + portName[i] + " - " + "Filtered\n";
            } else if (s.lastIndexOf("Close") != -1) {
                MySectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Closed");
                mail_portscan_tmp += portNum[i] + " - " + portName[i] + " - " + "Closed\n";
            } else {
                MySectionAdapter.tab_portscan.addItem(portNum[i], portName[i], "Refused");
                mail_portscan_tmp += portNum[i] + " - " + portName[i] + " - " + "Refused\n";
            }
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