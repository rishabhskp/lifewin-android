package app.lifewin.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * This is used for Hide the keyboard
 */
public class HideKeyboard {
    /**
     * Hide keyboard
     * @param mCtx
     */
    public static void keyboardHide(Context mCtx) {
        try {
            InputMethodManager imm = (InputMethodManager) mCtx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) mCtx).getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
