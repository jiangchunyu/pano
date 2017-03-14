package com.vb.pano.view;

/**
 * Created by seven on 2017/1/5.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class ScrollTextView extends TextView {
    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}