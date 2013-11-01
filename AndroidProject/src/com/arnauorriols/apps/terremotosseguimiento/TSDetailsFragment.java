/*
 * TSDetailsFragment.java
 *
 * Fragment to show the details of each earthquake. This fragment
 * corresponds to tab "detalles", and may show the usage explanation
 * when no earthquake is selected.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.ArrayList;


/**
 * Child class of Fragment, it defines the details fragment behaviour.
 * Depending on the extra data of the intent present in the activity,
 * it either presents this extra data or the usage explanation.
 *
 * Requires the activity that holds this fragment to implement
 * OnFragmentReadyListener, which will function as the callback to tell
 * the Activity that onCreateView has been finished, and all layout
 * references are available.
 */
public class TSDetailsFragment extends Fragment {

    private TextView title;
    private TextView time;
    private TextView date;
    private TextView magnitude;
    private TextView location;

    private OnFragmentReadyListener ofrl;

    /**
     * Throws a ClassCastException if the activity that this Fragment
     * attaches to haven't implemented OnFragmentReadyListener.
     */
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
    public View onCreateView (LayoutInflater inflater,
                        ViewGroup container, Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_last,
                                                    container, false);
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


    /**
     * All setting of content to the TextViews is performed here. It has
     * to be this way to update the data on each list's item click of
     * TSListFragment, because as this fragment is part of a ViewPager,
     * it's loaded at the same time as the list.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Intent intent = getActivity().getIntent();
            ArrayList<HashMap<String, String>> eqData =
                    (ArrayList<HashMap<String, String>>)
                            intent.getSerializableExtra(TSService.EQ_DATA);
            boolean details = false;

            if (eqData == null) {
                /* Different intents for whether it comes from the
                 * notification(EQ_DATA) or the ListFragment (INTENT_DETAILS).
                 */
                eqData = (ArrayList<HashMap<String, String>>)
                        intent.getSerializableExtra(
                                            TSListFragment.INTENT_DETAILS);
                details = true;
            }

            if (eqData != null){
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

