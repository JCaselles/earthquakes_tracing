/*
 * TSService.java
 *
 * Service for Seísmo Adviser. Performs network and parsing operations
 * with an instance of RequestHelper. It is fired every 15 minutes, and
 * compares the new downloaded data with the old one, prompting a
 * notification of new data differs.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.OptionalDataException;
import java.lang.NullPointerException;
import java.util.HashMap;
import java.util.ArrayList;

public class TSService extends IntentService {

    public static final String LAST_LATEST = "lastlatest.ser";
    public static final String LIST_LATEST = "latestlist.ser";
    public static final String EQ_DATA =
                        "com.arnauorriols.apps.terremotosseguimiento.EQDATA";
    private static final int NOTIFICATION_ID = 001;

    private HashMap<String, String> lastLatest;


    public TSService(){

        /* Debug related requirement from Android */
        super("TerremotosSeguimientoService");
    }


    @Override
    protected void onHandleIntent(Intent intent){
        RequestHelper rh = new RequestHelper(this);
        if(rh.checkNetwork()){
            FileInputStream fis = null;
            try{
                fis = openFileInput(LAST_LATEST);
                ObjectInputStream ois = new ObjectInputStream(fis);
                lastLatest = (HashMap<String, String>) ois.readObject();
                Log.v(RequestHelper.DEBUG_TAG, "Former earthquake data found.");
                Log.v(RequestHelper.DEBUG_TAG, "lastLatest = " + lastLatest.toString());

            }catch (FileNotFoundException e){
                Log.v(RequestHelper.DEBUG_TAG, "Fresh start. No earthquake data found");
                lastLatest = new HashMap<String, String>();
                lastLatest.put("date", "");
                lastLatest.put("time", "");
                lastLatest.put("magnitude", "");
                lastLatest.put("location", "");
            }catch (NullPointerException e){
                Log.v(RequestHelper.DEBUG_TAG, "NullPointerException", e);
            }catch (StreamCorruptedException e){
                Log.e(RequestHelper.DEBUG_TAG, "corrupted Stream when opening fis");
            }catch (ClassNotFoundException e){
                Log.e(RequestHelper.DEBUG_TAG, "ClassNotFoundException when opening fis");
            }catch (IOException e){
                Log.e(RequestHelper.DEBUG_TAG, "IOException when opening fis");
            }finally{
                if (fis != null) {
                    try{
                        fis.close();
                    }catch (IOException e){
                        Log.e(RequestHelper.DEBUG_TAG, "IOException when closing fis");
                    }
                }
            }

            HashMap<String, String> latestEQ = rh.fetchLastEarthquake();

            if (!lastLatest.equals(latestEQ)) {
                Log.v(RequestHelper.DEBUG_TAG, "new eq! former = " + lastLatest.toString() + 
                                               ", latest = " + latestEQ.toString());

                String shortMsg = latestEQ.get("magnitude") + " -- " +
                                               latestEQ.get("location");
                String bigMsg = latestEQ.get("time") + " -- " + latestEQ.get("date") +
                                        "\n" + latestEQ.get("magnitude") + " -- " +
                                        latestEQ.get("location");
                ArrayList <HashMap<String, String>> listLatest = rh.fetchEarthquakeList(2);
                sendNotification(shortMsg, bigMsg, listLatest);
                FileOutputStream fos = null;
                try{
                    fos = openFileOutput(LAST_LATEST, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(latestEQ);
                    oos.flush();
                    fos.close();
                    fos = openFileOutput(LIST_LATEST, Context.MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(listLatest);
                    Log.v(RequestHelper.DEBUG_TAG, "Writting files. whole list: " + listLatest.toString());
                    oos.flush();
                }catch (IOException e){
                    Log.e(RequestHelper.DEBUG_TAG, "IOException when opening fos");
                }finally{
                    if (fos != null){
                        try{
                            Log.v(RequestHelper.DEBUG_TAG, "Files saved, closing fos");
                            fos.close();
                        }catch (IOException e){
                            Log.e(RequestHelper.DEBUG_TAG, "IOException when closing fos");
                        }
                    }
                }
            }else{
                Log.v(RequestHelper.DEBUG_TAG, "No new earthquakes. We are safe");
            }
        }
    }


    /**
     * Builds and fires a notification with the new earthquake data and
     * an intent to be sent on the notification's click. This itent
     * carries the list with all the earthquakes registered, each with
     * in its proper dict.
     */
    private void sendNotification(String shortMsg, String bigMsg,
                                ArrayList<HashMap<String, String>> eqData) {

        NotificationManager nm =
                (NotificationManager) this.getSystemService(
                                                Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, TerremotosSeguimiento.class);
        intent.putExtra(EQ_DATA, (ArrayList) eqData);
        PendingIntent contentIntent = PendingIntent.getActivity(
                        this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT |
                           Intent.FLAG_ACTIVITY_NEW_TASK); /* Required */

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.new_eq))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                                        .bigText(bigMsg))
                        .setContentText(shortMsg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

        builder.setContentIntent(contentIntent);
        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
