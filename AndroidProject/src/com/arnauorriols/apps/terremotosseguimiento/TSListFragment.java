package com.arnauorriols.apps.terremotosseguimiento;

import android.support.v4.app.Fragment;
import android.widget.SimpleAdapter;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;

public class TSListFragment extends ListFragment {

    private static final String[] FROM_LIST = {"time",
                                             "date",
                                             "magnitude",
                                             "location"};

    private static final int[] TO_LIST = {R.id.row_time,
                                          R.id.row_date,
                                          R.id.row_magnitude,
                                          R.id.row_location};

    private static List<HashMap<String, String>> eqList;

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        Log.v(RequestHelper.DEBUG_TAG, "eqList: " + eqList.toString());
        SimpleAdapter sa = new SimpleAdapter(getActivity(),
                                             eqList,
                                             R.layout.row_layout,
                                             FROM_LIST,
                                             TO_LIST);

        setListAdapter(sa);
    }

    public static void updateEqList (List<HashMap<String, String>> _eqList){
        eqList = _eqList;
        Log.v(RequestHelper.DEBUG_TAG, "static eqList updated: " + eqList.toString());
    }
}

