package com.arnauorriols.apps.terremotosseguimiento;

import com.arnauorriols.apps.terremotosseguimiento.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import java.util.HashMap;

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
    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        HashMap<String, String> eqData = (HashMap<String, String>) intent.getSerializableExtra(TSService.EQ_DATA);

        if (eqData != null){ 
            TextView time  = (TextView) findViewById(R.id.time);
            TextView date = (TextView) findViewById(R.id.date);
            TextView magnitude = (TextView) findViewById(R.id.magnitude);
            TextView location = (TextView) findViewById(R.id.location);

            time.setText(getString(R.string.time_label) +
                         "\t\t" + eqData.get("time"));
            date.setText(getString(R.string.date_label) +
                         "\t\t" + eqData.get("date"));
            magnitude.setText(getString(R.string.magnitude_label) +
                              "\t\t" + eqData.get("magnitude"));
            location.setText(getString(R.string.location_label) +
                             "\t\t" + eqData.get("location"));
        }else{
            TextView title = (TextView) findViewById(R.id.title);
            TextView help = (TextView) findViewById(R.id.time);
            title.setText(getString(R.string.usage_title));
            help.setText(getString(R.string.usage_explanation));
        }

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
