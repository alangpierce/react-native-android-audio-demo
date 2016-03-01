package com.alangpierce.reactremoteviews;

import android.app.PendingIntent;

/**
 * Function for hooking up callbacks when working with RemoteViews. All onPress events are handled
 * by submitting a PendingIntent.
 */
public interface RemoteViewCallbackStrategy {
    PendingIntent createPendingIntent(String callbackId);
}
