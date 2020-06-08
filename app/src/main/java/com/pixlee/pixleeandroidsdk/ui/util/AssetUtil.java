package com.pixlee.pixleeandroidsdk.ui.util;

import android.content.Context;

import java.io.InputStream;

/**
 * Created by sungjun on 6/1/20.
 */
public class AssetUtil {
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
}
