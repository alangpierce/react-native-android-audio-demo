package com.wikipediareader;

import com.facebook.react.bridge.JavaScriptModule;

public interface ServiceTopLevel extends JavaScriptModule {
    void runService(int foo);
    void pause(int foo);
}
