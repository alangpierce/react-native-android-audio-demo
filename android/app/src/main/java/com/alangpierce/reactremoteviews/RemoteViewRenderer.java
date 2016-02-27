package com.alangpierce.reactremoteviews;

import com.wikipediareader.R;

import android.widget.RemoteViews;

public class RemoteViewRenderer {
    private final String mPackageName;

    public RemoteViewRenderer(String packageName) {
        mPackageName = packageName;
    }

    public RemoteViews renderNode(RemoteViewNode node) {
        RemoteViews result = new RemoteViews(mPackageName, layoutIdForClass(node.getClassName()));
        for (RemoteViewProperty property : node.getProperties()) {
            switch (property.getPropertyType()) {
                case STRING:
                    result.setString(R.id.linear_layout, property.getMethodName(), (String) property.getValue());
                    break;
                case INT:
                    result.setInt(R.id.linear_layout, property.getMethodName(), (int) property.getValue());
                    break;
            }
        }
        for (RemoteViewNode childNode : node.getChildren()) {
            result.addView(R.id.linear_layout, renderNode(childNode));
        }
        return result;
    }

    private int layoutIdForClass(String className) {
        switch (className) {
            case "LinearLayout":
                return R.layout.linear_layout;
            case "TextView":
                return R.layout.text_view;
            case "ImageButton":
                return R.layout.image_button;
        }
        throw new RuntimeException("Class " + className + " is not supported!");
    }
}
