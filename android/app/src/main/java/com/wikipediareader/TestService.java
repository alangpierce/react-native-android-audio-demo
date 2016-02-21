package com.wikipediareader;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class TestService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1;

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
            System.out.println("Running JS service code");
            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            ServiceTopLevel serviceTopLevel = catalystInstance.getJSModule(ServiceTopLevel.class);
            serviceTopLevel.runService(0);
        } else {
            System.out.println("Couldn't run JS service code");
        }

//        Icon pauseIcon = Icon.createWithResource(this, android.R.drawable.ic_media_pause);
//        PendingIntent pauseIntent = null;
//        Notification.Action pauseAction =
//                new Notification.Action.Builder(pauseIcon, "Pause", pauseIntent).build();
//
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("Playing article")
//                .setSmallIcon(android.R.drawable.ic_menu_search)
//                .addAction(pauseAction)
//                .build();
//        startForeground(ONGOING_NOTIFICATION_ID, notification);
//
//        final TextToSpeech[] textToSpeech = new TextToSpeech[1];
//        textToSpeech[0] = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                textToSpeech[0].setLanguage(Locale.US);
//
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < 100; i++) {
//                    textToSpeech[0].speak("" + i + ". ", TextToSpeech.QUEUE_ADD, null, "testutterance" + i);
//                }
//            }
//        });
    }
}
