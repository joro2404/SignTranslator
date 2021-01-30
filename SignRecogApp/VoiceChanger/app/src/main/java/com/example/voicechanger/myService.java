package com.example.voicechanger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static android.app.Service.START_STICKY;

public class myService extends Service {
    AudioManager myAudioManager;
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;
    ContinuousBuzzer tonePlayer;
    ContinuousBuzzer tonePlayer2;
    ContinuousBuzzer tonePlayer3;
    ContinuousBuzzer tonePlayer4;

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_CHANNELS_INT = 1;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    Thread thread;
    Thread threadSound;
    Thread threadSound2;
    Thread threadSound3;
    Thread threadSound4;
    Thread threadTest;
    int stopThread = 0;
    int stopThreadSound = 0;


    @Override
    public void onCreate() {
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        super.onCreate();
    }

    public double calculate(byte[] buffer) {

        int bufferSizeInBytes = 1024;
        double[] magnitude = new double[bufferSizeInBytes / 2];

        //Create Complex array for use in FFT
        Complex[] fftTempArray = new Complex[bufferSizeInBytes];
        for (int i = 0; i < bufferSizeInBytes; i++) {
            fftTempArray[i] = new Complex(buffer[i], 0);
        }

        //Obtain array of FFT data
        final Complex[] fftArray = FFT.fft(fftTempArray);
        // calculate power spectrum (magnitude) values from fft[]
        for (int i = 0; i < (bufferSizeInBytes / 2) - 1; ++i) {

            double real = fftArray[i].re();
            double imaginary = fftArray[i].im();
            magnitude[i] = Math.sqrt(real * real + imaginary * imaginary);

        }

        // find largest peak in power spectrum
        double max_magnitude = magnitude[0];
        int max_index = 0;
        for (int i = 0; i < magnitude.length; ++i) {
            if (magnitude[i] > max_magnitude) {
                max_magnitude = (int) magnitude[i];
                max_index = i;
            }
        }
        double freq = 44100 * max_index / bufferSizeInBytes;//here will get frequency in hz like(17000,18000..etc)
        return freq;
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

        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;

        return (int)frequency;
    }

    int stopSpam = 0;

    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    myAudioManager.setMode(3);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    myAudioManager.setMode(3);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    myAudioManager.setMode(0);
                    stopSpam = 1;
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("service", "on");
        Thread thread1 = new Thread(){
            @Override
            public void run() {
                super.run();
                while(stopThread == 0) {
                    if(stopSpam != 1) {
                        myAudioManager.setMode(3);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //thread1.start();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        createNotificationChannel();
        builder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("notification")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText("voice changer activated")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

        final int minSize = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        final AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000,AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
        ar.startRecording();

        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                while(stopThread == 0){
                    Log.d("mode", String.valueOf(myAudioManager.getMode()));

                    for(int i = 0; i < 20; i++) {
                        Log.d("mode", String.valueOf(myAudioManager.getMode()));
                        myAudioManager.setMicrophoneMute(true);
                        myAudioManager.setMicrophoneMute(false);
                        myAudioManager.setMicrophoneMute(true);
                    }
                }
            }
        };
        thread2.start();
/*
        thread = new Thread(){
            public void run(){
                    short[] buffer = new short[minSize];

                    ar.startRecording();
                    while(ar.read(buffer, 0, minSize) != -1)
                    {
                        int result = calculate(8000, buffer);
                        Log.d("result", String.valueOf(result));

                        if(result > 50){
                            myAudioManager.setMicrophoneMute(true);


                        }else{
                            myAudioManager.setMicrophoneMute(false);
                        }
                        /*int i = 0;
                        int max = 0;
                        int min = 0;
                        int grow = 1;
                        int prev = 0;
                        for (short s : buffer)
                        {



                            if(i%1 == 0) {
                                Log.d("result", String.valueOf(Math.abs(s)));
                            }
                            i++;
                        }


                    }
            }
        };

        thread.start();
*/
        /*
        thread = new Thread()
        {
            public void run() {
                int muted = 40;
                int unmuted = 16;
                while(stopThread == 0) {
                    myAudioManager.setMicrophoneMute(true);
                    //myAudioManager.playSoundEffect();
                        //Log.v("service", "mute");
                        try {
                            Thread.sleep(muted,0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    myAudioManager.setMicrophoneMute(false);
                    //myAudioManager.setMicrophoneMute(false);
                        //Log.v("service", "unmute");
                        try {
                            Thread.sleep(unmuted, 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    //int temp = muted;
                    //muted = unmuted;
                    //unmuted = temp;
                }

            }
        };
        thread.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadTest = new Thread()
        {
            public void run() {
                int muted = 40;
                int unmuted = 16;
                while(stopThread == 0) {
                    myAudioManager.setMicrophoneMute(true);
                    //myAudioManager.playSoundEffect();
                    //Log.v("service", "mute");
                    try {
                        Thread.sleep(muted,0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myAudioManager.setMicrophoneMute(false);
                    //myAudioManager.setMicrophoneMute(false);
                    //Log.v("service", "unmute");
                    try {
                        Thread.sleep(unmuted, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //int temp = muted;
                    //muted = unmuted;
                    //unmuted = temp;
                }

            }
        };
        threadTest.start();


        threadSound = new Thread()
        {
            public void run() {
                tonePlayer = new ContinuousBuzzer();
                tonePlayer.setToneFreqInHz(300);
                tonePlayer.setVolume(60);

                while(stopThreadSound == 0) {
                    myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    myAudioManager.setSpeakerphoneOn(true);
                    tonePlayer.play();
                }
            }
        };
        threadSound.start();

        threadSound2 = new Thread()
        {
            public void run() {
                tonePlayer2 = new ContinuousBuzzer();
                tonePlayer2.setToneFreqInHz(250);
                tonePlayer2.setVolume(100);

                while(stopThreadSound == 0) {
                    myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    myAudioManager.setSpeakerphoneOn(true);
                    tonePlayer2.play();
                }
            }
        };
        threadSound2.start();

        threadSound3 = new Thread()
        {
            public void run() {
                tonePlayer3 = new ContinuousBuzzer();
                tonePlayer3.setToneFreqInHz(350);
                tonePlayer3.setVolume(100);

                while(stopThreadSound == 0) {
                    myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    myAudioManager.setSpeakerphoneOn(true);
                    tonePlayer3.play();
                }
            }
        };
        threadSound3.start();

        threadSound4 = new Thread()
        {
            public void run() {
                tonePlayer4 = new ContinuousBuzzer();
                tonePlayer4.setToneFreqInHz(150);
                tonePlayer4.setVolume(100);

                while(stopThreadSound == 0) {
                    myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    myAudioManager.setSpeakerphoneOn(true);
                    tonePlayer4.play();
                }
            }
        };
        threadSound4.start();
*/
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        //myAudioRecorder.stop();
        AudioManager myAudioManager;
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.setMicrophoneMute(false);
        //tonePlayer.stop();
        myAudioManager.setMode(0);

        stopThread = 1;
        thread.interrupt();
        notificationManager.cancel(1);
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            String description = "notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
