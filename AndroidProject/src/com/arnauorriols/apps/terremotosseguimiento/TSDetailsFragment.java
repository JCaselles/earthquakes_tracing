package com.arnauoriols.apps.terremotosseguimiento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import java.util.HashMap;


public class TSDetailsFragment extends Fragment {
    private String mText;

    public TabContentFragment(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

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
        HashMap<String, String> eqData = (HashMap<String, String>) intent.getSerializableExtra(TSService.EQ_DATA);

        if (eqData != null){ 
            TextView time  = (TextView) getView().findViewById(R.id.time);
            TextView date = (TextView) getView().ffindViewById(R.id.date);
            TextView magnitude = (TextView) getView().ffindViewById(R.id.magnitude);
            TextView location = (TextView) getView().ffindViewById(R.id.location);

            time.setText(getString(R.string.time_label) +
                         "\t\t" + eqData.get("time"));
            date.setText(getString(R.string.date_label) +
                         "\t\t" + eqData.get("date"));
            magnitude.setText(getString(R.string.magnitude_label) +
                              "\t\t" + eqData.get("magnitude"));
            location.setText(getString(R.string.location_label) +
                             "\t\t" + eqData.get("location"));
        }
    }

}

