package com.alangpierce.reactremoteviews;

import com.facebook.react.bridge.JavaScriptModule;

public interface RemoteViewsModule extends JavaScriptModule {
    void runCallback(String callbackId);
}
