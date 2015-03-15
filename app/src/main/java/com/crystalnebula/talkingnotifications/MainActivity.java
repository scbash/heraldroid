package com.crystalnebula.talkingnotifications;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private static final String LOG_TAG = "SCBMainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button)findViewById(R.id.button1);
        Button stopButton = (Button)findViewById(R.id.button2);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button1:
                Log.i(LOG_TAG, "Start button pressed");
                Intent startIntent = new Intent(MainActivity.this, TNService.class);
                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(startIntent);
                break;
            case R.id.button2:
                Log.i(LOG_TAG, "Stop button pressed");
                Intent stopIntent = new Intent(MainActivity.this, TNService.class);
                stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                startService(stopIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    // *** The following methods were created by Android Studio and not discussed in the tutorial

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
