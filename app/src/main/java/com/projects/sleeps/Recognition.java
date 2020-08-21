package com.projects.sleeps;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recognition extends IntentService {
    String file_name = "result file";
    private static final String TAGS = "RecognitionActivity";
    private PendingIntent myPendingIntent;

    public Recognition(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity(result.getProbableActivities());
        }
    }

    public void setUpActivityTransitions(){
        List<ActivityTransition> transitions=new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.TILTING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.TILTING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.UNKNOWN)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.UNKNOWN)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request=new ActivityTransitionRequest(transitions);
        Task<Void> task= com.google.android.gms.location.ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, myPendingIntent);
        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }
        );
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }
        );
    }

    private void DetectedActivity(List<DetectedActivity> probableActivities) {
        long time=System.currentTimeMillis();
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.d(TAGS, "IN VEHICLE" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": In vehicle for " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.d(TAGS, "IN BICYCLE" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": In bicycle for " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.d(TAGS, "ON FOOT" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": on foot for " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.d(TAGS, "RUNNING" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": running " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.d(TAGS, "STILL" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": still: " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.d(TAGS, "WALKING" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": walking" + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.d(TAGS, "TILTING" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": tilting: " + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.d(TAGS, "UNKNOWN" + activity.getConfidence());
                    try {
                        FileOutputStream outputStream = openFileOutput(file_name, Context.MODE_APPEND);
                        outputStream.write((time + ": unknown :" + activity.getConfidence() + "% \n").getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

    }
}
