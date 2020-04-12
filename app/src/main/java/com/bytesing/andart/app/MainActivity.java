package com.bytesing.andart.app;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import com.bytesing.andart.aaipc.activity.MessengerActivity;
import com.bytesing.andart.aaipc.aidl.activity.BookManagerActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends MessengerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendMsgToService("this is a msg from client No."+ SystemClock.elapsedRealtime());
                BookManagerActivity.start(MainActivity.this);
            }
        });

    }

}
