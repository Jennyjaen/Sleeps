package com.projects.sleeps;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ActivityRecognition extends IntentService {
    String file_name = "result file";
    private static final String TAGS = "RecognitionActivity";
    public ActivityRecognition(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity(result.getProbableActivities());
        }
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
