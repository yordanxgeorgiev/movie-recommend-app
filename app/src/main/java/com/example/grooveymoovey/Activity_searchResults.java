package com.example.grooveymoovey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_searchResults extends AppCompatActivity {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private JSONArray resultsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String searchResults = getIntent().getStringExtra("searchResults");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Results:");

        try {
            resultsArray = new JSONObject(searchResults).getJSONArray("results");
        }
        catch (Exception ignored){}

        for(int i = 0; i < 20;i++)
        {
            try {
                JSONObject json = resultsArray.getJSONObject(i);
                MovieParser mp = new MovieParser(json, getResources());

                if(mp.getMovieType().equals("tv") || mp.getMovieType().equals("movie"))
                {
                    String title = mp.getMovieTitle();
                    String year = mp.getMovieReleaseDate();
                    String movieDescription = title+ " " + year;
                    mNames.add(movieDescription);
                    mImages.add(mp.getMovieImageURL());
                }
            }
            catch (Exception e){break;}
        }
        initRecyclerView();
    }

    private void initRecyclerView()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames,mImages, resultsArray, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
