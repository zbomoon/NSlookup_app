package com.nslookup;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TabFragment_Domain extends Fragment implements View.OnTouchListener {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    Button mButton;
    ArrayList<String> items;
    String url = "";
    Button btnSearchDomaininfo;

    public TabFragment_Domain() {
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
        mView = inflater.inflate(R.layout.tab_layout_domain, null);
        mListView = (ListView) mView.findViewById(R.id.t1_lv);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_single_choice_list, items);
        mListView.setAdapter(m_Adapter);
        mButton = (Button) mView.findViewById(R.id.button);
        mButton.setOnTouchListener(this);
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
        ((TextView) mView.findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) mView.findViewById(R.id.textView2)).setText(str);
    }

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
                if (id == -1) {
                    Toast toast = Toast.makeText(this.getContext(), "검색할 도메인을 선택해 주세요!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return true;
                }
                view = (Button) v;
                view.getBackground().clearColorFilter();
                view.invalidate();
                String ss = mListView.getItemAtPosition(id).toString();
                if (ss.contains("해당")) {
                    Toast toast = Toast.makeText(this.getContext(), "검색할 수 없습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return true;
                }
                String toastMessage = ss + " is selected.";
                Handler handler = new Handler();
                handler.post(new intentDomainInfo(ss));
        }
        return true;
    }


    class intentDomainInfo implements Runnable {
        String str;

        public intentDomainInfo(String s) {
            str = s;
        }

        public void run() {
            long nStart = System.currentTimeMillis();
            Intent intent = new Intent(TabFragment_Domain.this.getContext(), DomainInfoActivity.class);
            intent.putExtra("url", str);
            Log.d("Time1", Long.toString(System.currentTimeMillis() - nStart));
            startActivity(intent);
        }
    }
}