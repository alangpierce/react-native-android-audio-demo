package com.wikipediareader;

import com.alangpierce.reactremoteviews.RemoteViewNode;
import com.alangpierce.reactremoteviews.RemoteViewProperty;
import com.alangpierce.reactremoteviews.RemoteViewRenderer;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.RemoteViews;

import java.util.Arrays;

public class TestService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1;

    private static final String PAUSE_ACTION_URI = "com.alangpierce.wikipediareader.pause";
    private static final String PLAY_ACTION_URI = "com.alangpierce.wikipediareader.play";
    public static final String REDRAW_NOTIFICATION_URI = "com.alangpierce.wikipediareader.redraw";

    private ServiceTopLevel serviceTopLevel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
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

        // The service always starts by starting audio, so true is a good default for isPlaying.
        startForeground(ONGOING_NOTIFICATION_ID, createNotification(true));

        serviceTopLevel.initService(0);
    }

    private Notification createNotification(boolean isPlaying) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        // TODO: Figure out how to make it just dismiss the notification screen if the activity is
        // already active. This flag doesn't seem to be working like I thought it would.
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mainActivityPendingIntent =
                PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        int buttonImage;
        String actionUri;
        if (isPlaying) {
            buttonImage = android.R.drawable.ic_media_pause;
            actionUri = PAUSE_ACTION_URI;
        } else {
            buttonImage = android.R.drawable.ic_media_play;
            actionUri = PLAY_ACTION_URI;
        }

        Intent actionIntent = new Intent(this, TestService.class);
        actionIntent.setAction(actionUri);
        PendingIntent actionPendingIntent = PendingIntent.getService(this, 0, actionIntent, 0);

//        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.linear_layout);
//        notificationView.removeAllViews(R.id.linear_layout);
//
//        notificationView.setInt(R.id.linear_layout, "setBackgroundColor", Color.parseColor("#000000"));
//        notificationView.setInt(R.id.linear_layout, "setGravity", Gravity.CENTER_HORIZONTAL);
//
//        RemoteViews textView = new RemoteViews(getPackageName(), R.layout.text_view);
//        notificationView.addView(R.id.linear_layout, textView);
//
//        RemoteViews button = new RemoteViews(getPackageName(), R.layout.image_button);
//        notificationView.addView(R.id.linear_layout, button);
//
//        notificationView.setImageViewResource(R.id.play_pause_button, buttonImage);
//        notificationView.setOnClickPendingIntent(R.id.play_pause_button, actionPendingIntent);

        RemoteViews notificationView =
                new RemoteViewRenderer(getPackageName()).renderNode(renderNode());

        return new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .setContentText("")
                .setContent(notificationView)
                .setContentIntent(mainActivityPendingIntent)
                .build();
    }

    private RemoteViewNode renderNode() {
        return new RemoteViewNode(
                "LinearLayout",
                Arrays.asList(
                        new RemoteViewProperty("setBackgroundColor", RemoteViewProperty.PropertyType.INT, Color.parseColor("#000000")),
                        new RemoteViewProperty("setGravity", RemoteViewProperty.PropertyType.INT, Gravity.CENTER_HORIZONTAL)
                ),
                Arrays.asList(
                        new RemoteViewNode(
                                "TextView",
                                Arrays.<RemoteViewProperty>asList(),
                                Arrays.<RemoteViewNode>asList()
                        ),
                        new RemoteViewNode(
                                "ImageButton",
                                Arrays.<RemoteViewProperty>asList(),
                                Arrays.<RemoteViewNode>asList()
                        )
                )
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(PAUSE_ACTION_URI)) {
                serviceTopLevel.pause(0);
            } else if (intent.getAction().equals(PLAY_ACTION_URI)) {
                serviceTopLevel.play(0);
            } else if (intent.getAction().equals(REDRAW_NOTIFICATION_URI)) {
                boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
                startForeground(ONGOING_NOTIFICATION_ID, createNotification(isPlaying));
                if (!isPlaying) {
                    // In a paused state, we don't want the notification to be ongoing. Clearing the
                    // foreground state does that.
                    stopForeground(false /* removeNotification */);
                }
            }
        }
        return START_STICKY;
    }
}
