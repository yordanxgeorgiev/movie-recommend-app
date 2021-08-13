package com.example.grooveymoovey;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Activity_main extends AppCompatActivity{

    private JSONArray trendingResults = new JSONArray();
    private ArrayList<String> trendingImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("   Popular today:");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        setRecommendButton();
        setWatchlistButton();
        setOptionsButton();

        generateTrendingImages();
    }

    private void setOptionsButton()
    {
        ImageButton btnOptions = findViewById(R.id.btn_settings);
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Activity_optionsMenu.class);
                startActivity(intent);
            }
        });
    }

    private void setWatchlistButton()
    {
        Button btnWatchlist = findViewById(R.id.btn_watchlist);
        btnWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Activity_watchlist.class);
                startActivity(intent);
            }
        });
    }

    // this is the button used to recommend a movie
    private void setRecommendButton()
    {
        Button btnRecommend = findViewById(R.id.btn_recommend);
        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("options", MODE_PRIVATE);

                boolean checked_animation = sp.getBoolean("checked_animation", true);
                boolean checked_comedy = sp.getBoolean("checked_comedy", true);
                boolean checked_documentary = sp.getBoolean("checked_documentary", true);
                boolean checked_horror = sp.getBoolean("checked_horror", true);
                boolean checked_scifi = sp.getBoolean("checked_scifi", true);
                boolean checked_movies = sp.getBoolean("checked_movies", true);
                boolean checked_series = sp.getBoolean("checked_series", true);

                ArrayList<String> excludedGenres = new ArrayList<>();
                if(!checked_animation) { excludedGenres.add("Animation");}
                if(!checked_comedy) { excludedGenres.add("Comedy");}
                if(!checked_documentary) { excludedGenres.add("Documentary");}
                if(!checked_horror) { excludedGenres.add("Horror");}
                if(!checked_scifi) { excludedGenres.add("Science Fiction");}

                // next section of code contains some repeating code, but is left that way for clarity
                boolean flag = true;
                if(checked_movies && !checked_series)
                {
                    // repeating cycle until a movie is found
                    while(flag)
                    {
                        // first we get a page with results
                        JSONArray pageResults = getRandomResultsPage("movie");
                        // then get random result from that page and check if it meets the requirements
                        flag = !getRandomMovieOrTv(pageResults, excludedGenres);
                    }
                }
                else if(checked_series && !checked_movies)
                {
                    // repeating cycle until a movie is found
                    while(flag)
                    {
                        // first we get a page with results
                        JSONArray pageResults = getRandomResultsPage("tv");
                        // then get random result from that page and check if it meets the requirements
                        flag = !getRandomMovieOrTv(pageResults, excludedGenres);
                    }
                }
                else // checked_series && checked_movies (because they can't be both off)
                {
                    Random rnd = new Random();
                    boolean isMovie = rnd.nextBoolean();
                    String mediaType;

                    if(isMovie)
                    {
                        mediaType = "movie";
                    }
                    else
                    {
                        mediaType = "tv";
                    }
                    // repeating cycle until a movie is found
                    while(flag)
                    {
                        // here both functions can be merged into single one, but this is more clear
                        JSONArray pageResults = getRandomResultsPage(mediaType);
                        flag = !getRandomMovieOrTv(pageResults, excludedGenres);
                    }
                }
            }
        });
    }

    // if a movie/series meets the requirements (genres and not yet seen) starts "movieInfo" activity and returns true
    private boolean getRandomMovieOrTv(JSONArray pageResults, ArrayList<String> excludedGenres)
    {
        // here we operate both movies and series, although the variables are named with "movie"
        Random rnd = new Random();
        JSONObject movieInfo;
        int movieIndex = rnd.nextInt(20); // 20 results per page
        try {
            movieInfo = pageResults.getJSONObject(movieIndex);
            MovieParser mp = new MovieParser(movieInfo, getResources());
            String genres = mp.getMovieGenres();

            // checking if the movie is already seen
            SharedPreferences sp = getSharedPreferences(getString(R.string.alreadySeen_sharedPrefs), Context.MODE_PRIVATE);
            if(sp.contains(mp.getMovieTitle()))
            {
                return false;
            }
            // checking if the movie is of the excluded genres
            for(String s : excludedGenres)
            {
                if(genres.contains(s))
                {
                    return false;
                }
            }

            Intent intent = new Intent(getBaseContext(), Activity_movieInfo.class);
            intent.putExtra("movieInfo", movieInfo.toString());
            startActivity(intent);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    // gets a random results page of the given mediaType with min_rating and min_votes
    private JSONArray getRandomResultsPage(String mediaType)
    {
        Random rnd = new Random();
        // sending first request for movies/series to see how many pages with results there are
        JSONObject results;
        if(mediaType.equals("movie"))
        {
            results = API_handler.getMoviesByMinRating(getString(R.string.min_votes), getString(R.string.min_rating),"1");
        }
        else
        {
            results = API_handler.getSeriesByMinRating(getString(R.string.min_votes), getString(R.string.min_rating),"1");
        }

        // now getting the number of pages
        JSONArray resultsArray = new JSONArray();
        int totalPages = 0;
        try {
            totalPages = results.getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // getting a random page from all possible
        int page = totalPages;
        while(page == totalPages)
        {
            page = rnd.nextInt(totalPages)+1;
        }
        if(mediaType.equals("movie"))
        {
            results = API_handler.getMoviesByMinRating(getString(R.string.min_votes), getString(R.string.min_rating), Integer.toString(page));
        }
        else
        {
            results = API_handler.getSeriesByMinRating(getString(R.string.min_votes), getString(R.string.min_rating), Integer.toString(page));
        }
        try {
            resultsArray = results.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultsArray;
    }

    private void generateTrendingImages()
    {
        LinearLayout scrollLayout = findViewById(R.id.scrollView_layout);

        JSONObject trending = API_handler.getTrending();

        try {
            trendingResults = trending.getJSONArray("results");
        }
        catch (Exception ignored){}

        for(int i = 0; i < 20;i++)
        {
            try {
                JSONObject json = trendingResults.getJSONObject(i);
                MovieParser mp = new MovieParser(json, getResources());

                if(mp.getMovieType().equals("tv") || mp.getMovieType().equals("movie"))
                {
                    trendingImages.add(mp.getMovieImageURL());
                }
            }
            catch (Exception e){break;}
        }

        for(int i = 0; i < trendingImages.size(); i++)
        {
            ImageView image = new ImageView(this);
            Glide.with(this).
                    asBitmap().
                    load(trendingImages.get(i))
                    .into(image);
            String movieInfo = "";
            try {
                movieInfo = trendingResults.getJSONObject(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String finalMovieInfo = movieInfo;
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), Activity_movieInfo.class);
                    intent.putExtra("movieInfo", finalMovieInfo);
                    startActivity(intent);
                }
            });

            //converting dp to px
            float dip =440f;
            Resources r = getResources();
            int px = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    r.getDisplayMetrics()
            ));

            // Adding space between images in scrollview
            TextView blank = new TextView(this);
            blank.setText("\t");
            scrollLayout.addView(blank);

            // Adding the image in scrollview
            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, px));
            scrollLayout.addView(image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for a movie");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

            if(s == null || s.equals("")) return false;

            s = s.replace(" ", "+");

            JSONObject searchResults = API_handler.searchMovieByName(s);
            Intent searchActivityIntent = new Intent(getApplicationContext(), Activity_searchResults.class);
            searchActivityIntent.putExtra("searchResults", searchResults.toString());
            startActivity(searchActivityIntent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

}
