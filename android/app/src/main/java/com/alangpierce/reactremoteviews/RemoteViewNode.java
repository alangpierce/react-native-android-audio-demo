package com.alangpierce.reactremoteviews;

import java.util.List;

public class RemoteViewNode {
    private final String mClassName;
    private final List<RemoteViewProperty> mProperties;
    private final List<RemoteViewNode> mChildren;

    public RemoteViewNode(String className, List<RemoteViewProperty> properties, List<RemoteViewNode> children) {
        mClassName = className;
        mProperties = properties;
        mChildren = children;
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
}
