package com.alangpierce.reactremoteviews;

public class RemoteViewProperty {
    private final String mMethodName;
    private final PropertyType mPropertyType;
    private final Object mValue;

    public RemoteViewProperty(String methodName, PropertyType propertyType, Object value) {
        mMethodName = methodName;
        mPropertyType = propertyType;
        mValue = value;
    }

    public enum PropertyType {
        STRING,
        INT
    }

    public String getMethodName() {
        return mMethodName;
    }

    public PropertyType getPropertyType() {
        return mPropertyType;
    }

    public Object getValue() {
        return mValue;
    }
}
