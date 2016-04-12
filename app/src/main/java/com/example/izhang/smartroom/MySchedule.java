package com.example.izhang.smartroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String port = "http://172.16.0.4:5050";


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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.myScheduleFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSchedule(getActivity());
            }
        });




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
        final View dialogView = inflater.inflate(R.layout.dialog_addschedule, null);
        alert.setView(dialogView);

        final TextView scheName = (TextView) dialogView.findViewById(R.id.nameBox);
        Spinner chooseDevice = (Spinner) dialogView.findViewById(R.id.chooseDeviceSpinner);
        Spinner chooseActivity = (Spinner) dialogView.findViewById(R.id.chooseActivitySpinner);
        Spinner chooseWhen = (Spinner) dialogView.findViewById(R.id.chooseWhenSpinner);


        // Devices
        ArrayList deviceList = new ArrayList();
        deviceList.add("Speaker 1");

        ArrayAdapter deviceListAdapter = new ArrayAdapter(dialogView.getContext(),
                android.R.layout.simple_spinner_item, deviceList);

        chooseDevice.setAdapter(deviceListAdapter);


        // Activities
        ArrayList deviceActivities = new ArrayList();
        deviceActivities.add("Start Playing Music");

        ArrayAdapter deviceActivityAdapter = new ArrayAdapter(dialogView.getContext(),
                android.R.layout.simple_spinner_item, deviceActivities);

        chooseActivity.setAdapter(deviceActivityAdapter);


        // When
        ArrayList deviceWhen = new ArrayList();
        deviceActivities.add("Motion Sensor Detected");

        ArrayAdapter deviceWhenAdatper = new ArrayAdapter(dialogView.getContext(),
                android.R.layout.simple_spinner_item, deviceActivities);

        chooseWhen.setAdapter(deviceActivityAdapter);


        alert.setPositiveButton("Schedule It", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(fragmentActivity, "Scheduled", Toast.LENGTH_LONG).show();
                setSchedule(scheName.getText().toString(), "start_song");
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
     * New Schedule: url/save_ms_profile?name=<PROFILE_NAME>&action=<ACTION>
     * Send to this along with the JSON data for the ACTION
     * @param name
     * @param action
     */
    private void setSchedule(String name, String action){
        String url = port + "/save_ms_profile?name=" + name + "&action=" + action;

        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody = new JSONObject("{\"song\":\""+ "7 Years.mp3" +"\"}");
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
        Volley.newRequestQueue(getContext()).add(jsObjRequest);

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
