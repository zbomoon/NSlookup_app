package com.nslookup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class DomainInfoActivity extends AppCompatActivity {
    private String url;
    private TextView mTextView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_info);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#032137")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        mTextView = (TextView) findViewById(R.id.txtDomainInfo);
        mTextView.setTextColor(Color.parseColor("#ff000000"));
        setTitle(url);
        new DomainInfoThread().execute();
    }

    private class DomainInfoThread extends AsyncTask<Integer, Void, Void> {
        private String str;
        private boolean err = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setProgress(5);
            pd.setTitle("검색중...");
            pd.setMessage("잠시만 기다려주세요(최대 1분)");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if (url.substring(0, 3).equals("www")) url = url.substring(4);
            try {
                str = new MyDownloadTask("http://domain.whois.co.kr/whois/pop_whois.php", "domain=" + url, 6).GetString();
            } catch (Exception e){
                e.printStackTrace();
                err = true;
                return null;
            }
            if (str == null) {
                err = true;
                return null;
            }
            int st = str.indexOf("dot_line.gif", 4000) + 836;
            int fi = str.indexOf("dot_line.gif", st + 100) - 75;
            str = str.substring(st, fi).replaceAll("<br>", "").replaceAll("<", "").replaceAll(">", "");
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            super.onPostExecute(res);
            if(err){
                Toast toast = Toast.makeText(getApplicationContext(), "검색 도중 오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                finish();
            }
            if (str.contains("접속에 실패"))
                str = "찾을 수 없는 도메인입니다!";
            mTextView.setText(str);
            pd.dismiss();
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        setResult(RESULT_OK);
        finish();
        return super.onSupportNavigateUp();
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