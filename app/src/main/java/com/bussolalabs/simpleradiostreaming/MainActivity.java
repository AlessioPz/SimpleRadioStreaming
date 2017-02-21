package com.bussolalabs.simpleradiostreaming;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static Intent svc;
    public static MediaPlayer stream;
    public static int serviceAvailable;
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView textView = (TextView) findViewById(R.id.type_url);
        StreamService.url = textView.getText().toString();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StreamService.url = textView.getText().toString();
                stream = StreamService.stream;
                Log.d("MainActivity", "stream: " + stream);
                if (stream != null) {
                    if (stream.isPlaying() == true) {
                        stopService(svc);
                    } else {
                        startService(svc);
                    }
                } else {
                    startService(svc);
                }
            }
        });

        stream = StreamService.stream;
        svc = new Intent(this, StreamService.class);

        //mHandler.postDelayed(initializeStream, 2 * 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final Runnable initializeStream = new Runnable() {
        public void run() {

            serviceAvailable = StreamService.serviceAvailable;
            if (serviceAvailable == 1) {
            } else {
                startService(svc);
            }
        }
    };
}
