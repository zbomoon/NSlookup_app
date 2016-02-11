package com.nslookup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends Activity implements View.OnTouchListener {
    private boolean first = false, ipordm = false;
    private EditText txt_query;
    private String str;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txt_query = (EditText) findViewById(R.id.etxtQuery);
        txt_query.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!first) {
                    txt_query.setText("");
                    first = true;
                }
            }
        });
        txt_query.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    nextActivity();
                }
                return false;
            }
        });
        findViewById(R.id.btnSearch).setOnTouchListener(this);
    }

    private void wrong() {
        Toast toast = Toast.makeText(getApplicationContext(), "잘못된 형식의 도메인/IP 주소 입니다.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void nextActivity() {
        String checked = "-1";
        if (((CheckBox) findViewById(R.id.chkMail)).isChecked())
            checked = "1";
        try {
            str = (new BackgroundTask()).execute(txt_query.getText().toString(), checked).get();
            if (str == null)
                wrong();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {
        String ss, chk;

        @Override
        protected String doInBackground(String... params) {
            int diff;
            ss = params[0];
            chk = params[1];
            diff = isIp(ss);
            if (diff == -1) {
                return null;
            }
            if (ss.length() < 4) {
                return null;
            }
            if (ss.contains(" ")) {
                return null;
            }
            if (diff == 1) {
                String[] res = ss.split("\\.");
                for (int i = 0; i < res.length; i++) {
                    if (!isNumeric(res[i]) || !isNum(Integer.parseInt(res[i]))) {
                        return null;
                    }
                }
                ipordm = true;
            } else if (diff == 2) {
                if (ss.substring(0, 7).equals("http://"))
                    ss = ss.substring(7);
                else if (ss.substring(0, 8).equals("https://"))
                    ss = ss.substring(8);
                if (ss.charAt(ss.length() - 1) == '/')
                    ss = ss.substring(0, ss.length() - 1);
                ipordm = false;
            } else if (diff == 3) {
                return null;
            } else {
                return null;
            }
            if (ss == null) {
                return null;
            }
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.putExtra("url", ss);
            if (chk.equals("1")) intent.putExtra("email", true);
            if (ipordm)
                intent.putExtra("isIp", true);
            startActivity(intent);
            return ss;
        }

        public int isIp(String str) {
            int cnt = 0;
            boolean isNotIp = false;
            for (char ch : str.toCharArray()) {
                if (ch != '.' && !(ch <= '9' && ch >= '0')) {
                    isNotIp = true;
                }
                if (ch == '.') {
                    cnt++;
                }
            }
            if (cnt == 0) {
                return -1;
            }
            if (!isNotIp) {
                if (cnt != 3)
                    return 3;
                return 1;
            }
            return 2;
        }

        private boolean isNum(int n) {
            if (n <= 255 && n >= 0)
                return true;
            return false;
        }

        public boolean isNumeric(String str) {
            try {
                double d = Double.parseDouble(str);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Button view;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view = (Button) v;
                view.getBackground().setColorFilter(0x88000000, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                view = (Button) v;
                view.getBackground().setColorFilter(0x00000000, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                nextActivity();
        }
        return true;
    }
}