package app.lifewin.ui.views;


import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * This class is used for creating the custom edittext which
 */
public class CustomEditText extends EditText {
	
    /**
     * FormFieldEditText constructor.
     *
     * @param context  Context that will display this view.
     * @param attrs    Set of attributes defined for this view.
     * @param defStyle Style defined for this view.
     */
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initComponents(attrs);
    }

    /**
     * FormFieldEditText constructor.
     *
     * @param context Context that will display this view.
     * @param attrs   Set of attributes defined for this view.
     */
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(attrs);
    }

    /**
     * FormFieldEditText constructor.
     *
     * @param context Context that will display this view.
     */
    public CustomEditText(Context context) {
        super(context);
        initComponents(null);
    }

    /**
     * Initialize the UI components.
     *
     * @param attrs Set of attributes defined for this view.
     */
    private void initComponents(AttributeSet attrs) {
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(" ")) {
                    CustomEditText.this.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        super.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}