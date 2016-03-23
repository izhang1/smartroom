package com.example.izhang.smartroom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        final Spinner musicList = (Spinner) this.findViewById(R.id.musicList);
        final Button playButton = (Button) this.findViewById(R.id.playSongButton);
        Button prevButton = (Button) this.findViewById(R.id.prevSongButton);
        Button nextButton = (Button) this.findViewById(R.id.nextSongButton);
        final Button pauseButton = (Button) this.findViewById(R.id.pauseSongButton);

        ArrayList<String> songList = populateMusicList(musicList); // Populate list with songs from server

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSong();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }
        });

        String url = "http://httpbin.org/html";

        /**
         *
         **/
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    /**
     *  Get request to retreive the current set of song list in database
     *
     *    Server: /song_library
     *     GET: no input necessary
     *     Returns list of songs stored locally on the pi in format:
     *       {“songs”:[‘song_1.mp3’, ‘song_2.mp3’, …]}
     *
     * @param spinner
     * @return ArrayList of Songs
     */
    public ArrayList<String> populateMusicList(Spinner spinner){
        return new ArrayList<>();
    }

    /**
     *  Call the play_song REST api
     *
     *   Server: /play_song
     *    POST:  no input necessary
     *
     **/
    private void playSong(){

        String url = "http://httpbin.org/html";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    /**
     *  Call the pause_song REST api
     *
     *   Server: /pause_song
     *    POST:  no input necessary
     *
     **/
    private void pauseSong(){

        String url = "http://httpbin.org/html";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }


    /**
     *  Call the prev_song REST api
     *
     *   Server: /prev_song
     *    POST:  no input necessary
     *
     **/
    private void prevSong(){

        String url = "http://httpbin.org/html";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }


    /**
     *  Call the next_song REST api
     *
     *   Server: /next_song
     *    POST:  no input necessary
     *
     **/
    private void nextSong(){

        String url = "http://httpbin.org/html";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }
}
