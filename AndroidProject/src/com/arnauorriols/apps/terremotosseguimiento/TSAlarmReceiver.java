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
import android.content.ComponentName;
import android.content.pm.PackageManager;
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
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                               SystemClock.elapsedRealtime() + 10 * 1000,
                               AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                               pi);
        ComponentName receiver = new ComponentName(context, TSBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Log.v(RequestHelper.DEBUG_TAG, "Alarm is setted");
    }
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        Intent intent = new Intent (context, TSAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) {
            Log.v(RequestHelper.DEBUG_TAG, "Intent found, canceling alarm");
            am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            am.cancel(pi);
            if (PendingIntent.getBroadcast(context, 0, new Intent(context, TSAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) == null) {
                Log.v(RequestHelper.DEBUG_TAG, "Alarm successfully canceled");
            }
            ComponentName receiver = new ComponentName(context, TSBootReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
