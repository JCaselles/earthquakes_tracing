package com.arnauorriols.apps.terremotosseguimiento;

import com.arnauorriols.apps.terremotosseguimiento.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;

public class TerremotosSeguimiento extends ActionBarActivity
{
    private static final int MAX_ITEMS = 2;
    private TSFragmentPageAdapter tsfpa;
    private ViewPager vp;
    private TSAlarmReceiver alarm = new TSAlarmReceiver();


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tsfpa = new TSFragmentPageAdapter(this, getSupportFragmentManager());
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setAdapter(tsfpa);
        ActionBar ab = getSupportActionBar();

        ActionBar.TabListener tl = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft){
                vp.setCurrentItem(tab.getPosition());
            }
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft){
            }
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft){
            }
        };

        vp.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });
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

    public static class TSFragmentPageAdapter extends FragmentPagerAdapter {
        private Context context;
        public TSFragmentPageAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return MAX_ITEMS;
        }

        @Override
        public Fragment getItem(int position){
            Fragment f = null;
            switch (position) {
                case 0:
                    f = Fragment.instantiate(context, TSDetailsFragment.class.getName());
                    break;
                case 1:
                    f = Fragment.instantiate(context, TSDetailsFragment.class.getName());
                    break;
            }
            return f;
        }
    }
}
