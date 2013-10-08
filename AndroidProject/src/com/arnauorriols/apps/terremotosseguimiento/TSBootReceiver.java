/*
 * Boot persistance feature for scheduled alarm
 * 
 * Author: Arnau Orriols
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TSBootReceiver extends BroadcastReceiver {
    TSAlarmReceiver alarm = new TSAlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            alarm.setAlarm(context);
        }
    }   
}
