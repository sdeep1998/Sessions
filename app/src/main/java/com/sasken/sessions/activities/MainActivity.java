package com.sasken.sessions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sasken.sessions.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnServices;

    private void initViews() {
        btnServices = findViewById(R.id.btn_services);
        initListeners();
    }

    private void initListeners() {
        btnServices.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_services:
                startActivity(new Intent(this, ServiceExampleActivity.class));
                finish();
                break;
        }
    }
}