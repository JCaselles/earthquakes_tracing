/*
 * RequestHelper
 *
 * Performs the http request querying the IGN (instituto Geográfico
 * Nacional) website for the latest earthquake occurred in Spain.
 *
 * Date: 05/10/2013
 *
 * Author: Arnau Orriols
 */

package com.arnauorriols.apps.terremotosseguimiento;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.content.Context;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.helper.*;
import org.jsoup.parser.*;
import org.jsoup.safety.*;

class RequestHelper {

    private static final String  DEBUG_TAG = "REQUESTHELPER";

    private static final String BASE_URL = "http://www.ign.es/ign/layoutIn/"
                                           + "sismoListadoTerremotos.do";

    private Context context;

    public RequestHelper (Context context){
        this.context = context;
    }
    private boolean checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public List<HashMap<String, String>> fetchEarthquakeList(int listDays) throws IOException{

        //InputStream is = null;

        //try {
        //        URL url = new URL(BASE_URL);
        //        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //        conn.setReadTimeout(10000 /* milliseconds */);
        //        conn.setConnectTimeout(15000 /* milliseconds */);
        //        conn.setRequestMethod("GET");
        //        conn.setDoInput(true);
                // Starts the query
        //        conn.connect();
        //        int response = conn.getResponseCode();
        //        Log.d(DEBUG_TAG, "The response is: " + response);
        //        is = conn.getInputStream();

                // Convert the InputStream into a string
        //        String contentAsString = readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        //    } finally {
        //        if (is != null) {
        //            is.close();
        //        } 
        //    }
        //
        //    THIS IS RATHER TOO COMPLEX, OLD-FASHION. WILL TRY TO USE
        //    JSOUP BUILTIN CONNECTION FEATURE. KEEPING IT FOR
        //    EDUCATIONAL PURPOSES.


        HashMap<String, String> queryString = new HashMap<String, String>();
        queryString.put("zona", "1");
        queryString.put("cantidad_dias", Integer.toString(listDays));
        List<HashMap<String, String>> eqList = new ArrayList<HashMap<String, String>>();
        if (checkNetwork()){
            try{
                Document webContent = Jsoup.connect(BASE_URL).data(queryString).get();
                Elements eqTableRows = webContent.select("tr.filaNegra2");
                for (Element row : eqTableRows){
                    Elements children = row.children();
                    HashMap<String, String> eqData = new HashMap<String, String>();
                    eqData.put("date", children.get(1).text());
                    eqData.put("time", children.get(2).text());
                    eqData.put("magnitude", children.get(7).text());
                    eqData.put("location", children.get(9).text());
                    eqList.add(eqData);
                }
            }catch (IOException e){
                Log.e(DEBUG_TAG, "Error connecting with Jsoup to " + BASE_URL);
            }finally{
                return eqList;
            }
        }else{
            return null;
        }
    }

    //private String readIt(InputStream is) throws IOException{
    //    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    //    String result = null;
    //    String line = null;
    //    try{
    //        while ((line = br.readLine()) != null){
    //            result += line;
    //        }
    //    }finally{
    //        return result;
    //    }
    //}


    }

