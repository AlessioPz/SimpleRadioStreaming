package com.bussolalabs.simpleradiostreaming;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alessio on 21/02/17.
 */

public class StreamService extends Service {

    public static MediaPlayer stream;
    public static String url;
    TelephonyManager tm;
    public static PowerManager.WakeLock wl;
    public static int serviceAvailable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StreamService", "onStartCommand");
        stream = new MediaPlayer();

        try {
            stream.setDataSource(url);
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            streamErrorHandler();
        } catch (IllegalStateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            streamErrorHandler();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            streamErrorHandler();
        }

        stream.setAudioStreamType(AudioManager.STREAM_MUSIC);

        stream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer stream) {

                tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

                // Set the Wake Lock - CPU on, keyboard and screen off
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                        getString(R.string.app_name));
                wl.acquire();
                serviceAvailable = 1;
                stream.start();

            }
        });

        try {
            stream.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // kill the stream
        serviceAvailable = 0;
        stream.stop();
        stream.release();
        stream = null;

        if (wl.isHeld()) {
            // kill the wake lock
            wl.release();
            wl = null;
        }
    }

    public void streamErrorHandler() {

        Toast t = Toast.makeText(this, "network_error",
                Toast.LENGTH_LONG);
        t.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
        t.show();

        serviceAvailable = 0;
        stream.stop();
        stream.release();
        stream = null;
    }

    private PhoneStateListener mPhoneListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                Log.d("PhoneStateListener", "state: " + state);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        stopService(MainActivity.svc);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    default:
                }
            } catch (Exception e) {
            }
        }
    };

}
