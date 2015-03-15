package com.crystalnebula.talkingnotifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * foo bar baz make this yellow go away
 *
 * Created by scbash on 3/14/15.
 */
public class TNService extends NotificationListenerService
{
    private static final String LOG_TAG = "TalkingService";

    private final int LONG_DURATION = 1200;
    private final int SHORT_DURATION = 500;

    private Speaker speaker;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            // TODO should handle notifications that exist at startup...
            // TODO ... and should ignore itself on startup!

            Log.i(LOG_TAG, "Received start foreground action, this is " + this);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent muteIntent = new Intent(this, TNService.class);
            muteIntent.setAction(Constants.ACTION.MUTE_ACTION);
            PendingIntent pmuteIntent = PendingIntent.getService(this, 0, muteIntent, 0);

            Intent unmuteIntent = new Intent(this, TNService.class);
            unmuteIntent.setAction(Constants.ACTION.UNMUTE_ACTION);
            PendingIntent punmuteIntent = PendingIntent.getService(this, 0, unmuteIntent, 0);

            Intent closeIntent = new Intent(this, TNService.class);
            closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Talking Notifications")
                .setTicker("Talking Notifications")
                .setContentText("Some text about talking")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                // TODO combine mute/unmute into a single button
                .addAction(android.R.drawable.ic_lock_silent_mode, "", pmuteIntent)
                .addAction(android.R.drawable.ic_lock_silent_mode_off, "", punmuteIntent)
                .addAction(android.R.drawable.ic_delete, "", pcloseIntent)
                .build();

            speaker = new Speaker(this);
            speaker.allow(true);

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
        else if (intent.getAction().equals(Constants.ACTION.MUTE_ACTION))
        {
            Log.i(LOG_TAG, "Mute pressed, disallowing speech");
            speaker.allow(false);
        }
        else if (intent.getAction().equals(Constants.ACTION.UNMUTE_ACTION))
        {
            Log.i(LOG_TAG, "Speak pressed, allowing speech");
            speaker.allow(true);
        }
        else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION))
        {
            Log.i(LOG_TAG, "Received stop foreground intent");

            // Removes the foreground service, but since the notification listener is bound by
            // the system, the service will typically continue to run
            stopForeground(true);
            stopSelf();

            // Since the notification system may keep this service alive, silence speech
            speaker.allow(false);
        }

        return START_STICKY;
    }

    @TargetApi(19)  // don't complain about the use of Notification.extras
    private void log_helper(Notification n, String key)
    {
        CharSequence foo = n.extras.getCharSequence(key);
        if (foo != null)
            Log.i(LOG_TAG, key + " is " + foo.toString());
        else
            Log.i(LOG_TAG, key + " is (null)");
    }

    @TargetApi(21)  // don't complain about the use of Notification.extras or EXTRA_BIG_TEXT
    private void readNotification(StatusBarNotification sbn)
    {
        Notification n = sbn.getNotification();
        CharSequence text = n.tickerText;

        Log.i(LOG_TAG, "about to speak " + (text != null ? text.toString() : "(null)"));
        log_helper(n, Notification.EXTRA_TITLE);

        String src_name = "unknown";
        try
        {
            PackageManager pm = getPackageManager();
            ApplicationInfo src = pm.getApplicationInfo(sbn.getPackageName(),
                                                        PackageManager.GET_META_DATA);
            src_name = pm.getApplicationLabel(src).toString();
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "notification is from " + src_name + " (" + sbn.getPackageName() + ")");

        log_helper(n, Notification.EXTRA_SUMMARY_TEXT);
        log_helper(n, Notification.EXTRA_TEXT);
        log_helper(n, Notification.EXTRA_TITLE_BIG);
        log_helper(n, Notification.EXTRA_BIG_TEXT);

        if (speaker != null && speaker.isReady())
        {
            speaker.pause(LONG_DURATION);
            speaker.speak("You have a new notification");
            speaker.pause(SHORT_DURATION);
            speaker.speak(text.toString());
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        Log.i(LOG_TAG, "New 18 notification: " + sbn.getNotification().tickerText);
        if (Build.VERSION.SDK_INT < 21)
        {
            readNotification(sbn);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap)
    {
        Log.i(LOG_TAG, "New 21 notification: " + sbn.getNotification().tickerText);
        if (Build.VERSION.SDK_INT >= 21 )
        {
            NotificationListenerService.Ranking r = new NotificationListenerService.Ranking();
            rankingMap.getRanking(sbn.getKey(), r);
            if (r.matchesInterruptionFilter() && !r.isAmbient())
                readNotification(sbn);
        }

        super.onNotificationPosted(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        Log.i(LOG_TAG, "Notification removed 18");
        if (Build.VERSION.SDK_INT < 21)
        {
            super.onNotificationRemoved(sbn);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap)
    {
        Log.i(LOG_TAG, "Notification removed 21");
        if (Build.VERSION.SDK_INT >= 21)
        {
            super.onNotificationRemoved(sbn, rankingMap);
        }
    }

    @Override
    public void onDestroy()
    {
        Log.i(LOG_TAG, "In onDestroy");
        super.onDestroy();
        speaker.destroy();
    }
}
