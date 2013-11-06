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


/**
 * Implementation of the abstract class WakefulBroadcastReceiver, which is a
 * helper to start a service that ensures the devices awakes and doesn't go
 * back to sleep. It works as an alarm shedule: sets the time rate for a new
 * intent to get fired by AlarmManager (sort of cronjob) which is recived by
 * this same BroadcastReceiver, which in turn builds a new intent and starts a
 * new services with this intent, even if the device is sleep.
 *
 * It implements the code for setting this alarm at device boot.
 */
public class TSAlarmReceiver extends WakefulBroadcastReceiver {

    //AlarmManager am;
    //private PendingIntent pi;

    /** Creates a new intent to start TSService service */
    @Override
    public void onReceive(Context context, Intent intent){
        Intent service = new Intent(context, TSService.class);
        startWakefulService(context, service);
    }

    /**
     * Configures the alarm to fire once every 15 minutes (inexactRepeating).
     * AlarmManager will send a broadcast PendingIntent to this same class,
     * which will start the service when received. It also sets this alarm to
     * be set on device boot, using TSBootReceiver BroadcastReceiver.
     */
    public void setAlarm(Context context){
        AlarmManager am =
                (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent (context, TSAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                               SystemClock.elapsedRealtime() + 10 * 1000,
                               AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                               pi);

        /* Enabling this alarm to be set in every device boot */
        ComponentName receiver = new ComponentName(context,
                                                   TSBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                0);  // TODO: Trying without DONT_KILL_APP
        Log.v(RequestHelper.DEBUG_TAG, "Alarm is set");
    }


    /**
     * Gets any existing PendingIntents, it there is any means the alarm is
     * activated, cancelling it. It also cancels the alarm on device boot.
     *
     * TODO: Consider using settings instead, as they are going to be used
     * anyway.
     */
    public void cancelAlarm(Context context) {
        Intent intent = new Intent (context, TSAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                                                PendingIntent.FLAG_NO_CREATE);

        /* pi will be null if there isn't any PedingIntent with this intent */
        if (pi != null) {
            Log.v(RequestHelper.DEBUG_TAG, "Intent found, canceling alarm");
            AlarmManager am = (AlarmManager) context.getSystemService(
                                                        context.ALARM_SERVICE);
            am.cancel(pi);

            /* Cancel the alarm to be set on every device boot */
            ComponentName receiver = new ComponentName(context,
                                                       TSBootReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    0); // TODO: Trying without DONT_KILL_APP
        }
    }
}
