package com.akramhossain.quranulkarim.util;

import android.util.Log;
import android.view.View;

public class Utils {

    public Utils() {
    }

    public static void preventTwoClick(final View view, long miliSec){
        Log.d("Delay time in milisec",String.valueOf(miliSec));
        view.setEnabled(false);
        view.postDelayed(
                ()-> view.setEnabled(true),
                miliSec
        );
    }
}
