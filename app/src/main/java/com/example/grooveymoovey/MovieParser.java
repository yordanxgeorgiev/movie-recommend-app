package com.example.grooveymoovey;

import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// This class is used to read info about movie in JSON format and make it accessible

class MovieParser {

    private String movieType; // tv or movie
    private String movieTitle;
    private String movieOverview;
    private String movieImageURL;
    private String movieRating;
    private String movieReleaseDate;
    private String movieGenres;

    MovieParser(JSONObject jsonObject, Resources r) throws JSONException {

        if(jsonObject.has("name"))
        {
            movieType = "tv";
            movieTitle = jsonObject.getString("name");
            movieReleaseDate = jsonObject.getString("first_air_date");
        }
        else
        {
            movieType = "movie";
            movieTitle = jsonObject.getString("title");
            movieReleaseDate = jsonObject.getString("release_date");
        }
        movieOverview = jsonObject.getString("overview");
        movieImageURL = r.getString(R.string.tmdb_image_prefix) + jsonObject.getString("poster_path");
        movieRating = jsonObject.getString("vote_average");
        populateGenres(jsonObject);

    }

    boolean equals(MovieParser o)
    {
        return this.getMovieTitle().equals(o.getMovieTitle()) && this.getMovieReleaseDate().equals(o.getMovieReleaseDate());
    }

    private void populateGenres(JSONObject jsonObject)
    {
        try
        {
            String genresIDs = jsonObject.getString("genre_ids");
            genresIDs = genresIDs.substring(1);
            genresIDs = genresIDs.substring(0, genresIDs.length()-1);

            String[] movie_genres = genresIDs.split(",");

            JSONArray allGenres = API_handler.getGenres().getJSONArray("genres");

            String[] allGenresIDs = new String[allGenres.length()];
            String[] allGenresNames = new String[allGenres.length()];

            for(int i = 0; i < allGenres.length(); i++)
            {
                allGenresIDs[i] = allGenres.getJSONObject(i).getString("id");
                allGenresNames[i] = allGenres.getJSONObject(i).getString("name");
            }

            movieGenres = "";

            for (String movie_genre : movie_genres) {
                for (int j = 0; j < allGenresIDs.length; j++) {
                    if (movie_genre.equals(allGenresIDs[j])) {
                        if (!movieGenres.equals(""))
                            movieGenres = movieGenres + ", " + allGenresNames[j];
                        else movieGenres = allGenresNames[j];
                        break;
                    }
                }
            }
        }
        catch (Exception ignored){}
    }

    String getMovieGenres() {
        return movieGenres;
    }

    String getMovieImageURL() {
        return movieImageURL;
    }

    String getMovieOverview() {
        return movieOverview;
    }

    String getMovieRating() {
        return movieRating;
    }

    String getMovieTitle() {
        return movieTitle;
    }

    String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    String getMovieType() {
        return movieType;
    }
}
