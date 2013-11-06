/*
 * TerremotosSeguimiento.java
 *
 * Main Activity of the app. Extends ActionBarActivity from
 * support.v7.appcompat library. Features an ActionBar activity with swipe
 * enabled tabs.
 *
 * Author: Arnau Orriols
 *
 * Copyright 2013 Arnau Orriols.
 */
package com.arnauorriols.apps.terremotosseguimiento;

//import com.arnauorriols.apps.terremotosseguimiento.R;   // Required?
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.Menu;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.HashMap;
import java.util.ArrayList;



public class TerremotosSeguimiento extends ActionBarActivity
                    implements TSListFragment.OnRowSelectedListener,
                               TSDetailsFragment.OnFragmentReadyListener {

    private static final int MAX_ITEMS = 2;
    private TSFragmentPageAdapter tsfpa;
    private ViewPager vp;
    private TSAlarmReceiver alarm = new TSAlarmReceiver();
    private boolean activated = false;
    private TSFileLoader tsfl;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        activated = (PendingIntent.getBroadcast(
                this, 0, new Intent(this, TSAlarmReceiver.class),
                        PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(RequestHelper.DEBUG_TAG, "activated is" +
                                                    String.valueOf(activated));

        /* TSFragmentPageAdapter is an inner class of this. it defines the
           instantation of each fragment for each tab. */
        tsfpa = new TSFragmentPageAdapter(this, getSupportFragmentManager());
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setAdapter(tsfpa);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /* TabListener calls the ViewPager to change the current position based
           on the current selected tab. This links the selection of a tab to
           the ViewPager.*/
        ActionBar.TabListener tl = new ActionBar.TabListener() {

            public void onTabSelected(ActionBar.Tab tab,
                                      FragmentTransaction ft){
                vp.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab,
                                        FragmentTransaction ft){}

            public void onTabReselected(ActionBar.Tab tab,
                                        FragmentTransaction ft){}
        };

        /* Changes de selected tab on the Action Bar based on the selected page
           of the ViewPager. This links the ViewPager swipe with the Action Bar
           tabs. */
        vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });

        /* Adds the two tabs to the Action Bar. Each tab gets a label and the
           tab listener, which links the tab to the ViewPager. */
        ab.addTab(ab.newTab()
            .setText(getString(R.string.list_tab_label))
            .setTabListener(tl)
        );
        ab.addTab(ab.newTab()
            .setText(getString(R.string.details_tab_label))
            .setTabListener(tl)
        );

        /* If no EQDATA intent, loads eqList from file or url. If EQDATA intent
           is found, updates eqList from the intent.
           TODO: Should be improved, sometimes gets stuck, sometimes shows 
           wrong data. */
        if(new RequestHelper(this).checkNetwork()){
            if (!getIntent().hasExtra(TSService.EQ_DATA)){
                tsfl = new TSFileLoader(this);
                tsfl.execute();
            }else{
                importEQDATAIntent(getIntent());
            }
        }
    }


    /**
     * Implementation of TSDetailsFragment callback. Used to wait for the
     * fragment's layout to be fully loaded before switching to it and
     * modifying its views.
     */
    @Override
    public void onFragmentReady (){
        vp.setCurrentItem(1);
    }


    /**
     * Cancels TSFileLoader asynctask if not finished when the activity is
     * destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tsfl != null) {
            tsfl.cancel(true);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }


    /**
     * Updates eqList extracting it from EQ_DATA intent. Changes to details
     * tab automatically, calling setCurrentItem method of the ViewPager.
     */
    public void importEQDATAIntent(Intent intent){
        ArrayList<HashMap<String, String>> eqData =
            (ArrayList<HashMap<String, String>>)
                            intent.getSerializableExtra(TSService.EQ_DATA);
        TSListFragment.updateEqList(eqData);
        resetViewPager();

        // PENDING REVISION, TO BE REMOVED
        // TOFIX: Causes crash when app been created with EQDATA
        // intent -> fragment returns nullPointerException
        //vp.setCurrentItem(1);
    }


    /** PageAdapter is destroyed and rebuild to update the fragments data. */
    /* TODO: Consider notifyDataSetChanged, it should work. */
    public void resetViewPager(){
        //vp.getAdapter().notifyDataSetChanged();
        vp.setAdapter(null);
        vp.setAdapter(new TSFragmentPageAdapter(this, getSupportFragmentManager()));
        Log.v(RequestHelper.DEBUG_TAG, "List should be updated");
    }


    /**
     * This is called when a new intent is delivered with an instance of this
     * activity already present. This is due to the singleTop attribute of the
     * activity.
     */
    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        importEQDATAIntent(intent);
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


    /* Menu options to set and cancel the alarm. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                    f = Fragment.instantiate(context,
                                             TSListFragment.class.getName());
                    break;
                case 1:
                    f = Fragment.instantiate(context,
                                        TSDetailsFragment.class.getName());
                    break;
            }
            return f;
        }
    }


    @Override
    public void onRowSelected(HashMap<String, String> eqData){
        ArrayList<HashMap<String, String>> eqList =
                                new ArrayList<HashMap<String, String>>();
        eqList.add(eqData);

        Intent intent = new Intent()
                        .putExtra(TSListFragment.INTENT_DETAILS, eqList);
        setIntent(intent);
        vp.setCurrentItem(1);
    }
}
