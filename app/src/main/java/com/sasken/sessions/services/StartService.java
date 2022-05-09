package com.sasken.sessions.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StartService extends Service {

    private final String TAG = "StartService.class";
    private byte mCountDown = 0;
    private String mCmd = "";
    private Intent mIntent = new Intent();

    private CountDownTimer mTicker = new CountDownTimer(120000, 1000) {
        @Override
        public void onTick(long l) {
            mCountDown += 1;
            Log.d(TAG, "onTick --> " + mCountDown);
            Toast.makeText(StartService.this, "CountDown : " + mCountDown, Toast.LENGTH_SHORT).show();
            mIntent.setAction("com.sasken.sessions.action.TICKER_EVENT");
            mIntent.putExtra("ticker_count", "" + mCountDown);
            LocalBroadcastManager.getInstance(StartService.this).sendBroadcast(mIntent);
        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mCmd = "";
        Log.d(TAG, "onCreate() --> ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() --> ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() ");
        mCmd = intent.getStringExtra("service_status");
        switch (mCmd) {
            case "start":
                mTicker.start();
                break;
            case "stop":
                mTicker.cancel();
                break;
            case "kill":
                stopForeground(false);
//                stopSelf();
                break;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() ");
        return super.onUnbind(intent);
    }
}