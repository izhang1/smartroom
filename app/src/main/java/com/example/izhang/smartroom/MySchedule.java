package com.example.izhang.smartroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MySchedule.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MySchedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySchedule extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView scheduleList = null;
    Spinner chooseSong;
    String songName = "";
    MotionListAdapter listAdapter = null;
    Firebase firebaseRef;
    TinyDB internalDbRef = null;
    private ArrayList<Schedule> scheList = null;

    private String port = "http://172.16.0.6:5050";


    private OnFragmentInteractionListener mListener;

    public MySchedule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MySchedule.
     */
    // TODO: Rename and change types and number of parameters
    public static MySchedule newInstance(String param1, String param2) {
        MySchedule fragment = new MySchedule();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        internalDbRef = new TinyDB(getContext());

        firebaseRef = new Firebase("https://smartroom490.firebaseio.com/library/songs");


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.myScheduleFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSchedule(getActivity());
            }
        });


        scheduleList = (ListView) view.findViewById(R.id.motionListView);

        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Create action window to make lockcode
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete Motion Schedule?");

                Log.v("MySchedule", "Sup Dog");


                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.v("MySchedule", scheList.get(position).getScheduleName());
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


        scheList = internalDbRef.getListObject("motionScheList", Schedule.class);
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
    public void onPause(){
        super.onPause();
        internalDbRef.remove("motionScheList");
        internalDbRef.putListObject("motionScheList", (ArrayList<Object>)(Object) scheList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Creates an alert dialog to ask user for device name and connection
     *
     * @param fragmentActivity
     */
    private void addNewSchedule(final FragmentActivity fragmentActivity){
        // Create action window to make lockcode
        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentActivity);
        alert.setTitle("Add New Schedule");

        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addmotionschedule, null);
        alert.setView(dialogView);

        final TextView scheName = (TextView) dialogView.findViewById(R.id.nameBox);
        final Spinner chooseDevice = (Spinner) dialogView.findViewById(R.id.chooseDeviceSpinner);
        final Spinner chooseActivity = (Spinner) dialogView.findViewById(R.id.chooseActivitySpinner);
        chooseSong = (Spinner) dialogView.findViewById(R.id.chooseSong);
        final TextView chooseSongTitle = (TextView) dialogView.findViewById(R.id.chooseSongTitle);


        final ArrayList<String> songList = new ArrayList<>();

        // Activities
        final ArrayList speakerActivities = new ArrayList();
        speakerActivities.add("Start Music");
        speakerActivities.add("Stop Music");

        final ArrayList lightActivities = new ArrayList();
        lightActivities.add("Pulse");
        lightActivities.add("Breathe");
        lightActivities.add("Cycle");
        lightActivities.add("Turn On");


        // Devices
        final ArrayList deviceList = new ArrayList();
        deviceList.add("Speaker 1");
        deviceList.add("Lightbulb 1");

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


        alert.setPositiveButton("Schedule It", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(fragmentActivity, "Scheduled", Toast.LENGTH_LONG).show();
                String sche = scheName.getText().toString();
                String device = deviceList.get(chooseDevice.getSelectedItemPosition()).toString();
                String activity = "";
                if(device.equals("Speaker 1")){
                    activity = speakerActivities.get(chooseActivity.getSelectedItemPosition()).toString();
                }else{
                    activity = lightActivities.get(chooseActivity.getSelectedItemPosition()).toString();
                }

                songName = songList.get(chooseSong.getSelectedItemPosition());


                Log.v("MySchedule", activity);

                activity = getRequestName(activity);

                Log.v("MySchedule", activity);

                Schedule schedule = new Schedule(sche, activity, device, "");
                scheList.add(schedule);
                listAdapter.notifyDataSetChanged();
                setSchedule(sche, activity, "-1", "-1");
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }


    /**
     * New Schedule: /schedule_event?name=<name>&action=<action>&hour=<hour>&min=<min>
     * POST: input JSON depending on the action chosen
     * Send to this along with the JSON data for the ACTION
     *
     * @param name
     * @param activity
     * @param hour
     * @param minutes
     *
     */
    private void setSchedule(String name, String activity, String hour, String minutes){


        String url = port + "/save_ms_profile?name=" + name + "&action=" + activity + "&start_hour=" + hour + "&start_min=" + minutes + "&end_hour=" + minutes + "&end_min=" + minutes;

        Log.v("MySchedule", url);

        JSONObject jsonBody = new JSONObject();

        if(activity.equals("start_song")) {
            Log.v("SongName", songName);
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


    private String getRequestName(String activity){
        switch (activity){
            case "Pulse":
                return "set_pulse";
            case "Breathe":
                return "set_breathe";
            case "Cycle":
                return "set_cycle";
            case "Start Music":
                return "start_song";
            case "Stop Music":
                return "stop_song";
            case "Turn On":
                return "toggle_power";

        }

        return activity;
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
