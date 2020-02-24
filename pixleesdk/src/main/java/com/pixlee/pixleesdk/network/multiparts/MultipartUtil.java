package com.pixlee.pixleesdk.network.multiparts;

import android.util.Log;

import java.io.File;

import okhttp3.MultipartBody;

/**
 * Created by sungjun on 2020-02-12.
 */
public class MultipartUtil {
    public MultipartBody.Part getMultipartBody(String paramName, File file) {
        CountingFileRequestBody requestBody = new CountingFileRequestBody("multipart/form-data", file, getListener(file.length()));
        MultipartBody.Part body = MultipartBody.Part.createFormData(paramName, file.getName(), requestBody);
        return body;
    }

    private CountingFileRequestBody.ProgressListener getListener(final long fileSize) {
        return new CountingFileRequestBody.ProgressListener() {
            @Override
            public void transferred(long num) {
                Log.d("PRETTY", "fileSize:" + FileFormat.convert(fileSize) + ", num:" + FileFormat.convert(num) + ", progress:" + ((float) num / (float) fileSize * 100f));
            }
        };
    }
}
