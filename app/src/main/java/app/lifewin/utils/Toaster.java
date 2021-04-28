package app.lifewin.utils;

import android.content.Context;
import android.widget.Toast;


public class Toaster {
    private Toaster() {
    }

    public static void show(Context pContext,int value){
        Toast.makeText(pContext, value, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context pContext,String value){
        Toast.makeText(pContext, value, Toast.LENGTH_SHORT).show();
    }
}
