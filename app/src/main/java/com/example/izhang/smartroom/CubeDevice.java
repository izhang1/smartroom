package com.example.izhang.smartroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class CubeDevice extends AppCompatActivity {

    private String port = "http://172.16.0.6:5050";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cube_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);

        getData();

        final TextView tempValue = (TextView) findViewById(R.id.tempValue);
        final TextView humValue = (TextView) findViewById(R.id.humValue);
        final TextView lightValue = (TextView) findViewById(R.id.lightValue);
        final TextView pressValue = (TextView) findViewById(R.id.pressureValue);
        final TextView vocValue = (TextView) findViewById(R.id.vocValue);
        final TextView noiseVal = (TextView) findViewById(R.id.noiseVal);
        final TextView battValue = (TextView) findViewById(R.id.battValue);


        Firebase firebaseRef = new Firebase("https://smartroom490.firebaseio.com/");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.child("cubesensor").getValue().toString();

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String temp = jsonObject.get("temp").toString();
                    double numTemp = Double.parseDouble(temp);
                    numTemp = numTemp / 100.00;
                    tempValue.setText(numTemp + " Celcuis");
                    humValue.setText(jsonObject.get("humidity").toString() + "%");
                    lightValue.setText(jsonObject.get("light").toString() + " Lux");
                    pressValue.setText(jsonObject.get("pressure").toString() + " mBar");
                    vocValue.setText(jsonObject.get("voc").toString() + " PPM");
                    noiseVal.setText(jsonObject.get("dba").toString() + " dB");
                    battValue.setText(jsonObject.get("battery").toString() + "%");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    /**
     *  Call the play_song REST api
     *
     *  /start_song
     *  POST: input JSON: {“song”:”<song_name>”}
     *  <song_name> = name of song as string
     *
     **/
    private void getData(){

        String url = port + "/cube_sensor";
        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"sensor\":\"clyde\"}");
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
