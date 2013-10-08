package com.arnauorriols.apps.terremotosseguimiento;

import com.arnauorriols.apps.terremotosseguimiento.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;

public class TerremotosSeguimiento extends ActionBarActivity
{
    TSAlarmReceiver alarm = new TSAlarmReceiver();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu options to set and cancel the alarm.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks START ALARM, set the alarm.
            case R.id.start_action:
                alarm.setAlarm(this);
                Log.v(RequestHelper.DEBUG_TAG, "Alarm activated");
                return true;
            // When the user clicks CANCEL ALARM, cancel the alarm. 
            case R.id.cancel_action:
                alarm.cancelAlarm(this);
                Log.v(RequestHelper.DEBUG_TAG, "Alarm deactivated");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
