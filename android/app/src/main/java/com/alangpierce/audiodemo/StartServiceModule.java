package com.alangpierce.audiodemo;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.content.Intent;

public class StartServiceModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext mReactApplicationContext;

    public StartServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactApplicationContext = reactContext;
    }

    @Override
    public String getName() {
        return "StartService";
    }

    @ReactMethod
    public void startService() {
        Intent serviceIntent = new Intent(mReactApplicationContext, AudioService.class);
        mReactApplicationContext.startService(serviceIntent);
    }

    @ReactMethod
    public void invalidateNotification(String notificationJson) {
        Intent redrawIntent = new Intent(mReactApplicationContext, AudioService.class);
        redrawIntent.setAction(AudioService.REDRAW_NOTIFICATION_URI);
        // We use plain old JSON here because ReadableMap is a pain to put in an intent, it seems.
        // Ideally we would be able to just use ReadableMap without any serialization.
        redrawIntent.putExtra("notification", notificationJson);
        mReactApplicationContext.startService(redrawIntent);
    }
}
