package com.example.izhang.smartroom;

/**
 * Created by izhang on 4/27/16.
 */
public class Schedule {
    private String scheduleName;
    private String activity;
    private String device;
    private String time;

    public Schedule(String scheduleName, String activity, String device, String time){
        this.scheduleName = scheduleName;
        this.activity = activity;
        this.device = device;
        this.time = time;
    }


    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
