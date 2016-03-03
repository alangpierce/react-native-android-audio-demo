package com.alangpierce.audiodemo;

import com.alangpierce.reactremoteviews.RemoteViewCallbackStrategy;
import com.alangpierce.reactremoteviews.RemoteViewNode;
import com.alangpierce.reactremoteviews.RemoteViewNodeParser;
import com.alangpierce.reactremoteviews.RemoteViewRenderer;
import com.alangpierce.reactremoteviews.RemoteViewsModule;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

public class AudioService extends Service {
    private static final String TAG = "AudioService";

    private static final int ONGOING_NOTIFICATION_ID = 1;

    // We put the callback ID into the intent UI, since the system only wants one outstanding
    // PendingIntent per URI. If we try to specify the callback ID through an extra, different
    // PendingIntents will clobber each other.
    private static final String RUN_CALLBACK_URI_PREFIX = "com.alangpierce.runcallback.";
    public static final String REDRAW_NOTIFICATION_URI = "com.alangpierce.audiodemo.redraw";

    private ServiceTopLevel serviceTopLevel;
    private RemoteViewsModule mRemoteViewsModule;

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
            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            serviceTopLevel = catalystInstance.getJSModule(ServiceTopLevel.class);
            mRemoteViewsModule = catalystInstance.getJSModule(RemoteViewsModule.class);
        } else {
            Log.e(TAG, "Couldn't grab JS service code");
        }

        serviceTopLevel.initService(0);
    }

    private Notification createNotification(JsonObject notificationObject) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        // TODO: Figure out how to make it just dismiss the notification screen if the activity is
        // already active. This flag doesn't seem to be working like I thought it would.
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mainActivityPendingIntent =
                PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        RemoteViewNodeParser parser = new RemoteViewNodeParser(new RemoteViewCallbackStrategy() {
            @Override
            public PendingIntent createPendingIntent(String callbackId) {
                Intent actionIntent = new Intent(AudioService.this, AudioService.class);
                actionIntent.setAction(RUN_CALLBACK_URI_PREFIX + callbackId);
                return PendingIntent.getService(AudioService.this, 0, actionIntent, 0);
            }
        });

        RemoteViewNode node = parser.parseRemoteViewNode(
                notificationObject.getAsJsonObject("customView")
        );

        RemoteViews notificationView = new RemoteViewRenderer(getPackageName()).renderNode(node);

        return new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .setContentText("")
                .setContent(notificationView)
                .setContentIntent(mainActivityPendingIntent)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().startsWith(RUN_CALLBACK_URI_PREFIX)) {
                String callbackId = intent.getAction().substring(RUN_CALLBACK_URI_PREFIX.length());
                mRemoteViewsModule.runCallback(callbackId);
            } else if (intent.getAction().equals(REDRAW_NOTIFICATION_URI)) {
                String notificationJson = intent.getStringExtra("notification");
                JsonObject notificationObject = new JsonParser().parse(notificationJson).getAsJsonObject();
                startForeground(ONGOING_NOTIFICATION_ID, createNotification(notificationObject));
                if (!notificationObject.get("isForeground").getAsBoolean()) {
                    // In a paused state, we don't want the notification to be ongoing. Clearing the
                    // foreground state does that.
                    stopForeground(false /* removeNotification */);
                }
            }
        }
        return START_STICKY;
    }
}

