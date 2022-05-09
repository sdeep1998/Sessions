package com.sasken.sessions.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sasken.sessions.R;
import com.sasken.sessions.services.BoundService;
import com.sasken.sessions.services.ForegroundService;
import com.sasken.sessions.services.StartService;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServiceExampleActivity extends Activity implements View.OnClickListener {


    private final String TAG="ServiceExampleActivity.class";
    private boolean isServiceRunning = false;
    private Button btnServicesStart, btnServicesStop;
    private TextView txtTicker;

    private boolean isBounded = false;
    private Button btnBoundStart, btnBoundStop;
//    private BoundService mService;

    private Button btnForegroundStart,btnForegroundStop;

    private Messenger mMessenger;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG,"onServiceConnected()");
            isBounded = true;
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"onServiceDisconnected");
            isBounded = false;
        }

    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ticker_count = intent.getStringExtra("ticker_count");
            if(!ticker_count.isEmpty())
                txtTicker.setText(ticker_count);
        }
    };
    private IntentFilter mIntentFilter = new IntentFilter();

    private void initViews() {
        mIntentFilter.addAction("com.sasken.sessions.action.TICKER_EVENT");
        btnServicesStart = findViewById(R.id.btn_services_start);
        btnServicesStop = findViewById(R.id.btn_services_stop);
        txtTicker = findViewById(R.id.txt_ticker);

        btnBoundStart = findViewById(R.id.btn_bound_start);
        btnBoundStop = findViewById(R.id.btn_bound_stop);


        btnForegroundStart = findViewById(R.id.btn_foreground_start);
        btnForegroundStop = findViewById(R.id.btn_foreground_stop);
        initListeners();
    }

    private void initListeners() {
        btnServicesStop.setOnClickListener(this);
        btnServicesStart.setOnClickListener(this);

        btnBoundStart.setOnClickListener(this);
        btnBoundStop.setOnClickListener(this);

        btnForegroundStart.setOnClickListener(this);
        btnForegroundStop.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
        if(!isBounded){
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        if(isBounded)
            unbindService(mServiceConnection);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Intent intent ;
        Message msg = new Message();
        switch (view.getId()) {
            case R.id.btn_services_start:
                intent = new Intent(this, StartService.class);
                isServiceRunning = !isServiceRunning;
                btnServicesStart.setText(isServiceRunning ? "Pause Ticker " : "Start Ticker");
                intent.putExtra("service_status", "" + (isServiceRunning ? "start" : "stop"));
                startService(intent);
                break;
            case R.id.btn_services_stop:
                intent = new Intent(this, StartService.class);
                intent.putExtra("service_status", "kill");
                startService(intent);
                break;

            case R.id.btn_bound_start:
                if(isBounded) {
                    msg.what = 1; // Start Service
                    msg.obj = "START_COUNT_DOWN"; //
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_bound_stop:
                if(isBounded) {
                    msg.what = 1; // Start Service
                    msg.obj = "STOP_COUNT_DOWN"; //
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    unbindService(mServiceConnection);
                }
                break;
            case R.id.btn_foreground_start:
                intent = new Intent(this, ForegroundService.class);
                startExampleService(intent);
                break;
            case R.id.btn_foreground_stop:
                intent = new Intent(this, ForegroundService.class);
                startExampleService(intent);
                break;
        }
    }

    private void startExampleService(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, intent);
        } else {
            startService(intent);
        }
    }

}