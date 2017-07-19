package com.tominc.mirror.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import it.macisamuele.calendarprovider.EventInfo;

/**
 * Created by shubham on 07/07/17.
 */

public class CalenderFragment extends Fragment {
    TextView agenda_list;
    Utility utility;

    public CalenderFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        utility = Utility.getInstance(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.calender_layout, container, false);

        agenda_list = (TextView) root.findViewById(R.id.agenda_list);
        fetchCalender();
        return root;
    }

    private void showAgendaOnUI(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            agenda_list.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else{
            agenda_list.setText(Html.fromHtml(text));
        }
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
