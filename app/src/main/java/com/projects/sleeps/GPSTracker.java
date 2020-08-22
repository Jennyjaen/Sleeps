package com.projects.sleeps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.projects.sleeps.RecordService;

public class GPSTracker {
    RecordService recordService=new RecordService();
    private ArrayList<Location> locations;
    public GPSTracker(){
        locations=new ArrayList<Location>();
    }
    public void LogGPS(Location location){
        locations.add(location);
    }
    public void GetGPS(){
        for(Location location: locations){
            long cur_Time=System.currentTimeMillis();
            String lat, lon, accuracy, altitude, speed, address,bearing;

            lat=String.valueOf(location.getLatitude());
            lon=String.valueOf(location.getLongitude());
            accuracy=String.valueOf(location.getAccuracy());

            if(location.hasBearing()){
                bearing=String.valueOf(location.getBearing());
            }
            else{
                bearing="No Bearing Value";
            }
            if (location.hasAltitude()) {
                altitude=String.valueOf(location.getAltitude());
            } else {
                altitude="No Altitude Value";
            }
            if (location.hasSpeed()) {
                speed=String.valueOf(location.getSpeed());
            } else {
                speed="No Speed Value";
            }


           /* Geocoder geocoder = new Geocoder(GPSTracker.this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                address=addresses.get(0).getAddressLine(0);
            } catch (Exception e) {
                address ="Cannot find address";
            }*/
            recordService.writegeo(lat, lon, speed, /*address,*/ accuracy, altitude, bearing);
            try {
                recordService.outputStream = recordService.openFileOutput(recordService.file_name, Context.MODE_APPEND);
                recordService.outputStream.write((cur_Time + ": Lonitude: " +lon+ "\n"+ ": Latitude: " +lat+ "\n"+ ": Accuracy: " +accuracy+ "\n"+ ": Altitude: " +altitude+ "\n"
                        + ": Speed: " +speed+ "\n"+ /*": Address: " +address+ "\n"*+*/": Bearing: "+bearing).getBytes());

                recordService.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getAdress(Context context, double lat, double lon){
        Geocoder geocoder=new Geocoder(context, Locale.KOREA);
        String result="Can not find address";

        try{
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            result=addresses.get(0).getAddressLine(0);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
