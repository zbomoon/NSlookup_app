package com.nslookup;

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
    String url = "";

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
        mView = inflater.inflate(R.layout.tab_layout_isp, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview, items);
        mListView.setAdapter(m_Adapter);
        return mView;
    }

    public ListView getListView() {
        return mListView;
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