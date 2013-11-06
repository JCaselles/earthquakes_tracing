/*
 * TSBootReceiver.java
 *
 * Boot persistance feature for the scheduled alarm. Extends Broadcast
 * Receiver.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives BOOT_COMPLETED broadcast and calls setAalarm from TSAlarmReceiver,
 * to set the alarm on every device boot. By default is disabled in the
 * manifest, requires to be enabled dynamically.
 */
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
