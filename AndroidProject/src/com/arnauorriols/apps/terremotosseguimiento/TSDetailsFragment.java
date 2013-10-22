package com.arnauorriols.apps.terremotosseguimiento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;

import java.util.HashMap;
import java.util.ArrayList;


public class TSDetailsFragment extends Fragment {

    private TextView title;
    private TextView time;
    private TextView date;
    private TextView magnitude;
    private TextView location;
    private OnFragmentReadyListener ofrl;

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
        try {
            ofrl = (OnFragmentReadyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                                " must implement OnFragmentReadyListener.");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_last, container, false);
        return fragView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        title = (TextView) getView().findViewById(R.id.title);
        time  = (TextView) getView().findViewById(R.id.time);
        date = (TextView) getView().findViewById(R.id.date);
        magnitude = (TextView) getView().findViewById(R.id.magnitude);
        location = (TextView) getView().findViewById(R.id.location);
        ofrl.onFragmentReady();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Intent intent = getActivity().getIntent();
            ArrayList<HashMap<String, String>> eqData = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TSService.EQ_DATA);
            boolean details = false;
            if (eqData == null) {
                eqData = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TSListFragment.INTENT_DETAILS);
                details = true;
            }
            if (eqData != null){ 
                Log.v(RequestHelper.DEBUG_TAG, "eqData == " + eqData.toString());
                HashMap<String, String> eqLast = eqData.get(0);
                if (details) {
                    title.setText(getString(R.string.details_title));
                }else{
                    title.setText(getString(R.string.last_eq_title));
                }
                time.setText(getString(R.string.time_label) +
                             "\t\t" + eqLast.get("time"));
                date.setText(getString(R.string.date_label) +
                             "\t\t" + eqLast.get("date"));
                magnitude.setText(getString(R.string.magnitude_label) +
                                  "\t\t" + eqLast.get("magnitude"));
                location.setText(getString(R.string.location_label) +
                                 "\t\t" + eqLast.get("location"));
            }else{
                title.setText(getString(R.string.usage_title));
                time.setText(getString(R.string.usage_explanation));
            }
        }
    }

    /**
     * Interface that should implement the activity. It will be called when the
     * fragment is fully loaded: when onViewCreated is called.
     */
    public interface OnFragmentReadyListener {
        public void onFragmentReady();
    }

}

