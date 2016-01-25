package com.nslookup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TabFragment_Domain extends Fragment implements View.OnTouchListener {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    Button mButton;
    ArrayList<String> items;

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
        mListView = (ListView) mView.findViewById(R.id.lstDomain);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_single_choice_list, items);
        mListView.setAdapter(m_Adapter);

        ImageButton mBtnHelp = (ImageButton) mView.findViewById(R.id.btnHelp);
        mButton = (Button) mView.findViewById(R.id.btnDomainsch);
        mButton.setOnTouchListener(this);
        mBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(TabFragment_Domain.this.getActivity()).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.setMessage("IP에 연결된 도메인 조회 결과입니다.\n사이트에 따라 정상적 조회가 되지 않을수도 있습니다.\n원하는 IP를 선택 후 도메인 정보를 추가로 검색할 수 있습니다.");
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
                if (ss.equals("") || ss.contains("해당")) {
                    Toast toast = Toast.makeText(this.getContext(), "검색할 수 없습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return true;
                }
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
            startActivity(intent);
        }
    }
}