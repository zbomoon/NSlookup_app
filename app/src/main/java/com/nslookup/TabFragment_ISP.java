package com.nslookup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TabFragment_ISP extends Fragment {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    ArrayList<String> items;

    public TabFragment_ISP() {
        items = new ArrayList<String>();
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
        mView = inflater.inflate(R.layout.tab_layout_isp, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        mFloatingButton = (FloatingActionButton) mView.findViewById(R.id.mFloatingActionButton);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview, items);
        mListView.setAdapter(m_Adapter);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(TabFragment_ISP.this.getActivity()).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.setMessage("해당 IP에 대한 ISP 정보입니다.");
                ab.show();
            }
        });
        return mView;
    }

    public void Update() {
        m_Adapter.notifyDataSetChanged();
    }

    public void addItem(String str) {
        items.add(str);
    }

    public void setTextview(String str) {
        ((TextView) mView.findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.black));
        ((TextView) mView.findViewById(R.id.textView2)).setText(str);
    }
}