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
import java.lang.NullPointerException;
import java.io.StreamCorruptedException;
import java.io.OptionalDataException;
import java.util.HashMap;

public class TSService extends IntentService {

    private HashMap<String, String> formerLatest;

    private static final String LAST_LATEST = "lastlatest.ser";
    private static final String LIST_LATEST = "latestlist.ser";
    private static final int NOTIFICATION_ID = 001;
    public static final String EQ_DATA = "com.arnauorriols.apps.terremotosseguimiento.EQDATA";
    private NotificationManager nm;
    //private NotificationCompat.Builder builder;

    public TSService(){
        super("TerremotosSeguimientoService");


    }

    @Override
    protected void onHandleIntent(Intent intent){
        FileInputStream fis = null;
        try{
            fis = openFileInput(LAST_LATEST);
            ObjectInputStream ois = new ObjectInputStream(fis);
            formerLatest = (HashMap<String, String>) ois.readObject();
            Log.v(RequestHelper.DEBUG_TAG, "Former earthquake data found.");
            Log.v(RequestHelper.DEBUG_TAG, "formerLatest = " + formerLatest.toString());

        }catch (FileNotFoundException e){
            Log.v(RequestHelper.DEBUG_TAG, "Fresh start. No earthquake data found");
            formerLatest = new HashMap<String, String>();
            formerLatest.put("date", "");
            formerLatest.put("time", "");
            formerLatest.put("magnitude", "");
            formerLatest.put("location", "");
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
        RequestHelper rh = new RequestHelper(this);
        HashMap<String, String> latestEQ = rh.fetchLastEarthquake();

        if (!formerLatest.equals(latestEQ)) {
            Log.v(RequestHelper.DEBUG_TAG, "new eq! former = " + formerLatest.toString() + 
                                           ", latest = " + latestEQ.toString());

            String shortMsg = latestEQ.get("magnitude") + " -- " +
                                           latestEQ.get("location");
            String bigMsg = latestEQ.get("time") + " -- " + latestEQ.get("date") +
                                    "\n" + latestEQ.get("magnitude") + " -- " +
                                    latestEQ.get("location");
            sendNotification(shortMsg, bigMsg, latestEQ);
            List <HashMap<String, String>> listLatest = rh.fetchEarthquakeList(2);
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
                oos.flush();
            }catch (IOException e){
                Log.e(RequestHelper.DEBUG_TAG, "IOException when opening fos");
            }finally{
                if (fos != null){
                    try{
                        Log.v(RequestHelper.DEBUG_TAG, "File saved");
                        fos.close();
                    }catch (IOException e){
                        Log.e(RequestHelper.DEBUG_TAG, "IOException when closing fos");
                    }
                }
            }
        }
    }


    private void sendNotification(String shortMsg, String bigMsg, HashMap<String, String> eqData) {
        nm = (NotificationManager) this.getSystemService(
                                        Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, TerremotosSeguimiento.class).putExtra(EQ_DATA, eqData), PendingIntent.FLAG_UPDATE_CURRENT | Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(getString(R.string.new_eq))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(bigMsg))
        .setContentText(shortMsg)
        .setAutoCancel(true);
        builder.setContentIntent(contentIntent);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

}
