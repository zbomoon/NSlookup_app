package com.nslookup;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HP-01 on 2016-01-07.
 */

class MyDownloadTask {
    String urlToRead;
    String postParm;
    Boolean port_para = false;

    public MyDownloadTask(String urlToRead, String postParm) {
        this.urlToRead = urlToRead;
        this.postParm = postParm;
    }

    public MyDownloadTask(String urlToRead, String postParm, Boolean b) {
        this.urlToRead = urlToRead;
        this.postParm = postParm;
        this.port_para = b;
    }

    public String GetString() {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        BufferedWriter wd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}