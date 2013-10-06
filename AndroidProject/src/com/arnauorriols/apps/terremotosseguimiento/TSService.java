/*
 * TSService.java
 *
 * Service for Terremotos Seguimiento. Performs network and parsing operations 
 * with RequestHelper, compares latest data and shows notification if new data
 * is received.
 *
 * Author: Arnau Orriols
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.app.IntentService;
import android.app.NotificationManager;
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
import java.util.HashMap;

public class TSService extends IntentService {

    private HashMap<String, String> formerLatest;

    private static final String fileName = "formerlatest";
    private static final int NOTIFICATION_ID = 001;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;

    public TSService(){
        super("TerremotosSeguimientoService");

        FileInputStream fis = null;
        try{
            fis = openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            formerLatest = (HashMap<String, String>) ois.readObject();
        }catch (FileNotFoundException e){
            formerLatest = new HashMap<String, String>();
            formerLatest.put("date", "");
            formerLatest.put("time", "");
            formerLatest.put("magnitude", "");
            formerLatest.put("location", "");
        }catch (Exception e){
            Log.e(RequestHelper.DEBUG_TAG, e.getMessage());
        }finally{
            if (fis != null) {
                try{
                    fis.close();
                }catch (IOException e){
                    Log.e(RequestHelper.DEBUG_TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent){
        HashMap<String, String> latestEQ = new RequestHelper(this)
                                                .fetchEarthquakeList(1).get(0);

        if (formerLatest != latestEQ) {
            String shortMsg = latestEQ.get("magnitude") + " -- " +
                       latestEQ.get("location");
            String bigMsg = latestEQ.get("time") + " -- " + latestEQ.get("date") +
                     "\n" + latestEQ.get("magnitude") + " -- " +
                     latestEQ.get("location");

            sendNotification(shortMsg, bigMsg);
        }

        FileOutputStream fos = null;
        try{
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(latestEQ);
            oos.flush();
        }catch (IOException e){
            Log.e(RequestHelper.DEBUG_TAG, e.getMessage());
        }finally{
            if (fos != null){
                try{
                    fos.close();
                }catch (IOException e){
                    Log.e(RequestHelper.DEBUG_TAG, e.getMessage());
                }
            }
        }
    }


    private void sendNotification(String shortMsg, String bigMsg) {
        nm = (NotificationManager) this.getSystemService(
                                        Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, TerremotosSeguimiento.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(getString(R.string.new_eq))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(bigMsg))
        .setContentText(shortMsg);
        builder.setContentIntent(contentIntent);
        nm.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
