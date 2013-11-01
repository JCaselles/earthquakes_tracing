/*
 * TSListFragment.java
 *
 * Fragment that presents the list of the last earthquakes of the given
 * time range.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Definition of The ListFragment that holds all the earthquakes for
 * the given time range (to be flexibilized). Holds the actual
 * earthquake data list as a private static variable (eqList). All other
 * classes can update this list by calling updateEqList with the new
 * list to be hold. 
 *
 * Requires the Activity to which this Fragment is attached to implement
 * the interface OnRowSelectedListener, which serves as a callback for
 * passing the selected earthquake data HashMap to the Activity on
 * onListItemClick.
 */
public class TSListFragment extends ListFragment {

    public static final String INTENT_DETAILS =
            "com.arnauorriols.apps.terremotosseguimiento.INTENTDETAILS";
    private static final String[] FROM_LIST = {"time",
                                             "date",
                                             "magnitude",
                                             "location"};
    private static final int[] TO_LIST = {R.id.row_time,
                                          R.id.row_date,
                                          R.id.row_magnitude,
                                          R.id.row_location};
    private static ArrayList<HashMap<String, String>> eqList;

    private OnRowSelectedListener orsl;


    @Override
    public void onResume(){
        super.onResume();
        getListView().setCacheColorHint(0); // Fixes white color when scrolling
        if (eqList == null){
        Log.v(RequestHelper.DEBUG_TAG, "eqList is null!");
        eqList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> eqRow = new HashMap<String, String>();
        eqRow.put("time", "loading data...");
        eqRow.put("date", "");
        eqRow.put("magnitude", "");
        eqRow.put("location", "");
        eqList.add(eqRow);
        }
        refreshList();
    }

    /**
     * Any class willing to update the earthquakes data list should
     * call this method.
     */
    public static void updateEqList (
                            ArrayList<HashMap<String, String>> _eqList){
        eqList = _eqList;
    }

    /**
     * Recreates the list adapter so to refresh the list content.
     * TODO: Should work with notifyDataSetChange.
     */
    public void refreshList (){
        SimpleAdapter sa = new SimpleAdapter(getActivity(),
                                             eqList,
                                             R.layout.row_layout,
                                             FROM_LIST,
                                             TO_LIST);
        setListAdapter(sa);
    }

    /**
     * Makes sure that the activity this Fragment is attaching
     * implements OnRowSelectedListener interface. Throws
     * ClassCastException if not.
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            orsl = (OnRowSelectedListener) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                                    "Must implement OnRowSelectedListener");
        }
    }

    /**
     * Passes the HashMap related to the clicked item to the Activity.
     */
    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        orsl.onRowSelected(eqList.get(position));
    }

    public interface OnRowSelectedListener {
        public void onRowSelected(HashMap<String, String> eqData);
    }
}

