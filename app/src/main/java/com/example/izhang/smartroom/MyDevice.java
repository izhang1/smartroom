package com.example.izhang.smartroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyDevice.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyDevice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDevice extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String port = "http://172.16.0.6:5050";

    private OnFragmentInteractionListener mListener;

    public MyDevice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyDevice.
     */
    // TODO: Rename and change types and number of parameters
    public static MyDevice newInstance() {
        MyDevice fragment = new MyDevice();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("MyDevice", "Within OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_device, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.myDeviceFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDevice(getActivity());
            }
        });

        final TextView cube1 = (TextView) view.findViewById(R.id.cubeDevice1);
        cube1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cubeDevice = new Intent(view.getContext(), CubeDevice.class);
                startActivity(cubeDevice);
            }
        });

        final TextView light = (TextView) view.findViewById(R.id.lightDevice1Text);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lightDevice = new Intent(view.getContext(), LightDevice.class);
                startActivity(lightDevice);
            }
        });

        final TextView speaker1 = (TextView) view.findViewById(R.id.speakerDeviceText);
        speaker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speakerDevice = new Intent(view.getContext(), MusicPlayer.class);
                startActivity(speakerDevice);
            }
        });

        final Switch lightSwitch = (Switch) view.findViewById(R.id.lightSwitch);
        SharedPreferences prefs = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
        boolean lightisOn = prefs.getBoolean("lightSwitch", false);
        if(lightisOn){
            lightSwitch.setChecked(true);
        }else{
            lightSwitch.setChecked(false);
        }

        lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lightSwitch.isChecked()){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE).edit();
                    editor.putBoolean("lightSwitch", true);
                    editor.commit();

                    toggleLightPower(view.getContext());

                }else{
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE).edit();
                    editor.putBoolean("lightSwitch",false);
                    editor.commit();
                }
            }
        });

        return view;
    }

    private void toggleLightPower(Context context){
        String url = port + "/toggle_power";

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
        Volley.newRequestQueue(context).add(stringRequest);
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

    /**
     * Creates an alert dialog to ask user for device name and connection
     *
     * @param fragmentActivity
     */
    private void addNewDevice(final FragmentActivity fragmentActivity){
        // Create action window to make lockcode
        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentActivity);
        alert.setTitle("Add New Device");

        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_adddevice, null);
        alert.setView(dialogView);

        final EditText deviceName = (EditText) dialogView.findViewById(R.id.nameBox);
        final ListView deviceList = (ListView) dialogView.findViewById(R.id.deviceList);

        final ArrayList<String> deviceTypes = new ArrayList<>();
        deviceTypes.add("Motion Sensor");
        deviceTypes.add("Web Cam");
        deviceTypes.add("Light Bulb");
        deviceTypes.add("Speaker");
        deviceTypes.add("Thermometer");

        ArrayAdapter msgListAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_checked, deviceTypes);
        deviceList.setAdapter(msgListAdapter);
        deviceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        deviceList.setItemChecked(0, true);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceList.setItemChecked(position, true);
                deviceList.setSelection(position);
            }
        });

        alert.setPositiveButton("Add Device", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(fragmentActivity, "Adding the device: " + deviceName.getText().toString() + " Type: ", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }
}
