package com.example.jzycc.mld_view.utils;

import android.content.Context;

public class PixelCalculationUtils {

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources ().getDisplayMetrics ().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }


}
