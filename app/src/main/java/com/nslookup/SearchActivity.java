package com.nslookup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends Activity {
	boolean first = false;
	EditText txt_query;
	String str;
	boolean ipordm = false;
	boolean protect_twice = false;
	MyProgressBarTask pt;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				protect_twice = false;
			}
		}
	}

	private void wrong() {
		Toast toast = Toast.makeText(getApplicationContext(), "잘못된 형식의 도메인 또는 IP 주소 입니다.", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	private boolean isNum(int n) {
		if (n <= 255 && n >= 0)
			return true;
		return false;
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static int isIp(String str) {
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
		if (!isNotIp) {
			if (cnt != 3)
				return 3;
			return 1;
		}
		return 2;
	}

	private void nextActivity() {
		int diff;
		if (protect_twice)
			return;

		(pt = new MyProgressBarTask(this)).execute();

		protect_twice = true;
		str = txt_query.getText().toString();
		diff = isIp(str);
		if (str.length() < 4) {
			wrong();
			return;
		}
		Log.d("string res", "x1");
		if (diff == 1) { // IP
			Log.d("string res", "x2");
			Log.d("string res", str);
			String[] res = str.split("\\.");
			Log.d("string res", Integer.toString(res.length));
			for (int i = 0; i < res.length; i++) {
				Log.d("string res", res[i]);
				if (!isNumeric(res[i]) || !isNum(Integer.parseInt(res[i]))) {
					Log.d("string res", "wrong");
					wrong();
					return;
				}
			}
			ipordm = true;
		} else if (diff == 2) { // domain :: http:// , https:// deleting
			if (str.substring(0, 6).equals("http://"))
				str = str.substring(7);
			else if (str.substring(0, 7).equals("https://"))
				str = str.substring(8);
			ipordm = false;
		} else if (diff == 3) {
			wrong();
		} else {
			Log.d("tag", "Fatal!");
			wrong();
		}
		Handler handler = new Handler();
		handler.post(new intentRun(str));
	}

	class intentRun implements Runnable {
		String str;

		public intentRun(String s) {
			str = s;
		}

		public void run() {
			Intent intent = new Intent(SearchActivity.this, MainActivity.class);
			intent.putExtra("url", str);
			if (ipordm)
				intent.putExtra("isip", "1");
			else
				intent.putExtra("isip", "2");
			startActivityForResult(intent, 1);
			pt.dismiss();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		txt_query = (EditText) findViewById(R.id.editText1);
		txt_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!first)
					txt_query.setText("");
			}
		});
		txt_query.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					nextActivity();
				}
				return false;
			}
		});
		findViewById(R.id.imageButton1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextActivity();
			}
		});
	}
}
