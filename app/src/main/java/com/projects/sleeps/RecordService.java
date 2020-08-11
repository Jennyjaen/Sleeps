package com.projects.sleeps;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.projects.sleeps.GoogleFit;
import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.projects.sleeps.Service.CHANNEL_ID;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.Task;
import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permissions;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class RecordService extends Service implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor_light, sensor_accel, sensor_gyro, sensor_cnstep, sensor_dtstep, sensor_rotate, sensor_motion, sensor_pose, sensor_sigmotion, sensor_linaccel;
    FileOutputStream outputStream;
    String file_name = "result file";
    private static final String TAG = "Google Fitness Service";
    private MainActivity activity;
    private long totalSleepTime;
    private GoogleFit googleFit;

    @Override
    public void onCreate() {
        android.util.Log.i("Test", "onCreate()");
        super.onCreate();


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
        return START_STICKY;
    }

    public void startMeasurement(){
        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensor_accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_linaccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensor_gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_cnstep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensor_dtstep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_rotate = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensor_motion = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        sensor_pose = sensorManager.getDefaultSensor(Sensor.TYPE_POSE_6DOF);
        sensor_sigmotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

        sensorManager.registerListener(this, sensor_light, 1000000);
        sensorManager.registerListener(this, sensor_accel, 1000000);
        sensorManager.registerListener(this, sensor_linaccel, 1000000);
        sensorManager.registerListener(this, sensor_gyro, 1000000);
        sensorManager.registerListener(this, sensor_cnstep, (SensorManager.SENSOR_DELAY_NORMAL * 5));
        sensorManager.registerListener(this, sensor_dtstep, (SensorManager.SENSOR_DELAY_NORMAL * 5));
        sensorManager.registerListener(this, sensor_rotate, 1000000);
        sensorManager.registerListener(this, sensor_motion, (SensorManager.SENSOR_DELAY_NORMAL * 5));
        sensorManager.registerListener(this, sensor_sigmotion, (SensorManager.SENSOR_DELAY_NORMAL * 5));
        sensorManager.registerListener(this, sensor_pose, (SensorManager.SENSOR_DELAY_NORMAL * 5));

    }

    private void stopMeasurement(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement();

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
        } else if (Sensor.TYPE_STEP_DETECTOR == sensorEvent.sensor.getType()) {
            // += sensorEvent.values[0];
            //tv_dtstep.setText(String.valueOf(stepdetect));
        } else if (Sensor.TYPE_ROTATION_VECTOR == sensorEvent.sensor.getType()) {
            try {
                outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                outputStream.write((cur_Time + ": Rotation-x : " + String.valueOf(sensorEvent.values[0]) + " Rotation-y: " + String.valueOf(sensorEvent.values[1]) + " Rotation-z: " + String.valueOf(sensorEvent.values[2])).getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //tv_rotate.setText(String.valueOf(sensorEvent.values[0]));
        } else if (Sensor.TYPE_MOTION_DETECT == sensorEvent.sensor.getType()) {
            //tv_motion.setText(String.valueOf(sensorEvent.values[0]));
        } else if (Sensor.TYPE_SIGNIFICANT_MOTION == sensorEvent.sensor.getType()) {
            //tv_sigmotion.setText(String.valueOf(sensorEvent.values[0]));
        } else if (Sensor.TYPE_POSE_6DOF == sensorEvent.sensor.getType()) {
            //tv_pose.setText(String.valueOf(sensorEvent.values[0]));
        } else if (Sensor.TYPE_HEART_BEAT == sensorEvent.sensor.getType()) {
           // tv_heartb.setText(String.valueOf(sensorEvent.values[0]));
        } else if (Sensor.TYPE_HEART_RATE == sensorEvent.sensor.getType()) {
           // tv_heartr.setText(String.valueOf(sensorEvent.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
