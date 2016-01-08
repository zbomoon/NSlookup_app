package com.nslookup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TabActivity extends Fragment {
    String[] items;
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    Boolean domainTab = false;
    String url = "";

    public TabActivity() {
    }

    public TabActivity(String[] strs) {
        items = strs;
    }

    public TabActivity(String[] strs, Boolean Domainchk) {
        items = strs;
        domainTab = Domainchk;
    }

    public TabActivity(String[] strs, String url) {
        items = strs;
        this.url = url;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        setRetainInstance(true);
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (url != "") mView = inflater.inflate(R.layout.tab1_layout, null);
        else mView = inflater.inflate(R.layout.tab2_layout, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        Log.d("items", items[0]);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview, items);

        Log.i("xx", mListView != null ? "listview is not null!" : "listview is null!");
        mListView.setAdapter(m_Adapter);
        if (domainTab && !items[0].equals("해당 IP를 찾을 수 없습니다."))
            mListView.setOnItemClickListener(itemClickListenerOfLanguageList);
        if (url != "")
            ((TextView) mView.findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.white));
        if (url != "")
            ((TextView) mView.findViewById(R.id.textView2)).setText("Search IP : " + url);
        return mView;
    }

    private OnItemClickListener itemClickListenerOfLanguageList = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id) {
            String ss = ((TextView) clickedView).getText().toString();
            String toastMessage = ss + " is selected.";
            Log.d("list", toastMessage);
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