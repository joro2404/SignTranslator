package com.example.voicechanger;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AccService extends AccessibilityService {

    private static String LOG_TAG_S = "result";
    AudioManager myAudioManager;

    public static final String CHANNEL_ID = "MyAccessibilityService";

    private void startForegroundService() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,           0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle("recording Service")
                .setContentText("Start")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recording Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }


    public static int calculate(int sampleRate, short [] audioData){

        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0))
            {
                numCrossing++;
            }
        }
        Log.d("result", String.valueOf(numCrossing));

        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;

        return (int)frequency;
    }

    @Override
    public void onServiceConnected() {
/*
        //==============To Record Audio wile Call received=================
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes=AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.packageNames = null;
        setServiceInfo(info);

        startForegroundService();
        final int stpThread = 0;

        Thread thread = new Thread(){
            public void run(){
                    Thread thread1 = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            while(stpThread == 0) {myAudioManager.setMode(3);
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread1.start();


                    Thread thread2 = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            while(stpThread == 0) {
                                myAudioManager.setMicrophoneMute(true);
                                myAudioManager.setMicrophoneMute(false);
                            }
                        }
                    };
                    thread2.start();


                    /*
                    int minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                    AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
                    ar.startRecording();
                    minSize = minSize/10;

                    boolean recorder = true;
                    short[] buffer = new short[minSize];
                    ar.startRecording();
                    //Log.d("SIZE", String.valueOf(minSize));

                    while (ar.read(buffer, 0, minSize) != -1) {
                        //MyRunnable runnable = new MyRunnable(buffer);
                        //runnable.run();
                        int result = calculate(minSize, buffer);
                        //Log.d("result", String.valueOf(result));
                        if(result > 1){
                            myAudioManager.setMicrophoneMute(true);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            myAudioManager.setMicrophoneMute(false);
                        }
                    }



                }

        };

        thread.start();
        */
    }


    //=================================End================================

    public  static  boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        //your package /   accesibility service path/class
        //
        // final String service = "com.example.sotsys_014.accessibilityexample/com.accessibilityexample.Service.MyAccessibilityService";

        final String service = "nisarg.app.demo/nisarg.app.demo.MyService";


        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(LOG_TAG_S, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(LOG_TAG_S, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(LOG_TAG_S, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(LOG_TAG_S, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(LOG_TAG_S, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(LOG_TAG_S, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }


    public class MyRunnable implements Runnable {
        short [] tempBuffer;
        AudioRecord ar;
        int minSize;
        public MyRunnable(short[] tempBuffer, AudioRecord ar, int minSize) {
            // store parameter for later user
            this.tempBuffer = tempBuffer;
            this.ar = ar;
            this.minSize = minSize;
        }

        public void run() {
            //Log.d("result", String.valueOf(result));
            while (ar.read(tempBuffer, 0, minSize) != -1) {
                int result = calculate(8000, tempBuffer);

                //MyRunnable runnable = new MyRunnable(buffer);
                //runnable.run();
                if (result > 300) {
                    myAudioManager.setMicrophoneMute(true);

                }

            }


        }
    }
}
