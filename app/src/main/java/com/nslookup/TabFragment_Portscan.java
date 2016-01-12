package com.nslookup;

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
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview_portscan, items);
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
}