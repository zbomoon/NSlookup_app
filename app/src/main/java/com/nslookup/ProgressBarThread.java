package com.nslookup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

class MyProgressBarTask extends AsyncTask<Void, Void, Void> {
    ProgressDialog pb;
    Activity mActivity;
    public MyProgressBarTask(Activity ac) {
        mActivity = ac;
    }

    public void dismiss(){
        pb.dismiss();
    }
    public Void doInBackground(Void... params) {
        pb = new ProgressDialog(mActivity);
        pb.setProgress(5);
        pb.setTitle("NSlooking...");
        pb.setMessage("검색중입니다.\n잠시만 기다려주세요");
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.setCanceledOnTouchOutside(false);
        pb.show();
        return null;
    }
}
