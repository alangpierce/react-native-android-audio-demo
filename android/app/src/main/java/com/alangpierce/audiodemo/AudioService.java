package com.alangpierce.audiodemo;

import com.alangpierce.reactremoteviews.RemoteViewNode;
import com.alangpierce.reactremoteviews.RemoteViewProperty;
import com.alangpierce.reactremoteviews.RemoteViewRenderer;
import com.alangpierce.reactremoteviews.RemoteViewsModule;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // The service always starts by starting audio, so true is a good default for isPlaying.
        // TODO: Uncomment this, or find another way to make sure the notification always shows up.
//        startForeground(ONGOING_NOTIFICATION_ID, createNotification(true));

        serviceTopLevel.initService(0);
    }

    private Notification createNotification(JsonObject notificationObject) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        // TODO: Figure out how to make it just dismiss the notification screen if the activity is
        // already active. This flag doesn't seem to be working like I thought it would.
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mainActivityPendingIntent =
                PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        RemoteViewNode node = parseRemoteViewNode(notificationObject.getAsJsonObject("customView"));

        RemoteViews notificationView = new RemoteViewRenderer(getPackageName()).renderNode(node);

        return new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .setContentText("")
                .setContent(notificationView)
                .setContentIntent(mainActivityPendingIntent)
                .build();
    }

    private RemoteViewNode parseRemoteViewNode(JsonObject viewElement) {
        List<RemoteViewProperty> props = new ArrayList<>();
        List<RemoteViewNode> children = new ArrayList<>();
        PendingIntent onPress = null;

        JsonObject propsObject = viewElement.getAsJsonObject("props");
        for (Map.Entry<String, JsonElement> entry : propsObject.entrySet()) {
            if (entry.getKey().equals("children")) {
                for (JsonElement childElement : entry.getValue().getAsJsonArray()) {
                    children.add(parseRemoteViewNode(childElement.getAsJsonObject()));
                }
            } else if (entry.getKey().equals("onPress")) {
                Intent actionIntent = new Intent(this, AudioService.class);
                actionIntent.setAction(RUN_CALLBACK_URI_PREFIX + entry.getValue().getAsString());
                onPress = PendingIntent.getService(this, 0, actionIntent, 0);
            } else {
                props.add(parseProperty(entry));
            }
        }

        return new RemoteViewNode(
                viewElement.get("type").getAsString(),
                props,
                children,
                onPress);
    }

    private RemoteViewProperty parseProperty(Map.Entry<String, JsonElement> entry) {
        JsonElement value = entry.getValue();
        switch (entry.getKey()) {
            case "backgroundColor":
                return new RemoteViewProperty("setBackgroundColor", RemoteViewProperty.PropertyType.INT, Color.parseColor(value.getAsString()));
            case "gravity":
                // TODO: Parse the value
                return new RemoteViewProperty("setGravity", RemoteViewProperty.PropertyType.INT, Gravity.CENTER);
            case "text":
                return new RemoteViewProperty("setText", RemoteViewProperty.PropertyType.CHAR_SEQUENCE, value.getAsString());
            case "textSize":
                return new RemoteViewProperty("setTextSize", RemoteViewProperty.PropertyType.FLOAT, value.getAsFloat());
            case "textColor":
                return new RemoteViewProperty("setTextColor", RemoteViewProperty.PropertyType.INT, Color.parseColor(value.getAsString()));
            case "imageResource":
                return new RemoteViewProperty("setImageResource", RemoteViewProperty.PropertyType.INT, value.getAsInt());
            default:
                throw new RuntimeException("Unexpected property name " + entry.getKey());
        }
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
