package com.nslookup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TabFragment_Server extends Fragment {
    View mView;
    TableLayout tb;
    ArrayList<String>[] items;

    public TabFragment_Server() {
        items = (ArrayList<String>[]) new ArrayList[4];
        items[0] = new ArrayList<String>();
        items[1] = new ArrayList<String>();
        items[2] = new ArrayList<String>();
        items[3] = new ArrayList<String>();
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        setRetainInstance(true);
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FloatingActionButton mFloatingButton;
        mView = inflater.inflate(R.layout.tab_layout_server, null);
        tb = (TableLayout) mView.findViewById(R.id.tableLayout);
        mFloatingButton = (FloatingActionButton) mView.findViewById(R.id.mFloatingActionButton);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(TabFragment_Server.this.getActivity()).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.setMessage("해당 IP에 대한 서버 히스토리 조회결과입니다.");
                ab.show();
            }
        });
        return mView;
    }

    public void Update() {
        for (int i = 0; i < items[0].size(); i++) {
            TableRow tr = new TableRow(this.getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setGravity(Gravity.CENTER);
            for (int j = 0; j < 4; j++) {
                TextView tv = new TextView(this.getActivity());
                tv.setText(items[j].get(i));
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setBackgroundResource(R.drawable.xml_border);
                tr.addView(tv);
            }
            tb.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void addItem(String ip, String os, String ws, String dt) {
        items[0].add(dt);
        items[1].add(ip);
        items[2].add(os);
        if(ws.length() > 20)
            items[3].add(ws.substring(0, 20));
        else items[3].add(ws);
    }
}