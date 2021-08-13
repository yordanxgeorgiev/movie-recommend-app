package com.example.grooveymoovey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Activity_watchlist extends AppCompatActivity {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private JSONArray watchlistJSON = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Watchlist:");

        populateRecyclerView();
    }

    private void populateRecyclerView()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.watchlist_sharedPrefs), MODE_PRIVATE);
        String watchlist = sharedPreferences.getString(getString(R.string.watchlist_key), "");

        try {
            watchlistJSON = new JSONArray(watchlist);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < watchlistJSON.length();i++)
        {
            try {
                JSONObject json = watchlistJSON.getJSONObject(i);
                MovieParser mp = new MovieParser(json, getResources());

                String title = mp.getMovieTitle();
                String year = mp.getMovieReleaseDate();
                String movieDescription = title+ " " + year;
                mNames.add(movieDescription);
                mImages.add(mp.getMovieImageURL());
            }
            catch (Exception e){break;}
        }
        initRecyclerView();
    }


    private void initRecyclerView()
    {
        RecyclerView recyclerView = findViewById(R.id.watchlist_recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames,mImages, watchlistJSON, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
