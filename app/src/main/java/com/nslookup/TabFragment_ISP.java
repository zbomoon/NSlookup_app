package com.nslookup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class TabFragment_ISP extends Fragment {
    private ArrayAdapter<String> m_Adapter;
    View mView;
    ListView mListView;
    ArrayList<String> items;
    LatLng SEOUL;
    private GoogleMap map;

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
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapIsp)).getMap();
        mListView = (ListView) mView.findViewById(R.id.lstIsp);
        ImageButton mBtnHelp = (ImageButton) mView.findViewById(R.id.btnHelp);
        m_Adapter = new ArrayAdapter<String>(mView.getContext(), R.layout.simple_textview, items);
        mListView.setAdapter(m_Adapter);
        mBtnHelp.setOnClickListener(new View.OnClickListener() {
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
        ((TextView) mView.findViewById(R.id.txtIP)).setTextColor(getResources().getColor(R.color.black));
        ((TextView) mView.findViewById(R.id.txtIP)).setText(str);
    }

    public void setGis(Double a, Double b) {
        SEOUL = new LatLng(a, b);
        Marker seoul = map.addMarker(new MarkerOptions().position(SEOUL).title("ISP"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public File getCapture() {
        final File fileCacheItem = new File("/sdcard/gps.jpg");
        map.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                if (snapshot == null)
                    Toast.makeText(TabFragment_ISP.this.getContext(), "null", Toast.LENGTH_SHORT).show();
                else {
                    OutputStream out = null;
                    try {
                        fileCacheItem.createNewFile();
                        out = new FileOutputStream(fileCacheItem);
                        snapshot.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return fileCacheItem;
    }
}