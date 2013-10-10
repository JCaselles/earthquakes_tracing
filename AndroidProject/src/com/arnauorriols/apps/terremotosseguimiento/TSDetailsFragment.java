package com.arnauorriols.apps.terremotosseguimiento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import java.util.HashMap;
import java.util.ArrayList;


public class TSDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_last, container, false);
        return fragView;
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = getActivity().getIntent();
        ArrayList<HashMap<String, String>> eqData = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TSService.EQ_DATA);

        if (eqData != null){ 
            HashMap<String, String> eqLast = eqData.get(0);
            TextView title = (TextView) getView().findViewById(R.id.title);
            TextView time  = (TextView) getView().findViewById(R.id.time);
            TextView date = (TextView) getView().findViewById(R.id.date);
            TextView magnitude = (TextView) getView().findViewById(R.id.magnitude);
            TextView location = (TextView) getView().findViewById(R.id.location);

            title.setText(getString(R.string.display_title));
            time.setText(getString(R.string.time_label) +
                         "\t\t" + eqLast.get("time"));
            date.setText(getString(R.string.date_label) +
                         "\t\t" + eqLast.get("date"));
            magnitude.setText(getString(R.string.magnitude_label) +
                              "\t\t" + eqLast.get("magnitude"));
            location.setText(getString(R.string.location_label) +
                             "\t\t" + eqLast.get("location"));
        }else{
            TextView title = (TextView) getView().findViewById(R.id.title);
            TextView help = (TextView) getView().findViewById(R.id.time);
            title.setText(getString(R.string.usage_title));
            help.setText(getString(R.string.usage_explanation));
        }
    }

}

