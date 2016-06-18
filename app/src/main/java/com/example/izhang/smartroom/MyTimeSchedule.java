package com.example.izhang.smartroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTimeSchedule.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTimeSchedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTimeSchedule extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String port = "http://172.16.0.6:5050";
    TinyDB internalDbRef = null;
    Firebase firebaseRef = null;
    Spinner chooseSong;
    String songName = "";
    private ListView scheduleList = null;
    private ArrayList<Schedule> scheList = null;
    private MotionListAdapter listAdapter = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyTimeSchedule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTimeSchedule.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTimeSchedule newInstance(String param1, String param2) {
        MyTimeSchedule fragment = new MyTimeSchedule();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        firebaseRef = new Firebase("https://smartroom490.firebaseio.com/library/songs");

        View view = inflater.inflate(R.layout.fragment_my_time_schedule, container, false);

        internalDbRef = new TinyDB(getContext());

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.myScheduleFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSchedule(getActivity());
            }
        });

        scheduleList = (ListView) view.findViewById(R.id.timeListView);

        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Create action window to make lockcode
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete Schedule?");



                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteSchedule(scheList.get(position).getScheduleName());
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                alert.show();
            }
        });

        scheList = internalDbRef.getListObject("timeScheList", Schedule.class);
        if(scheList == null) {
            scheList = new ArrayList<>();
            scheList.add(new Schedule("Alarm 1", "Doing something", "Light Bulb 1", ""));
            scheList.add(new Schedule("Alarm 2", "Doing something", "Light Bulb 1", ""));
            scheList.add(new Schedule("Alarm 3", "Doing something", "Light Bulb 1", ""));
        }


        listAdapter = new MotionListAdapter(getContext(), scheList);

        scheduleList.setAdapter(listAdapter);

        return view;
    }


    /**
     * Creates an alert dialog to ask user for device name and connection
     *
     * @param fragmentActivity
     */
    private void addNewSchedule(final FragmentActivity fragmentActivity){
        // Create action window to make lockcode
        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentActivity);
        alert.setTitle("New Time Schedule");

        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addtimeschedule, null);
        alert.setView(dialogView);

        final TextView scheName = (TextView) dialogView.findViewById(R.id.nameBox);
        final Spinner chooseDevice = (Spinner) dialogView.findViewById(R.id.chooseDeviceSpinner);
        final Spinner chooseActivity = (Spinner) dialogView.findViewById(R.id.chooseActivitySpinner);
        chooseSong = (Spinner) dialogView.findViewById(R.id.chooseSong);
        final TextView hours = (TextView) dialogView.findViewById(R.id.timeHour);
        final TextView minutes = (TextView) dialogView.findViewById(R.id.timeMinutes);
        final TextView chooseSongTitle = (TextView)dialogView.findViewById(R.id.textView8);


        final ArrayList<String> songList = new ArrayList<>();

        //
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    DataSnapshot songObj = (DataSnapshot)iter.next();
                    songList.add(songObj.getValue().toString());
                }

                ArrayAdapter<String> songAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, songList);
                chooseSong.setAdapter(songAdapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Activities
        final ArrayList speakerActivities = new ArrayList();
        speakerActivities.add("Start Music");
        speakerActivities.add("Stop Music");

        final ArrayList lightActivities = new ArrayList();
        lightActivities.add("Pulse");
        lightActivities.add("Breath");
        lightActivities.add("Cycle");

        // Devices
        final ArrayList deviceList = new ArrayList();
        deviceList.add("Speaker 1");
        deviceList.add("Lightbulb 1");

        chooseActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 && deviceList.get(chooseDevice.getSelectedItemPosition()).equals("Speaker 1")){
                    chooseSong.setVisibility(View.VISIBLE);
                    chooseSongTitle.setVisibility(View.VISIBLE);
                }else{
                    chooseSong.setVisibility(View.INVISIBLE);
                    chooseSongTitle.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter deviceListAdapter = new ArrayAdapter(dialogView.getContext(),
                R.layout.support_simple_spinner_dropdown_item, deviceList);

        chooseDevice.setAdapter(deviceListAdapter);

        chooseDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ArrayAdapter deviceActivityAdapter = new ArrayAdapter(dialogView.getContext(),
                            R.layout.support_simple_spinner_dropdown_item, speakerActivities);
                    chooseActivity.setAdapter(deviceActivityAdapter);
                }else{
                    ArrayAdapter deviceActivityAdapter = new ArrayAdapter(dialogView.getContext(),
                            R.layout.support_simple_spinner_dropdown_item, lightActivities);
                    chooseActivity.setAdapter(deviceActivityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alert.setPositiveButton("Schedule", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String scheduleName = scheName.getText().toString();
                String device = deviceList.get(chooseDevice.getSelectedItemPosition()).toString();
                String activity = "";
                if(device.equals("Speaker 1")){
                    activity = speakerActivities.get(chooseActivity.getSelectedItemPosition()).toString();
                }else{
                    activity = lightActivities.get(chooseActivity.getSelectedItemPosition()).toString();
                }

                activity = getRequestName(activity);

                songName = songList.get(chooseSong.getSelectedItemPosition());

                String hourVal = hours.getText().toString();
                int hourIntVal = Integer.parseInt(hourVal);
                String minuteVal = minutes.getText().toString();
                int minIntVal = Integer.parseInt(minuteVal);


                Date date = new Date();   // given date
                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                calendar.setTime(date);   // assigns calendar to given date
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                int minute = calendar.get(Calendar.MINUTE);        // gets hour in 12h format
                int second = calendar.get(Calendar.SECOND);

                minute = Math.abs(minute - minIntVal);
                hour = Math.abs(hour - hourIntVal);

                int seconds = (hour * 360) + (minute * 60) + (second - 60);

                String secondBuf = "0";

                String secondsToString = seconds + "";

                Log.v("MyTimeSchedule", " seconds: " + secondsToString);

                Schedule sche = new Schedule(scheduleName, device, activity, hourVal + ":" + minuteVal);
                scheList.add(sche);
                listAdapter.notifyDataSetChanged();

                setSchedule(scheduleName, activity, secondsToString);

            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }


    @Override
    public void onPause(){
        super.onPause();
        internalDbRef.remove("timeScheList");
        internalDbRef.putListObject("timeScheList", (ArrayList<Object>) (Object) scheList);
    }


    private String getRequestName(String activity){
        switch (activity){
            case "Pulse":
                return "set_pulse";
            case "Breath":
                return "set_breath";
            case "Cycle":
                return "set_cucle";
            case "Start Music":
                return "start_song";
            case "Stop Music":
                return "stop_song";
        }

        return activity;
    }


    /**
     * New Schedule: /schedule_event?name=<name>&action=<action>&hour=<hour>&min=<min>
     * POST: input JSON depending on the action chosen
     * Send to this along with the JSON data for the ACTION
     *
     * @param name
     * @param activity
     * @param seconds
     *
     *
     */
    private void setSchedule(String name, String activity, String seconds){
        String url = port + "/schedule_event?name=" + name + "&action=" + activity + "&seconds=" + seconds;

        Log.v("MyTimeSchedule", url);

        JSONObject jsonBody = new JSONObject();

        if(activity.equals("start_song")) {
            try {
                jsonBody = new JSONObject("{\"song\":\"" + songName + "\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        Volley.newRequestQueue(getContext()).add(jsObjRequest);

    }


    private void deleteSchedule(String name){

        for(int i = 0; i < scheList.size(); i++) {
            if(scheList.get(i).getScheduleName().equals(name)){
                scheList.remove(i);
                listAdapter.notifyDataSetChanged();
            }
        }

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
        Volley.newRequestQueue(getContext()).add(jsObjRequest);

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
