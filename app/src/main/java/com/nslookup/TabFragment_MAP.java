package com.nslookup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TabFragment_MAP extends Fragment {
    View mView;
    LatLng SEOUL;
    private GoogleMap map;

    public TabFragment_MAP() {
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
        mView = inflater.inflate(R.layout.tab_layout_map, null);
        map = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        mFloatingButton = (FloatingActionButton) mView.findViewById(R.id.mFloatingActionButton);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(TabFragment_MAP.this.getActivity()).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.setMessage("해당 ISP 업체 위치 정보입니다.");
                ab.show();
            }
        });
        return mView;
    }

    public void setTextview(String str) {
        ((TextView) mView.findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.black));
        ((TextView) mView.findViewById(R.id.textView2)).setText(str);
    }

    public void setGis(Long a, Long b) {
        SEOUL = new LatLng(a, b);
        Marker seoul = map.addMarker(new MarkerOptions().position(SEOUL).title("ISP"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }
}