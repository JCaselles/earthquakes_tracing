/*
 * TSAlarmReceiver
 *
 * Provides repeating alarm scheduling feature to Terremotos
 * Seguimiento. This class handles the alarm scheduling for firing the
 * IntentService every 15 minutes aprox. 
 *
 * Author: Arnau Orriols
 */
package com.arnauorriols.apps.terremotosseguimiento

import android.support.v4.content.WakefulBroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TSAlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent){
        Intent service = new Intent(context, TSService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context){
        am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent (context, TSAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                               AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                               AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                               pi)
    }
}
