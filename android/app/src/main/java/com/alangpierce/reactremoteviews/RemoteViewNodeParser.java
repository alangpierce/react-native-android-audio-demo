package com.alangpierce.reactremoteviews;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.app.PendingIntent;
import android.graphics.Color;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteViewNodeParser {
    private final RemoteViewCallbackStrategy mCallbackStrategy;

    public RemoteViewNodeParser(RemoteViewCallbackStrategy callbackStrategy) {
        mCallbackStrategy = callbackStrategy;
    }

    public RemoteViewNode parseRemoteViewNode(JsonObject viewElement) {
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
                String callbackId = entry.getValue().getAsString();
                onPress = mCallbackStrategy.createPendingIntent(callbackId);
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
}
