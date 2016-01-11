package com.nslookup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class DomainInfoActivity extends AppCompatActivity {
    Intent intent;
    String url;
    TextView mTextView;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_info);

        pd = new ProgressDialog(this);
        intent = getIntent();
        url = intent.getStringExtra("url");
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setTextColor(Color.parseColor("#FFFFFF"));
        setTitle(url);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DomainInfo_thread().execute();
    }

    class DomainInfo_thread extends AsyncTask<Integer, Void, Void> {
        String q;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setProgress(5);
            pd.setTitle("NSlooking...");
            pd.setMessage("검색중입니다.\n잠시만 기다려주세요");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if (url.substring(0, 3).equals("www"))
                url = url.substring(4);
            Log.d("urlurl", url);
            q = new MyDownloadTask("http://domain.whois.co.kr/whois/pop_whois.php", "domain=" + url).GetString();
            if (q == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "검색 도중 오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                finish();
            }
            int st = q.indexOf("dot_line.gif", 4000) + 836;
            int fi = q.indexOf("dot_line.gif", st + 100) - 75;
            q = q.substring(st, fi).replaceAll("<br>", "").replaceAll("<", "").replaceAll(">", "");
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            super.onPostExecute(res);
            if (q.indexOf("접속에 실패") != -1)
                q = "찾을 수 없는 도메인입니다!";
            mTextView.setText(q);
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
}