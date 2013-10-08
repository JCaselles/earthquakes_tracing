package com.arnauorriols.apps.terremotosseguimiento;

import com.arnauorriols.apps.terremotosseguimiento.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.TextView;
import android.view.MenuItem;
import android.content.Intent;
import android.app.PendingIntent;

import java.util.HashMap;

public class TerremotosSeguimiento extends ActionBarActivity
{
    private TSAlarmReceiver alarm = new TSAlarmReceiver();
    private boolean activated = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        activated = (PendingIntent.getBroadcast(this, 0, new Intent(this, TSAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        Log.d(RequestHelper.DEBUG_TAG, "activated is" + String.valueOf(activated));
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        HashMap<String, String> eqData = (HashMap<String, String>) intent.getSerializableExtra(TSService.EQ_DATA);

        if (eqData != null){ 
            TextView title = (TextView) findViewById(R.id.title);
            TextView time  = (TextView) findViewById(R.id.time);
            TextView date = (TextView) findViewById(R.id.date);
            TextView magnitude = (TextView) findViewById(R.id.magnitude);
            TextView location = (TextView) findViewById(R.id.location);

            title.setText(getString(R.string.display_title));
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
        MenuItem mi = menu.findItem(R.id.activation_switch);
        if (activated){
            mi.setTitle(getString(R.string.cancel_text));
        }else{
            mi.setTitle(getString(R.string.start_text));
        }
        return true;
    }

    // Menu options to set and cancel the alarm.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks START ALARM, set the alarm.
            case R.id.activation_switch:
                if(!activated){
                    alarm.setAlarm(this);
                    item.setTitle(getString(R.string.cancel_text));
                    Log.v(RequestHelper.DEBUG_TAG, "Alarm activated");
                    activated = true;
                }else{
                    alarm.cancelAlarm(this);
                    item.setTitle(getString(R.string.start_text));
                    Log.v(RequestHelper.DEBUG_TAG, "Alarm deactivated");
                    activated = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
