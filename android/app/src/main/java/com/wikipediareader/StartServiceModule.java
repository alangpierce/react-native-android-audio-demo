package com.wikipediareader;

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
        System.out.println("Hello from native module!");

        Intent serviceIntent = new Intent(mReactApplicationContext, TestService.class);
        mReactApplicationContext.startService(serviceIntent);
    }
}
