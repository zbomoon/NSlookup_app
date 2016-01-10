package com.nslookup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DomainInfoActivity extends AppCompatActivity {
    Intent intent;
    String url;
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_info);

        intent = getIntent();
        url = intent.getStringExtra("url");
        mTextView = (TextView)findViewById(R.id.textView);
        mTextView.setTextColor(Color.parseColor("#FFFFFF"));
        setTitle(url);
        try {
            mTextView.setText(parsingDomains());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String parsingDomains() throws Exception {
        String q = new MyDownloadTask("http://domain.whois.co.kr/whois/pop_whois.php", "domain="+url).doInBackground();
        int st = q.indexOf("dot_line.gif",4000)+836;
        int fi = q.indexOf("dot_line.gif",st+100)-75;
        Log.d("domain", q);
        return q.substring(st,fi).replaceAll("<br>", "").replaceAll("<","").replaceAll(">","");
    }
}