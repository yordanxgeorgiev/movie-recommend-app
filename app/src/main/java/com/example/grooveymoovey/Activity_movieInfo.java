package com.example.grooveymoovey;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Activity_movieInfo extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_info);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.movie_name);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        JSONObject movieInfo;
        try
        {
            // next section of code gets the passed movieInfo and reads it using the class MovieParser

            movieInfo = new JSONObject(getIntent().getStringExtra("movieInfo"));
            MovieParser mp = new MovieParser(movieInfo, getResources());
            ImageView movieImage = findViewById(R.id.movie_image);

            Glide.with(this).
                    asBitmap().
                    load(mp.getMovieImageURL())
                    .into(movieImage);

            TextView movieTitle = findViewById(R.id.movie_title);
            movieTitle.setText(mp.getMovieTitle());

            TextView movieDescription = findViewById(R.id.movie_description);
            movieDescription.setText(mp.getMovieOverview());

            setCheckBoxWatchlist(movieInfo);
            setCheckBoxAlreadySeen(mp.getMovieTitle());
        }
        catch (Exception ignored){}
    }

    private void setCheckBoxWatchlist(final JSONObject movieInfo) throws JSONException {

        final CheckBox cb_addWatchlist = findViewById(R.id.cb_addToWatchlist);
        JSONArray watchlist = readJSONArray(getString(R.string.watchlist_sharedPrefs), getString(R.string.watchlist_key));

        // checks if the viewed movie is already in the watchlist
        boolean check = jsonObjectSearch(movieInfo, watchlist);
        if(!check) cb_addWatchlist.setChecked(false);
        else cb_addWatchlist.setChecked(true);

        // saving/removing movie in/from the watchlist
        cb_addWatchlist.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.watchlist_sharedPrefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                JSONArray jsonArray = readJSONArray(getString(R.string.watchlist_sharedPrefs), getString(R.string.watchlist_key));

                if(cb_addWatchlist.isChecked())
                {
                    jsonArray.put(movieInfo);
                    editor.putString(getString(R.string.watchlist_key), jsonArray.toString());
                    editor.apply();
                }
                else
                {
                    try {
                        jsonArray = removeJSONObject(jsonArray, movieInfo);
                        editor.putString(getString(R.string.watchlist_key), jsonArray.toString());
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // function for reading JSONArray from shared preferences
    private JSONArray readJSONArray(String sharedPrefs, String key)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        String jsonStringArray = sharedPreferences.getString(key, "");
        JSONArray jsonArray = new JSONArray();
        try {
            if(!jsonStringArray.equals(""))
            {
                jsonArray = new JSONArray(jsonStringArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    // searches if a JSONObject is contained in a JSONArray
    private boolean jsonObjectSearch(JSONObject jsonObject, JSONArray jsonArray) throws JSONException {

        boolean check = false;

        for(int i = 0; i < jsonArray.length(); i++)
        {
            MovieParser mp1 = new MovieParser(jsonArray.getJSONObject(i), getResources());
            MovieParser mp2 = new MovieParser(jsonObject, getResources());

            if(mp1.equals(mp2))
            {
                check = true;
                break;
            }
        }
        return check;
    }

    // function to remove JSONObject from jsonArray
    private JSONArray removeJSONObject(JSONArray jsonArray, JSONObject jsonObject) throws JSONException {

        int pos = -1;
        for (int i = 0; i < jsonArray.length(); i++){
            MovieParser mp1 = new MovieParser(jsonArray.getJSONObject(i), getResources());
            MovieParser mp2 = new MovieParser(jsonObject, getResources());
            if(mp1.equals(mp2))
            {
                pos = i;
                break;
            }
        }
        if(pos == -1)
        {
            return jsonArray;
        }
        else
        {
            JSONArray list = new JSONArray();
            int len = jsonArray.length();
            for (int i=0;i<len;i++)
            {
                //Excluding the item at position
                if (i != pos)
                {
                    list.put(jsonArray.get(i));
                }
            }
            return list;
        }
    }

    // next function is for the alreadySeen checkBox, the logic is the same as for the watchlist
    private void setCheckBoxAlreadySeen(final String movieTitle)
    {
        final CheckBox cb_alreadySeen = findViewById(R.id.cb_alreadySeen);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alreadySeen_sharedPrefs), Context.MODE_PRIVATE);
        String alreadySeen = sharedPreferences.getString(getString(R.string.alreadySeen_movieTitle), null);
        if(alreadySeen == null) cb_alreadySeen.setChecked(false);
        else cb_alreadySeen.setChecked(true);

        cb_alreadySeen.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alreadySeen_sharedPrefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(cb_alreadySeen.isChecked())
                {
                    editor.putString(movieTitle, movieTitle);
                    editor.apply();
                }
                else
                {
                    editor.remove(movieTitle);
                    editor.apply();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
