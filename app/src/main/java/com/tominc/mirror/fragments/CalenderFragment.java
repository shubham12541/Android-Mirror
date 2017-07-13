package com.tominc.mirror.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tominc.mirror.MainActivity;
import com.tominc.mirror.R;
import com.tominc.mirror.Utility;

import java.util.Calendar;
import java.util.List;

import de.mateware.snacky.Snacky;
import it.macisamuele.calendarprovider.EventInfo;

/**
 * Created by shubham on 07/07/17.
 */

public class CalenderFragment extends Fragment {
    TextView agenda_list;
    Utility utility;

    private static final String TAG = "CalenderFragment";
    private static final int CALENDER_PERMISSION = 102;

    public CalenderFragment(){
        utility = Utility.getInstance(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.calender_layout, container, false);

        agenda_list = (TextView) root.findViewById(R.id.agenda_list);

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        int result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);

        if(result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED){
            fetchCalender();
        } else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, CALENDER_PERMISSION);
        }
        return root;
    }

    private void showAgendaOnUI(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            agenda_list.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else{
            agenda_list.setText(Html.fromHtml(text));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CALENDER_PERMISSION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    fetchCalender();
                } else{
                    Snacky.builder()
                            .setText("Permission Denied")
                            .setActivty(getActivity())
                            .warning()
                            .show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void fetchCalender(){
        String agenda_text = "<b>Today: </b><br>";
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        Calendar endOfToday = Calendar.getInstance();
        endOfToday.set(Calendar.HOUR_OF_DAY, 23);
        List<EventInfo> todayEvents = EventInfo.getEvents(getActivity(), today.getTime(), endOfToday.getTime(), null, null);
        for(EventInfo event: todayEvents){
            agenda_text += event.getTitle() + "<br>";
        }

        agenda_text += "<b>Tommorrow</b><br>";
        today.add(Calendar.DATE, 1);
        endOfToday.add(Calendar.DATE, 1);
        List<EventInfo> tomEvents = EventInfo.getEvents(getActivity(), today.getTime(), endOfToday.getTime(), null, null);
        for(EventInfo event: tomEvents){
            agenda_text += event.getTitle() + "<br>";
        }

    }
}
