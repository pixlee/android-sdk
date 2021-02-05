package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import com.pixlee.pixleesdk.client.PXLAlbum;
import com.pixlee.pixleesdk.client.PXLBaseAlbum;
import com.pixlee.pixleesdk.client.PXLClient;
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.data.PXLPhoto;
import com.pixlee.pixleesdk.enums.PXLContentSource;
import com.pixlee.pixleesdk.enums.PXLContentType;
import com.pixlee.pixleesdk.enums.PXLPhotoSize;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/***
 * PXLAlbum tests
 */
public class AlbumTests {
    private final static String TestAlbumId = BuildConfig.PIXLEE_ALBUM_ID;
    private final static String TestApiKey = BuildConfig.PIXLEE_API_KEY;
    private PXLAlbum testAlbum;
    private Random random;
    private int requestCount;

    @Before
    public void setup() {
        Context c = InstrumentationRegistry.getTargetContext();
        PXLClient.Companion.initialize(TestApiKey, null);

        testAlbum = new PXLAlbum(TestAlbumId, PXLClient.Companion.getInstance(c));
        this.random = new Random();
        requestCount = 0;
    }


    @Test
    public void testFilters() throws Exception {
        ArrayList<PXLAlbumFilterOptions> filterOptions = this.getTestFilterOptions();
        for (PXLAlbumFilterOptions fo : filterOptions) {
            testAlbum.setFilterOptions(fo);
            Log.d("AlbumTests", "making call");
            requestCount++;
            testAlbum.loadNextPageOfPhotos(new PXLAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
                @Override
                public void onComplete(ArrayList<PXLPhoto> photos) {
                    Log.d("testFilters", String.format("Fetched %s photos", photos.size()));
                    requestCount--;
                }

                @Override
                public void onError(String error) {
                    Log.d("testFilters", "test failure, error: " + error);
                    Assert.fail("unable to load photos");
                    requestCount--;
                }
            });
        }
        while (requestCount >  0) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testPhotoLoad() throws Exception {
        requestCount++;
        // update api key and photo id to match
        String album_photo_id = "381257461";
        testAlbum.getPhotoWithId(album_photo_id, new PXLBaseAlbum.RequestHandlers<PXLPhoto>() {
            @Override
            public void onComplete(PXLPhoto photo) {
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.THUMBNAIL)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.MEDIUM)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.BIG)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.ORIGINAL)));
                requestCount--;
            }

            @Override
            public void onError(String error) {
                requestCount--;
            }
        });

        PXLPhoto photo = new PXLPhoto();
        photo.albumPhotoId = album_photo_id;
        requestCount++;
        testAlbum.getPhotoWithId(photo, new PXLBaseAlbum.RequestHandlers<PXLPhoto>() {
            @Override
            public void onComplete(PXLPhoto photo) {
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.THUMBNAIL)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.MEDIUM)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.BIG)));
                Log.d("testphoto", "testFilters: " + String.format("%s", photo.getUrlForSize(PXLPhotoSize.ORIGINAL)));
                requestCount--;
            }

            @Override
            public void onError(String error) {
                requestCount--;
            }
        });
        while (requestCount >  0) {
            Thread.sleep(100);
        }
    }

    public void testOtherFilter() throws Exception {
        PXLAlbumFilterOptions filterOptions = new PXLAlbumFilterOptions();
        filterOptions.contentSource = new ArrayList<>();
        filterOptions.contentSource.add(PXLContentSource.INSTAGRAM_FEED);
        filterOptions.contentType.add(PXLContentType.IMAGE);

    }

    private ArrayList<PXLAlbumFilterOptions> getTestFilterOptions() throws IllegalAccessException {
        ArrayList<PXLAlbumFilterOptions> testCases = new ArrayList<PXLAlbumFilterOptions>();
        Field[] fields = PXLAlbumFilterOptions.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
            Object val = this.getTestVal(fields[i]);

            if (val != null) {
                fields[i].set(fo, val);
            } else {
                Log.d("pxlAlbumTest", String.format("failed to get options for %s", fields[i].getName()));
            }
            testCases.add(fo);
        }
        return testCases;
    }

    private Object getTestVal(Field field) {
        if("filterByRadius".equals(field.getName())){
            return "21.3069,-157.8583,20";
        }

        Class<?> type = field.getType();
        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt(100);
        } else if (type.equals(Date.class)) {
            Date now = new Date();
            int maxDaysAgo = 90;
            long nowMilli = now.getTime();
            long msecAgo = random.nextInt(maxDaysAgo) * 24 * 60 * 60 * 1000;
            now.setTime(nowMilli - msecAgo);
            return now;
        } else if (type.equals(ArrayList.class)) {
            Type gType = field.getGenericType();
            if (gType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) gType;
                Type[] subTypes = pType.getActualTypeArguments();
                for (int i = 0; i < subTypes.length; i++) {
                    if (subTypes[i].equals(PXLContentSource.class)) {
                        ArrayList<PXLContentSource> testVal = new ArrayList<>();
                        testVal.add(PXLContentSource.values()[random.nextInt(PXLContentSource.values().length)]);
                        return testVal;
                    } else if (subTypes[i].equals(PXLContentType.class)) {
                        ArrayList<PXLContentType> testVal = new ArrayList<>();
                        testVal.add(PXLContentType.values()[random.nextInt(PXLContentType.values().length)]);
                        return testVal;
                    }
                }
            }
            return null;
        } else if (type.equals(String.class)) {
            return "work";
        } else {
            return null;
        }
    }
}
