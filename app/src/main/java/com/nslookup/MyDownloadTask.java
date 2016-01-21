package com.nslookup;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class MyDownloadTask {
    String urlToRead;
    String postParm;
    Boolean port_para = false;
    int time;

    public MyDownloadTask(String urlToRead, String postParm, int time) {
        this.urlToRead = urlToRead;
        this.postParm = postParm;
        this.time = time;
    }

    public MyDownloadTask(String urlToRead, String postParm, int time, Boolean b) {
        this.urlToRead = urlToRead;
        this.postParm = postParm;
        this.port_para = b;
        this.time = time;
    }

    public String GetString() throws Exception {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        BufferedWriter wd;
        String line;
        String result = "";
        url = new URL(urlToRead);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20 * 1000);
        conn.setReadTimeout(20 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        if (port_para)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        else
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("http.protocol.version", "HTTP/1.1");
        wd = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        wd.write(postParm);
        wd.flush();
        wd.close();
        if (conn.getErrorStream() != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String ss;
            while ((ss = br.readLine()) != null) {
                Log.d("errors", ss);
            }
        }

        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            result += line + "\n";
        }
        rd.close();
        return result;
    }
}