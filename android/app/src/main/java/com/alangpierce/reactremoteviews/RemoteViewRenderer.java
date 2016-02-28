package com.alangpierce.reactremoteviews;

import com.audiodemo.R;

import android.app.PendingIntent;
import android.widget.RemoteViews;

public class RemoteViewRenderer {
    private final String mPackageName;

    public RemoteViewRenderer(String packageName) {
        mPackageName = packageName;
    }

    /**
     * Note that every layout XML uses the ID remote_view for its only view, so we can always use
     * that ID.
     */
    public RemoteViews renderNode(RemoteViewNode node) {
        RemoteViews result = new RemoteViews(mPackageName, layoutIdForClass(node.getClassName()));
        for (RemoteViewProperty property : node.getProperties()) {
            switch (property.getPropertyType()) {
                case CHAR_SEQUENCE:
                    result.setCharSequence(R.id.remote_view, property.getMethodName(), (CharSequence) property.getValue());
                    break;
                case STRING:
                    result.setString(R.id.remote_view, property.getMethodName(), (String) property.getValue());
                    break;
                case FLOAT:
                    result.setFloat(R.id.remote_view, property.getMethodName(), (float) property.getValue());
                    break;
                case INT:
                    result.setInt(R.id.remote_view, property.getMethodName(), (int) property.getValue());
                    break;
            }
        }
        if (!node.getChildren().isEmpty()) {
            result.removeAllViews(R.id.remote_view);
            for (RemoteViewNode childNode : node.getChildren()) {
                result.addView(R.id.remote_view, renderNode(childNode));
            }
        }
        PendingIntent onClick = node.getOnClick();
        if (onClick != null) {
            result.setOnClickPendingIntent(R.id.remote_view, onClick);
        }
        return result;
    }

    private int layoutIdForClass(String className) {
        switch (className) {
            case "LinearLayoutWrapContent":
                return R.layout.linear_layout_wrap_content;
            case "LinearLayout":
                return R.layout.linear_layout_match_parent;
            case "TextView":
                return R.layout.text_view;
            case "ImageButton":
                return R.layout.image_button;
        }
        throw new RuntimeException("Class " + className + " is not supported!");
    }
}
