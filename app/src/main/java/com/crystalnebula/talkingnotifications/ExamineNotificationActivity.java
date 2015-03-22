package com.crystalnebula.talkingnotifications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ExamineNotificationActivity extends ActionBarActivity
{
    private static final String LOG_TAG = "ExamineNotif";

    private TNService tnService;
    private boolean serviceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            TNService.LocalBinder binder = (TNService.LocalBinder) service;
            tnService = binder.getService();
            serviceBound = true;

            // TODO it feels a bit odd to do this here...
            JSONArray notifications = tnService.getNotificationList();
            ListView listView = (ListView)findViewById(R.id.notification_list);
            listView.setAdapter(new NotificationListAdapter(ExamineNotificationActivity.this, R.id.notification_list, notifications));
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_notification);

        // TODO set (or default) view to "Loading..."
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Request service to list all current notifications
        Intent intent = new Intent(this, TNService.class);
        intent.setAction(Constants.ACTION.REQUEST_LOCAL_BIND);
        Log.i(LOG_TAG, "Requesting local bind with intent " + intent.getAction());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (serviceBound)
        {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_examine_notification, menu);
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

    private class NotificationListAdapter extends ArrayAdapter
    {
        private JSONArray notifications;

        public NotificationListAdapter(Context context, int resource, JSONArray notifications)
        {
            super(context, resource);
            this.notifications = notifications;
            Log.d(LOG_TAG, "ListAdapter created");
        }

        @Override
        public int getCount()
        {
            return notifications.length();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Log.d(LOG_TAG, "getView(" + position + ") called");

            LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try
            {
                JSONObject notification = this.notifications.getJSONObject(position);
                Log.d(LOG_TAG, "creating table entry for " + notification.getString(Constants.JSON.APPNAME));

                TableLayout table;
                if (convertView == null)
                {
                    Log.v(LOG_TAG, "creating new view for table");
                    table = (TableLayout)inflator.inflate(R.layout.notification_details_table, null);
                }
                else
                {
                    Log.v(LOG_TAG, "reusing existing view for table");
                    table = (TableLayout)convertView;
                }

                View header = table.findViewById(R.id.notification_details_header);
                TextView appNameView = (TextView)header.findViewById(R.id.details_app_name);
                appNameView.setText(notification.getString(Constants.JSON.APPNAME));

                TextView packageNameView = (TextView)header.findViewById(R.id.details_package_name);
                packageNameView.setText(String.format("(%s)", notification.getString(Constants.JSON.PACKAGE)));

                Iterator<String> keys = notification.keys();
                int curRow = 1;  // start after the header
                while (keys.hasNext())
                {
                    String key = keys.next();
                    if (!notification.isNull(key))
                    {
                        TableRow row;
                        boolean new_row_p;
                        if (table.getChildCount() < curRow+1)
                        {
                            row = (TableRow) inflator.inflate(R.layout.notification_details_row, null);
                            new_row_p = true;
                        }
                        else
                        {
                            row = (TableRow)table.getChildAt(curRow);
                            new_row_p = false;
                        }

                        String value = notification.optString(key, "something weird");
                        Log.v(LOG_TAG, "creating line item for " + key);

                        TextView androidView = (TextView)row.findViewById(R.id.details_android_name);
                        androidView.setText(key);

                        TextView valueView = (TextView)row.findViewById(R.id.details_value);
                        if (value != null)
                            valueView.setText(value);
                        else
                            valueView.setText("null");

                        if (new_row_p)
                           table.addView(row);
                        curRow++;
                    }
                }

                // If reusing the table widget, remove excess rows
                while (table.getChildCount() > curRow+1)
                    table.removeViewAt(table.getChildCount()-1);

                return table;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }
}
