package com.arnauorriols.apps.terremotosseguimiento;

import android.support.v4.app.Fragment;
import android.widget.SimpleAdapter;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.os.Bundle;

import java.util.HashMap;
import java.util.ArrayList;

public class TSListFragment extends ListFragment {

    private static final String[] FROM_LIST = {"time",
                                             "date",
                                             "magnitude",
                                             "location"};

    private static final int[] TO_LIST = {R.id.row_time,
                                          R.id.row_date,
                                          R.id.row_magnitude,
                                          R.id.row_location};

    private static ArrayList<HashMap<String, String>> eqList;

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated (savedInstanceState);
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
        Log.v(RequestHelper.DEBUG_TAG, "eqList: " + eqList.toString());
        refreshList();
    }

    public static void updateEqList (ArrayList<HashMap<String, String>> _eqList){
        eqList = _eqList;
        Log.v(RequestHelper.DEBUG_TAG, "static eqList updated: " + eqList.toString());
    }

    public void refreshList (){
        SimpleAdapter sa = new SimpleAdapter(getActivity(),
                                            eqList,
                                            R.layout.row_layout,
                                            FROM_LIST,
                                            TO_LIST);
        setListAdapter(sa);
    }
}

