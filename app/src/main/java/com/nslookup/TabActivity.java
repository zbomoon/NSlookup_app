package com.nslookup;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TabActivity extends Fragment {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    Boolean ISPTab = false;
    Boolean domainTab = false;
    ArrayList<String> items;
    String url = "";
    Button btnSearchDomaininfo;

    public TabActivity() {
        items = new ArrayList<String>();
        setRetainInstance(true);
    }

    public void setTabasISPtab() {
        ISPTab = true;
    }

    public void setTabasDomaintab() {
        domainTab = true;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        setRetainInstance(true);
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (ISPTab) mView = inflater.inflate(R.layout.tab1_layout, null);
        else if (domainTab) mView = inflater.inflate(R.layout.tab3_layout, null);
        else mView = inflater.inflate(R.layout.tab2_layout, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        if (domainTab) {
            btnSearchDomaininfo = (Button) mView.findViewById(R.id.button);
            btnSearchDomaininfo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Button view;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            view = (Button) v;
                            view.getBackground().setColorFilter(0x88000000, PorterDuff.Mode.SRC_ATOP);
                            v.invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            int id = mListView.getCheckedItemPosition();
                            view = (Button) v;
                            view.getBackground().clearColorFilter();
                            view.invalidate();
                            String ss = mListView.getItemAtPosition(id).toString();
                            String toastMessage = ss + " is selected.";
                            Handler handler = new Handler();
                            handler.post(new intentDomainInfo(ss));
                    }
                    return true;
                }
            });
        }
        if (domainTab) mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (domainTab)
            m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_single_choice_list, items);

        else
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

    class intentDomainInfo implements Runnable {
        String str;

        public intentDomainInfo(String s) {
            str = s;
        }

        public void run() {
            long nStart = System.currentTimeMillis();
            Intent intent = new Intent(TabActivity.this.getContext(), DomainInfoActivity.class);
            intent.putExtra("url", str);
            Log.d("Time1", Long.toString(System.currentTimeMillis() - nStart));
            startActivity(intent);
        }
    }
}