package com.example.grooveymoovey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class Activity_optionsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres_rating);

        Button btnSave = findViewById(R.id.btnSaveGenrePrefs);

        // Sets the correct values for the views (switches)
        updateViews();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Switch sw_animation = findViewById(R.id.sw_animation);
                Switch sw_comedy = findViewById(R.id.sw_comedy);
                Switch sw_documentary = findViewById(R.id.sw_documentary);
                Switch sw_horror = findViewById(R.id.sw_horror);
                Switch sw_scifi = findViewById(R.id.sw_scifi);
                Switch sw_movies = findViewById(R.id.sw_movies);
                Switch sw_series = findViewById(R.id.sw_series);

                boolean checked_animation = sw_animation.isChecked();
                boolean checked_comedy = sw_comedy.isChecked();
                boolean checked_documentary = sw_documentary.isChecked();
                boolean checked_horror = sw_horror.isChecked();
                boolean checked_scifi = sw_scifi.isChecked();
                boolean checked_movies = sw_movies.isChecked();
                boolean checked_series = sw_series.isChecked();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.options_sharedPrefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("checked_animation", checked_animation);
                editor.putBoolean("checked_comedy", checked_comedy);
                editor.putBoolean("checked_documentary", checked_documentary);
                editor.putBoolean("checked_horror", checked_horror);
                editor.putBoolean("checked_scifi", checked_scifi);

                if(!checked_movies && !checked_series)
                {
                    checked_movies = true;
                    checked_series = true;
                    sw_movies.setChecked(true);
                    sw_series.setChecked(true);
                    Toast.makeText(Activity_optionsMenu.this, "Movie and Series can't be both off.",
                            Toast.LENGTH_LONG).show();
                    editor.putBoolean("checked_movies", checked_movies);
                    editor.putBoolean("checked_series", checked_series);
                    editor.apply();
                    return;
                }

                editor.putBoolean("checked_movies", checked_movies);
                editor.putBoolean("checked_series", checked_series);
                editor.apply();

                Intent intent = new Intent(view.getContext(), Activity_main.class);
                startActivity(intent);
            }
        });
    }

    private void updateViews()
    {
        Switch sw_animation = findViewById(R.id.sw_animation);
        Switch sw_comedy = findViewById(R.id.sw_comedy);
        Switch sw_documentary = findViewById(R.id.sw_documentary);
        Switch sw_horror = findViewById(R.id.sw_horror);
        Switch sw_scifi = findViewById(R.id.sw_scifi);
        Switch sw_movies = findViewById(R.id.sw_movies);
        Switch sw_series = findViewById(R.id.sw_series);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.options_sharedPrefs), Context.MODE_PRIVATE);

        boolean checked_animation = sharedPreferences.getBoolean("checked_animation", true);
        boolean checked_comedy = sharedPreferences.getBoolean("checked_comedy", true);
        boolean checked_documentary = sharedPreferences.getBoolean("checked_documentary", true);
        boolean checked_horror = sharedPreferences.getBoolean("checked_horror", true);
        boolean checked_scifi = sharedPreferences.getBoolean("checked_scifi", true);
        boolean checked_movies = sharedPreferences.getBoolean("checked_movies", true);
        boolean checked_series = sharedPreferences.getBoolean("checked_series", true);

        sw_animation.setChecked(checked_animation);
        sw_comedy.setChecked(checked_comedy);
        sw_documentary.setChecked(checked_documentary);
        sw_horror.setChecked(checked_horror);
        sw_scifi.setChecked(checked_scifi);
        sw_movies.setChecked(checked_movies);
        sw_series.setChecked(checked_series);
    }
}
