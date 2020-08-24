package com.pixlee.pixleesdk.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import java.io.InputStream;

/**
 * Created by sungjun on 6/1/20.
 */
public class PXLViewUtil {
    public static String getLottieLoadingJson(Context context) {
        return getText(context, "lottie/pixlee_loading.json");
    }

    public static String getText(Context context, String fileName) {
        String tContents = "";

        try {
            InputStream stream = context.getAssets().open(fileName);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here
        }

        return tContents;

    }

    public static int getStatusBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static void expandContentAreaOverStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
