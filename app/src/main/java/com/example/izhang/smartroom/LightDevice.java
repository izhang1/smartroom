package com.example.izhang.smartroom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class LightDevice extends AppCompatActivity {
    Context context;
    private TextView currentColor;

    private String port = "http://172.16.0.6:5050";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_device);
        context = this;


        final Button pulseButton = (Button) findViewById(R.id.pulseButton);
        final Button breathButton = (Button) findViewById(R.id.breathButton);
        final Button cycleButton = (Button) findViewById(R.id.cycleButton);


        pulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseBulb();
            }
        });

        breathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breathBulb();
            }
        });

        cycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleBulb();
            }
        });

        currentColor = (TextView) findViewById(R.id.currentColorText);

        SeekBar seekBar = (SeekBar) this.findViewById(R.id.lightBrightnessSeek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setBrightness(seekBar.getProgress());
                Toast.makeText(getApplication(), "Progress: " + seekBar.getProgress(), Toast.LENGTH_LONG).show();
            }
        });

        Button changeColor = (Button) findViewById(R.id.changeColorButton);
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("Choose color")
                        .initialColor(0xffffffff)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor.setBackgroundColor(selectedColor);
                                String colorString = Integer.toHexString(selectedColor);
                                int color = (int)Long.parseLong(colorString, 16);
                                int r = (selectedColor >> 16) & 0xFF;
                                int g = (color >> 8) & 0xFF;
                                int b = (color >> 0) & 0xFF;

                                System.out.println("RGB: " + r + ", " + g + ". " + b);
                                setRGB(r, g, b);
                                setBrightness(10);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

    }

    /**
     *  Call the volumee REST api
     *
     *   Server: /next_song
     *    POST:  no input necessary
     *
     **/
    private void setRGB(int r, int g, int b){

        String url = port + "/set_rgb";

        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"color\":\"rgb:"+  r + "," + g + "," + b +"\"}");
        }catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

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
     *  Call the set brightness API
     *
     *   Server: /set_degree_of_luminescence
     *    POST:  no input necessary
     *
     **/
    private void setBrightness(int brightnessVal){

        double value = (double) brightnessVal/ 100.00;
        if(value == 0) value = .01;

        String url = port + "/set_degree_of_luminescence";

        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"brightness\":\""+ value + "\"}");
        }catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

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
     *  Call the set_pulse REST api
     *
     *   Server: /set_pulse
     *    POST:  no input necessary
     *
     **/
    private void pulseBulb(){
        String url = port + "/set_pulse";
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
     *  Call the set_breathe REST api
     *
     *   Server: /set_breathe
     *    POST:  no input necessary
     *
     **/
    private void breathBulb(){
        String url = port + "/set_breathe";

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
     *  Call the set_cycle REST api
     *
     *   Server: /set_cycle
     *    POST:  no input necessary
     *
     **/
    private void cycleBulb(){
        String url = port + "/set_cycle";

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




}
