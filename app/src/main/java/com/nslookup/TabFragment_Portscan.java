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

import java.util.ArrayList;

public class TabFragment_Portscan extends Fragment {
    private ArrayAdapter<String> m_Adapter;
    private FloatingActionButton mFloatingButton;
    View mView;
    ListView mListView;
    ArrayList<String> items;
    String url = "";

    public TabFragment_Portscan() {
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
        mView = inflater.inflate(R.layout.tab_layout_portscan, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        mFloatingButton = (FloatingActionButton) mView.findViewById(R.id.mFloatingActionButton);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview_portscan, items);
        mListView.setAdapter(m_Adapter);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(TabFragment_Portscan.this.getActivity()).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.setMessage("해당 IP에 대한 포트스캔 결과입니다.\nOpen : 포트 열림\nClose : 포트 닫힘\nFiltered : 포트스캔 실패");
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
}