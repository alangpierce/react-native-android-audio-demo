package com.wikipediareader;

import com.facebook.react.bridge.JavaScriptModule;

public interface ServiceTopLevel extends JavaScriptModule {
    void initService(int foo);
    void runCallback(String callbackId);
}
