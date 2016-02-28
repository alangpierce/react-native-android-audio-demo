package com.audiodemo;

import com.alangpierce.reactremoteviews.RemoteViewsPackage;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.mihir.react.tts.RCTTextToSpeechModule;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

/**
 * Static storage of the React instance, used to share a JS heap across
 */
public class ReactInstanceSingleton {
    private static volatile ReactInstanceManager sSingletonManager;

    public static synchronized ReactInstanceManager getReactInstanceManager(
            Application application) {
        if (sSingletonManager == null) {
            ReactInstanceManager.Builder builder = ReactInstanceManager.builder()
                    .setApplication(application)
                    .setJSMainModuleName("index.android")
                    .setUseDeveloperSupport(BuildConfig.DEBUG)
                            // TODO figure out the right thing to put here.
                    .setInitialLifecycleState(LifecycleState.BEFORE_RESUME);

            for (ReactPackage reactPackage : getPackages()) {
                builder.addPackage(reactPackage);
            }

            builder.setBundleAssetName("index.android.bundle");
            sSingletonManager = builder.build();
        }
        return sSingletonManager;
    }

    private static List<ReactPackage> getPackages() {
        return Arrays.asList(
                new MainReactPackage(),
                new StartServicePackage(),
                new RemoteViewsPackage(),
                new RCTTextToSpeechModule()
        );
    }
}
