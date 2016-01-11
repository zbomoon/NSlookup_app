package com.nslookup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TabActivity extends Fragment {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    Boolean domainTab = false;
    ArrayList<String> items;
    String url = "";

    public TabActivity() {
        items = new ArrayList<String>();
        setRetainInstance(true);
    }

    public void setTabasDomaintab() {
        items = new ArrayList<String>();
        domainTab = true;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        setRetainInstance(true);
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (domainTab) mView = inflater.inflate(R.layout.tab1_layout, null);
        else mView = inflater.inflate(R.layout.tab2_layout, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview, items);
        mListView.setAdapter(m_Adapter);
        return mView;
    }

    public void Update() {
        m_Adapter.notifyDataSetChanged();
    }

    public void addItem(String str) {
        items.add(str);
    }

    public void setTextview(String str) {
        ((TextView) mView.findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) mView.findViewById(R.id.textView2)).setText(str);
    }

    public void addListener() {
        mListView.setOnItemClickListener(itemClickListenerOfLanguageList);
    }

    private OnItemClickListener itemClickListenerOfLanguageList = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id) {
            String ss = ((TextView) clickedView).getText().toString();
            String toastMessage = ss + " is selected.";
            Handler handler = new Handler();
            handler.post(new intentDomainInfo(ss));
        }
    };

    class intentDomainInfo implements Runnable {
        String str;

        public intentDomainInfo(String s) {
            str = s;
        }

        public void run() {
            Intent intent = new Intent(TabActivity.this.getContext(), DomainInfoActivity.class);
            intent.putExtra("url", str);
            startActivity(intent);
        }
    }
}