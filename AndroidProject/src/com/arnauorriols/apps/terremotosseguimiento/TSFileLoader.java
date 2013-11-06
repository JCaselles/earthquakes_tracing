/*
 * TSFileLoader.java
 *
 * Implementation of an AsyncTask to load the earthquakes list when the app is
 * started.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * Implementation of AsyncTask to load from file or url the earthquakes' list.
 * If there is an existing LIST_LATEST in the app's system directory, it is
 * read. If the file doesn't exist yet, this is is feched from internet using
 * RequestHelper class. When done, EqList is updated and the ViewPager that
 * hostes the ListFragment is reseted to show the results.
 */
public class TSFileLoader extends AsyncTask<Void, Void, ArrayList
                                                <HashMap<String, String>>>{

    private Context context;

    public TSFileLoader (Context context){
        this.context = context;
    }

    /**
     * Reads from LIST_LATEST file, or fetches from url if file doesn't exist.
     * Returns the list of HashMaps with the data of all the earthquakes.
     */
    public ArrayList<HashMap<String, String>> loadEarthquakesList(){
        ArrayList<HashMap <String, String>> eqList = null;
        FileInputStream fis = null;
        try{
            fis = context.openFileInput(TSService.LIST_LATEST);
            ObjectInputStream ois = new ObjectInputStream(fis);
            eqList = (ArrayList<HashMap<String, String>>) ois.readObject();
            fis.close();
        }catch (FileNotFoundException e){
            Log.v(RequestHelper.DEBUG_TAG, "File not found. Fetching from url");
            eqList = new RequestHelper(context).fetchEarthquakeList(2);
        }catch (Exception e){
            Log.e(RequestHelper.DEBUG_TAG, "Error: ", e);
            Toast toast = Toast.makeText(context,
                    context.getString(R.string.unknown_error),
                                                    Toast.LENGTH_LONG);
        }
        return eqList;
    }

    protected ArrayList<HashMap<String, String>> doInBackground(Void... v) {
        return loadEarthquakesList();
    }
    protected void onProgressUpdate(Void... v){}
    protected void onPostExecute(ArrayList<HashMap<String, String>> result){
        TSListFragment.updateEqList(result);
        ((TerremotosSeguimiento)context).resetViewPager();
    }
}

