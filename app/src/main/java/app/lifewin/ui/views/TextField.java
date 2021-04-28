package app.lifewin.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import app.lifewin.R;


public class TextField extends TextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}
