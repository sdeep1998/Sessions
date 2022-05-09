package com.sasken.sessions.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BoundService extends Service {

    private final String TAG = "BoundService.class";
    private final IBinder mBinder = new ServiceBinder();
    private final int START_COUNTDOWN = 1;
    private final int STOP_COUNTDOWN = 2;
    private int mCountDown = 0;
    private CountDownTimer mTicker = new CountDownTimer(120000, 1000) {
        @Override
        public void onTick(long l) {
            mCountDown += 1;
            Intent intent = new Intent();
            intent.setAction("com.sasken.sessions.action.TICKER_EVENT");
            intent.putExtra("ticker_count", "" + mCountDown);
            LocalBroadcastManager.getInstance(BoundService.this).sendBroadcast(intent);
        }

        @Override
        public void onFinish() {
            mCountDown = 0;
            Intent intent = new Intent();
            intent.setAction("com.sasken.sessions.action.TICKER_EVENT");
            intent.putExtra("ticker_count", "");
            LocalBroadcastManager.getInstance(BoundService.this).sendBroadcast(intent);
        }
    };
    private Messenger mMessenger = new Messenger(new RequestHandler());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() -- >");
        return super.onStartCommand(intent, flags, startId);
    }

    public int getCountDown() {
        return mCountDown;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() -- >");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() -- >");
        mTicker.cancel();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind() -- >");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() -- >");
        super.onDestroy();
    }

    private class RequestHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_COUNTDOWN:
                    if (mTicker != null) {
                        mTicker.start();
                        Toast.makeText(BoundService.this, "" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case STOP_COUNTDOWN:
                    if (mTicker != null) {
                        mTicker.cancel();
                        Toast.makeText(BoundService.this, "" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public class ServiceBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }
}
