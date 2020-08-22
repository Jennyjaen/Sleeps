package com.projects.sleeps;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.projects.sleeps.Service.CHANNEL_ID;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RecordService extends Service implements SensorEventListener {
    private static final int DEFAULT_UPDATE = 60;
    private static final int FAST_UPDATE = 1;
    SensorManager sensorManager;
    Sensor sensor_light, sensor_accel, sensor_gyro, sensor_cnstep, sensor_dtstep, sensor_rotate, sensor_motion, sensor_pose, sensor_sigmotion, sensor_linaccel;
    FileOutputStream outputStream;
    String file_name = "result file"+".json";
    private static final String TAG = "Google Fitness Service";
    private MainActivity activity;
    private long totalSleepTime;
    private GoogleFit googleFit;
    private Recognition activityRecognition;
    LocationListener locationListener;
    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private List<DatedActivity> datedActivities=new ArrayList<>();
    private Observable<DatedActivity> observable;
    GPSListener gpsListener;

    File file=new File(this.getFilesDir(), file_name);
    //FileWriter fileWriter=new FileWriter(file);
    JSONObject jsonObject=new JSONObject();
    JSONObject geoObject=new JSONObject();
    JSONObject sleepObject=new JSONObject();
    JSONObject activityObject=new JSONObject();
    JSONObject sensorObject=new JSONObject();
    private GPSTracker gpsTracker;


    @Override
    public void onCreate() {
        android.util.Log.i("Test", "onCreate()");
        super.onCreate();
        }

    public void writegeo(String lat, String lon, String speed, /*String add,*/ String accuracy, String altitude, String bear){
        long cur_time=System.currentTimeMillis();
        try {
            geoObject.put(cur_time+" " +"Latitude: ", lat);
            geoObject.put(cur_time+" " +"Lonitude: ", lon);
            geoObject.put(cur_time+" " +"Altitude: ", altitude);
            geoObject.put(cur_time+" " +"Speed: ", speed);
            geoObject.put(cur_time+" " +"Accuracy: ", accuracy);
            //geoObject.put(cur_time+" " +"Address: ", add);
            geoObject.put(cur_time+" " +"Bearing: ", bear);
            //jsonObject.put(cur_time+" " +"geo "+String.valueOf(cur_time)+":",geoObject);

        }catch (JSONException e){e.printStackTrace();}

    }

    public void writesensor(String type, String val_x, String val_y, String val_z){
        long cur_time=System.currentTimeMillis();
        try {
            //sensorObject.put("Latitude", lat);
            if(type=="Accel"||type=="Gyro"||type=="Lin Accel"||type=="Rotate"){
                sensorObject.put(cur_time+" " +type+": x axis: ", val_x);
                sensorObject.put(cur_time+" " +type+": y axis: ", val_y);
                sensorObject.put(cur_time+" " +type+": z axis: ", val_z);
            }
            else if(type=="Light"||type=="Step"){
                sensorObject.put(cur_time+" " +type+": ", val_x);
            }
            //jsonObject.put("sensor "+String.valueOf(cur_time)+":",sensorObject);

        }catch (JSONException e){e.printStackTrace();}

    }

    public void writesleep(String type, long start_time, long end_time){
        try {
            sleepObject.put(type+": ", start_time+" to "+end_time);
            //jsonObject.put("sleep "+String.valueOf(cur_time)+":",geoObject);
        }catch (JSONException e){e.printStackTrace();}

    }

    public void writeactivity(String activity_type, int confidence,long time){
        try {
            activityObject.put(activity_type, confidence+"% in "+time);

            //jsonObject.put("activity "+String.valueOf(cur_time)+":",geoObject);

        }catch (JSONException e){e.printStackTrace();}

    }

    public void writeall(){
        long cur_time=System.currentTimeMillis();
        try {
            jsonObject.put("sensor "+String.valueOf(cur_time)+":",sensorObject);
            jsonObject.put("sleep "+String.valueOf(cur_time)+":",sleepObject);
            jsonObject.put("geo "+String.valueOf(cur_time)+":",geoObject);
            jsonObject.put("activity "+String.valueOf(cur_time)+":",activityObject);

        }catch (JSONException e){e.printStackTrace();}

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,notificationIntent, 0);
        Notification notification=new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleeps Service")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        startMeasurement();
        startGPSservice();
        collectFit();
        return START_STICKY;
    }


    @SuppressLint("MissingPermission")
    private void startGPSservice() {
        if(locationManager==null){
        locationManager=(LocationManager)getSystemService(this.LOCATION_SERVICE);}
        if(gpsListener==null){
            gpsListener=new GPSListener(gpsTracker);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 0, gpsListener);

        //startLocationUpdates();
    }


    public void startMeasurement(){
        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensor_accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_linaccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensor_gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_cnstep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensor_rotate = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensor_motion = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);

        sensorManager.registerListener(this, sensor_light, 1000000);
        sensorManager.registerListener(this, sensor_accel, 1000000);
        sensorManager.registerListener(this, sensor_linaccel, 1000000);
        sensorManager.registerListener(this, sensor_gyro, 1000000);
        sensorManager.registerListener(this, sensor_cnstep, (SensorManager.SENSOR_DELAY_NORMAL * 5));
        sensorManager.registerListener(this, sensor_rotate, 1000000);
        sensorManager.registerListener(this, sensor_motion, (SensorManager.SENSOR_DELAY_NORMAL * 5));

    }

    private void stopMeasurement(){
        sensorManager.unregisterListener(this);
    }


    @RequiresApi(api= Build.VERSION_CODES.N)
    public void collectFit(){
        GoogleSignInOptionsExtension fitnessOptions=FitnessOptions.builder()
                .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();

        try{
            GoogleSignInAccount googleSignInAccount=GoogleSignIn.getLastSignedInAccount(this);
            if(GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)){
                googleFit.accessSleepData();
                //activityRecognition.onHandleIntent();
            }
        }catch (Exception e){
            Log.e("Record Service","collect denied");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement();
        stopGPSservice();

    }

    private void stopGPSservice() {
        locationManager.removeUpdates(gpsListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long cur_Time = System.currentTimeMillis();
        //Toast.makeText(this, "sensor is working!", Toast.LENGTH_SHORT).show();
        if (Sensor.TYPE_LIGHT == sensorEvent.sensor.getType()) {
            //tv_light.setText(String.valueOf(sensorEvent.values[0]));
            writesensor("Light", String.valueOf(sensorEvent.values[0]),null,null);
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Light: " + String.valueOf(sensorEvent.values[0]) + "\n").getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Sensor.TYPE_ACCELEROMETER == sensorEvent.sensor.getType()) {
            //tv_accel.setText("x: " + String.valueOf(sensorEvent.values[0]) + "y: " + String.valueOf(sensorEvent.values[1]) + "z: " + String.valueOf(sensorEvent.values[2]));
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Accelometer-x : " + String.valueOf(sensorEvent.values[0]) + " Accelometer-y: " + String.valueOf(sensorEvent.values[1]) + " Accelometer-z: " + String.valueOf(sensorEvent.values[2])).getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Sensor.TYPE_LINEAR_ACCELERATION == sensorEvent.sensor.getType()) {
            //tv_linaccel.setText("x: " + String.valueOf(sensorEvent.values[0]) + "y: " + String.valueOf(sensorEvent.values[1]) + "z: " + String.valueOf(sensorEvent.values[2]));
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Linear Accelometer-x : " + String.valueOf(sensorEvent.values[0]) + " Linear Accelometer-y: " + String.valueOf(sensorEvent.values[1]) + " Linear Accelometer-z: " + String.valueOf(sensorEvent.values[2])).getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Sensor.TYPE_GYROSCOPE == sensorEvent.sensor.getType()) {
            //tv_gyro.setText("x: " + String.valueOf(sensorEvent.values[0]) + "y: " + String.valueOf(sensorEvent.values[1]) + "z: " + String.valueOf(sensorEvent.values[2]));
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Gyroscope-x : " + String.valueOf(sensorEvent.values[0]) + " Gyroscope-y: " + String.valueOf(sensorEvent.values[1]) + " Gyroscope-z: " + String.valueOf(sensorEvent.values[2])).getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Sensor.TYPE_STEP_COUNTER == sensorEvent.sensor.getType()) {
            //tv_cnstep.setText(String.valueOf(sensorEvent.values[0]));
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Step Counter: " + String.valueOf(sensorEvent.values[0]) + "\n").getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } /*else if (Sensor.TYPE_STEP_DETECTOR == sensorEvent.sensor.getType()) {
            // += sensorEvent.values[0];
            //tv_dtstep.setText(String.valueOf(stepdetect));
        }*/ else if (Sensor.TYPE_ROTATION_VECTOR == sensorEvent.sensor.getType()) {
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Rotation-x : " + String.valueOf(sensorEvent.values[0]) + " Rotation-y: " + String.valueOf(sensorEvent.values[1]) + " Rotation-z: " + String.valueOf(sensorEvent.values[2])).getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //tv_rotate.setText(String.valueOf(sensorEvent.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
