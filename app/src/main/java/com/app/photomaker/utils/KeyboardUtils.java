package com.app.photomaker.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by thevu2907@gmail.com on 1/14/17.
 */

public class KeyboardUtils {

    public static void hideKeyboard(Activity activity) {
        if (null != activity) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    public static void hideKeyboard(View view, Context context) {
        if (null != context) {
            InputMethodManager inputManager =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity, View view) {
        if (null != view) {
            view.requestFocus();
            view.postDelayed(() -> {
                InputMethodManager imm =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }, 200);
        }
    }

    public static void showSoftKeyboard(EditText editText) {
        ((InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}