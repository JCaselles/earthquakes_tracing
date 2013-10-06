/*
 * TSAlarmReceiver
 *
 * Provides repeating alarm scheduling feature to Terremotos
 * Seguimiento. This class handles the alarm scheduling for firing the
 * IntentService every 15 minutes aprox. 
 *
 * Author: Arnau Orriols
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.support.v4.content.WakefulBroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.SystemClock;

public class TSAlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent){
        Log.v(RequestHelper.DEBUG_TAG, "Service is fired");
        Intent service = new Intent(context, TSService.class);
        startWakefulService(context, service);

    }

    public void setAlarm(Context context){
        am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent (context, TSAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                               SystemClock.elapsedRealtime() + 10 * 1000,
                               AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                               pi);
        Log.v(RequestHelper.DEBUG_TAG, "Alarm is setted");
    }
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (am != null) {
            am.cancel(pi);
        }
    }
}
