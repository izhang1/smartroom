package com.example.izhang.smartroom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MusicPlayer extends AppCompatActivity {

    Firebase firebaseRef;
    ArrayList<String> songList;
    Spinner musicList;

    Button pauseButton;
    Button playButton;

    private String port = "http://172.16.0.4:5050";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Firebase.setAndroidContext(this);

        firebaseRef = new Firebase("https://smartroom490.firebaseio.com/library/songs");

        setTitle("Music Player");

        musicList = (Spinner) this.findViewById(R.id.musicList);
        playButton = (Button) this.findViewById(R.id.playSongButton);
        Button prevButton = (Button) this.findViewById(R.id.prevSongButton);
        Button nextButton = (Button) this.findViewById(R.id.nextSongButton);
        pauseButton = (Button) this.findViewById(R.id.pauseSongButton);
        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar1);

        songList = new ArrayList<>(); // Populate list with songs from server


        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    populateMusicList();
                    DataSnapshot songObj = (DataSnapshot)iter.next();
                    songList.add(songObj.getValue().toString());
                }

                ArrayAdapter<String> songAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, songList);
                musicList.setAdapter(songAdapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        musicList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startSong(songList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setVolume(seekBar.getProgress());
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplication(), "Progress: " + seekBar.getProgress(), Toast.LENGTH_LONG).show();
            }
        });

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
     * @return ArrayList of Songs
     */
    public ArrayList<String> populateMusicList(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = port + "/song_library";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


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
        String url = port + "/play_song";
        JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(jsObjRequest);

    }

    /**
     *  Call the play_song REST api
     *
     *  /start_song
     *  POST: input JSON: {“song”:”<song_name>”}
     *  <song_name> = name of song as string
     *
     **/
    private void startSong(String songName){

        System.out.println("Start Song");

        String url = port + "/start_song";
        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"song\":\""+ songName +"\"}");
        }catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(jsObjRequest);

    }


    /**
     *  Call the pause_song REST api
     *
     *   Server: /pause_song
     *    POST:  no input necessary
     *
     **/
    private void pauseSong(){

        String url = port + "/pause_song";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
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
        System.out.println("Prev Song");
        int selectedItem = musicList.getSelectedItemPosition();
        if(selectedItem > 0) {
            musicList.setSelection(selectedItem - 1);
        }

        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
    }


    /**
     *  Call the next_song REST api
     *
     *   Server: /next_song
     *    POST:  no input necessary
     *
     **/
    private void nextSong(){

        System.out.println("Next Song");
        int selectedItem = musicList.getSelectedItemPosition();
        if(selectedItem + 1 <= songList.size()) {
            musicList.setSelection(selectedItem + 1);
        }

        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);

    }

    /**
     *  Call the volumee REST api
     *
     *   Server: /next_song
     *    POST:  no input necessary
     *
     **/
    private void setVolume(int volume){

        String url = port + "/set_volume";
        double v2 = volume / 100.0;
        String volConverted = Double.toString(v2);
        System.out.println("Converted: " + volConverted);

        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"volume\":\" "+ volConverted  +"\"}");
        }catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(jsObjRequest);

    }
}
