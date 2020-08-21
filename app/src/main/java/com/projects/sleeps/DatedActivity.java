package com.projects.sleeps;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

public class DatedActivity {
    DetectedActivity detectedActivity;
    Date date;

    public DatedActivity(DetectedActivity detectedActivity){
        this.detectedActivity=detectedActivity;
        date=new Date();
    }

    public String getType(){
     int type_num=detectedActivity.getType();
     String what="Can not get type of activity";
     if(type_num==DetectedActivity.IN_VEHICLE){
         what="In vehicle";
     }
     else if(type_num==DetectedActivity.ON_BICYCLE){what="On bicycle";}
     else if(type_num==DetectedActivity.ON_FOOT){what="On foot";}
     else if(type_num==DetectedActivity.RUNNING){what="Running";}
     else if(type_num==DetectedActivity.STILL){what="Still";}
     else if(type_num==DetectedActivity.TILTING){what="Tilting";}
     else if(type_num==DetectedActivity.WALKING){what="Walking";}
     else if(type_num==DetectedActivity.UNKNOWN){what="Unknown";}

     return what;
    }

    public int getConfidence(){
        return detectedActivity.getConfidence();
    }
}
