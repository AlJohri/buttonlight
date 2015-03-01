package com.buttonlight.buttonlight;

import android.app.Activity;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CheckButtonLight extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

     /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_button_light);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;


        switch (position){
            case 0:
                fragment = new NavigationDrawerFragment();
                break;
            case 1:
                fragment = new NavigationDrawerFragment();
                break;
            case 2:
                fragment = new NavigationDrawerFragment();
                break;

        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        String args = Integer.toString(position + 1) + "\n";
        try {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpClient client = new DefaultHttpClient();
            String request = "http://buttonlight.herokuapp.com/status/?device_id=";
            switch(position)
            {
                case 0:
                    request+= "53ff6b066678505521351367";
                    break;
                case 1:
                    request +="1337";
                    break;
                case 2:
                    request += "420";
                    break;

            }
            request += "&last=1";
            HttpGet get = new HttpGet(request);
            HttpResponse response = client.execute(get);
            String resp = EntityUtils.toString(response.getEntity());
            JSONArray jsonObj = new JSONArray(resp);
            Long longTime =  (long)((JSONObject)jsonObj.get(0)).get("time");
            //long time = Long.parseLong();
            Time t = new Time();
            t.set(longTime);

            String state = ((int)((JSONObject)jsonObj.get(0)).get("status"))==1?"On":"Off";
            String time = Integer.toString(t.month+1) + "/" + Integer.toString(t.monthDay) + "/" + Integer.toString(t.year) + ": " + Integer.toString(t.hour) + ":" + Integer.toString(t.minute) + ":" + Integer.toString(t.second);
            args+=state+"\n"+time;
        }
        catch(Exception e)
        {
            args += e.toString();

        }


        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(args))
                .commit();

    }

    public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }


    }

    public void onStart(int number, int val) {
        TextView t = (TextView) findViewById(R.id.textView);
        switch (number) {

            case 1:
                t.setText("Feed Dog");
                break;
            case 2:
                t.setText("Wash Dishes");
                break;
            case 3:
                t.setText("Take out trash");
                break;
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.check_button_light, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_VALUE = "val";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String arg) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, arg);

            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
            View rootView;
            Bundle args = getArguments();
            String[] arg = getArguments().getString(ARG_SECTION_NUMBER).split("\n");
            int currentView = Integer.parseInt(arg[0]);
            rootView = inflater.inflate(R.layout.fragment_check_button_light, container, false);
            TextView Namn = (TextView) rootView.findViewById(R.id.textView);
            TextView val = (TextView) rootView.findViewById(R.id.textView6);
            TextView time = (TextView) rootView.findViewById(R.id.textView8);
            if(currentView == 1){

                Namn.setText("Feed Dog");
            }else if(currentView == 2){
                Namn.setText("Wash Dishes");
            }else if(currentView == 3){
                Namn.setText("Take out Trash");
            }else {
            }
            val.setText(arg[1]);
            time.setText(arg[2]);



            return rootView;
        }

        /**
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_check_button_light, container, false);

            return rootView;

        }
**/
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((CheckButtonLight) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }



}
