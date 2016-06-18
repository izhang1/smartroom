package com.example.izhang.smartroom;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MotionListAdapter extends BaseAdapter{
    Context context;
    private static LayoutInflater inflater=null;
    private ArrayList<Schedule> scheduleList;
    private String port = "http://172.16.0.6:5050";

    public MotionListAdapter(Context context, ArrayList<Schedule> scheduleList) {
        // TODO Auto-generated constructor stub
        this.scheduleList = scheduleList;
        this.context = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final View rowView;
        rowView = inflater.inflate(R.layout.list_schedule, null);

        final TextView scheName = (TextView) rowView.findViewById(R.id.scheName);
        TextView activity = (TextView) rowView.findViewById(R.id.activity);
        TextView device = (TextView) rowView.findViewById(R.id.device);
        TextView time = (TextView) rowView.findViewById(R.id.time);

        Schedule scheduleItem = scheduleList.get(position);

        scheName.setText(scheduleItem.getScheduleName());
        activity.setText(scheduleItem.getActivity() + " using");
        device.setText(scheduleItem.getDevice());
        time.setText(scheduleItem.getTime());

        return rowView;
    }


    private void deleteSchedule(String name){
        String url = port + "/delete_ms_profile?name=" + name;

        JSONObject jsonBody = new JSONObject();
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
        Volley.newRequestQueue(context).add(jsObjRequest);

    }

}