package com.projects.sleeps;


import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;

public class GoogleFit extends AppCompatActivity {
    private static final String TAG = "Google Fitness Service";
    private MainActivity activity;
    private long totalSleepTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setup(){
        FitnessOptions fitnessOptions=FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();
        GoogleSignInAccount account=GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if(!GoogleSignIn.hasPermissions(account, fitnessOptions)){
            GoogleSignIn.requestPermissions(this, /**/1, account, fitnessOptions);}
        else{
            accessGoogleFit();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void accessSleepData(){
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        long startTime=cal.getTimeInMillis() -14400000;/*20시*/

        //Should the time unit be days?
        SessionReadRequest request=new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                .read(TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();
        Task<SessionReadResponse> task=Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this)).readSession(request);
        task.addOnSuccessListener(response->{
            List<Session> sleepSessions=response.getSessions().stream()
                    .collect(Collectors.toList());

            //If no data repeat
            if(sleepSessions.size()==0){
                accessSleepData();
            }

            for(Session session: sleepSessions){
                this.totalSleepTime=session.getEndTime(TimeUnit.MILLISECONDS)-session.getStartTime(TimeUnit.MILLISECONDS);

                List<DataSet> dataSets=response.getDataSet(session);
                for(DataSet dataSet: dataSets){
                    for(DataPoint point: dataSet.getDataPoints()){
                        String sleepStage=point.getValue(Field.FIELD_ACTIVITY).asActivity();
                        int sleepStageint=point.getValue(Field.FIELD_ACTIVITY).asInt();
                        switch (sleepStageint){
                            case 72:
                                sleepStage="Sleep";
                                break;
                            case 109:
                                sleepStage="Light Sleep";
                                break;
                            case 110:
                                sleepStage="Deep Sleep";
                                break;
                            case 111:
                                sleepStage="REM";
                                break;
                            case  112:
                                sleepStage="Awake";
                                break;
                        }
                        long start=point.getStartTime(TimeUnit.MILLISECONDS);
                        long end=point.getEndTime(TimeUnit.MILLISECONDS);

                    }
                }
            }

        });
    }
    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();



        Fitness.getHistoryClient(this,GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    // Use response data here

                    Log.d(TAG, "OnSuccess()");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "OnFailure()", e);
                });
    }


}
