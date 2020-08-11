package com.projects.sleeps;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Service extends Application {
    public static final String CHANNEL_ID="RecordServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel=new NotificationChannel(CHANNEL_ID, "Record Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
