package com.arnauorriols.apps.terremotosseguimiento;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.ArrayList;

public class TSFileLoader extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>{

    private Context context;

    public TSFileLoader (Context context){
        this.context = context;
    }

    public ArrayList<HashMap<String, String>> loadEarthquakesList(){
        ArrayList<HashMap <String, String>> eqList = null;
        FileInputStream fis = null;
        try{
            fis = context.openFileInput(TSService.LIST_LATEST);
            ObjectInputStream ois = new ObjectInputStream(fis);
            eqList = (ArrayList<HashMap<String, String>>) ois.readObject();
            fis.close();
        }catch (FileNotFoundException e){
            eqList = new RequestHelper(context).fetchEarthquakeList(2);
        }catch (Exception e){
            Log.e(RequestHelper.DEBUG_TAG, "Error: ", e);
            Toast toast = Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG);
        }
        return eqList;
    }

    protected ArrayList<HashMap<String, String>> doInBackground(Void... v) {
        return loadEarthquakesList();
    }

    protected void onProgressUpdate(Void... v){}
    protected void onPostExecute(ArrayList<HashMap<String, String>> result){
        TSListFragment.updateEqList(result);
    }

}

