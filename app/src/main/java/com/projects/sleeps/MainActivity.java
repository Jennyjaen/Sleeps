package com.projects.sleeps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 3;
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS = {
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
            }
        }}

    public void startService(View v){
        //Toast.makeText(this, "click good", Toast.LENGTH_SHORT).show();
        Intent serviceIntent=new Intent(this, RecordService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent=new Intent(this, RecordService.class);
        stopService(serviceIntent);
    }
}