package com.alangpierce.reactremoteviews;

import android.app.PendingIntent;
import android.support.annotation.Nullable;

import java.util.List;

public class RemoteViewNode {
    private final String mClassName;
    private final List<RemoteViewProperty> mProperties;
    private final List<RemoteViewNode> mChildren;
    private final @Nullable PendingIntent mOnClick;

    public RemoteViewNode(String className, List<RemoteViewProperty> properties, List<RemoteViewNode> children, PendingIntent onClick) {
        mClassName = className;
        mProperties = properties;
        mChildren = children;
        mOnClick = onClick;
    }

    public String getClassName() {
        return mClassName;
    }

    public List<RemoteViewProperty> getProperties() {
        return mProperties;
    }

    public List<RemoteViewNode> getChildren() {
        return mChildren;
    }

    @Nullable
    public PendingIntent getOnClick() {
        return mOnClick;
    }
}
