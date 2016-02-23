package com.wikipediareader;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class TestService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1;

    private static final String PAUSE_ACTION_URI = "com.alangpierce.wikipediareader.pause";
    private static final String PLAY_ACTION_URI = "com.alangpierce.wikipediareader.play";

    private ServiceTopLevel serviceTopLevel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Created the test service");

        ReactInstanceManager reactInstanceManager =
                ReactInstanceSingleton.getReactInstanceManager(getApplication());

        ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
        if (reactContext != null) {
            System.out.println("Grabbing JS service code");
            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            serviceTopLevel = catalystInstance.getJSModule(ServiceTopLevel.class);
        } else {
            System.out.println("Couldn't grab JS service code");
        }

        startForeground(ONGOING_NOTIFICATION_ID, createNotification());
    }

    private Notification createNotification() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        // TODO: Figure out how to make it just dismiss the notification screen if the activity is
        // already active. This flag doesn't seem to be working like I thought it would.
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mainActivityPendingIntent =
                PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        Intent pauseIntent = new Intent(this, TestService.class);
        pauseIntent.setAction(PAUSE_ACTION_URI);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Icon pauseIcon = Icon.createWithResource(this, android.R.drawable.ic_media_pause);
        Notification.Action pauseAction =
                new Notification.Action.Builder(pauseIcon, "Pause", pausePendingIntent).build();


        Intent playIntent = new Intent(this, TestService.class);
        playIntent.setAction(PLAY_ACTION_URI);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Icon playIcon = Icon.createWithResource(this, android.R.drawable.ic_media_play);
        Notification.Action playAction =
                new Notification.Action.Builder(playIcon, "Play", playPendingIntent).build();

        return new Notification.Builder(this)
                .setContentTitle("Playing article")
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .setContentIntent(mainActivityPendingIntent)
                .addAction(pauseAction)
                .addAction(playAction)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(PAUSE_ACTION_URI)) {
                serviceTopLevel.pause(0);
            } else if (intent.getAction().equals(PLAY_ACTION_URI)) {
                serviceTopLevel.play(0);
            }
        }
        return START_STICKY;
    }
}
