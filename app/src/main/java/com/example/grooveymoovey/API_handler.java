package com.example.grooveymoovey;

import org.json.JSONObject;
import java.net.URL;

/* Class for making the API requests */

class API_handler {

    private static final String API_KEY = "b5d1124be4cbf665b10154bd9a4a96f5";

    static JSONObject searchMovieByName(String title)
    {
        String targetURLString = "https://api.themoviedb.org/3/search/multi?api_key=" + API_KEY + "&query=" + title;

        return urlExecutor(targetURLString);
    }

    static JSONObject getGenres()
    {
        String targetURLString = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY;

        return urlExecutor(targetURLString);
    }

    static JSONObject getTrending()
    {
        String targetURL = "https://api.themoviedb.org/3/trending/all/day?api_key=" + API_KEY;

        return urlExecutor(targetURL);
    }

    static JSONObject getMoviesByMinRating(String minVotes, String minRating, String page)
    {
           String targetURL = "https://api.themoviedb.org/3/discover/movie?api_key=b5d1124be4cbf665b10154bd9a4a96f5" +
            "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page="+ page +
            "&vote_count.gte=" + minVotes +
            "&vote_average.gte=" + minRating;

        return urlExecutor(targetURL);
    }

    static JSONObject getSeriesByMinRating(String minVotes, String minRating, String page)
    {
        String targetURL = "https://api.themoviedb.org/3/discover/tv?api_key=b5d1124be4cbf665b10154bd9a4a96f5" +
                "&language=en-US&sort_by=popularity.desc" +
                "&page=" + page +
                "&vote_average.gte=" + minRating +
                "&vote_count.gte=" + minVotes +
                "&include_null_first_air_dates=false";

        return urlExecutor(targetURL);
    }

    private static JSONObject urlExecutor(String url)
    {
        JSONObject result = new JSONObject();

        try
        {
            RequestHandler request = new RequestHandler(new URL(url));
            Thread networkingThread = new Thread(request);
            networkingThread.start();

            networkingThread.join();

            result = request.getResult();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  result;
    }
}
