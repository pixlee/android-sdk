package com.pixlee.pixleesdk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/***
 * PXLAlbum tests
 */
@RunWith(AndroidJUnit4.class)
public class AlbumTests {
    private final static String TestAlbumId = "1568132";
    private final static String TestApiKey = "dhXS3FBYcS65ZAVudJy4";// "zk4wWCOaHAo4Hi8HsE";
    private PXLAlbum testAlbum;
    private Random random;
    private int requestCount;

    @Before
    public void setup() {
        Context c = InstrumentationRegistry.getTargetContext();
        PXLClient.initialize(TestApiKey);
        testAlbum = new PXLAlbum(TestAlbumId, c);
        this.random = new Random();
        requestCount = 0;
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.pixlee.pixleesdk.test", appContext.getPackageName());
    }

    @Test
    public void testFilters() throws Exception {
        ArrayList<PXLAlbumFilterOptions> filterOptions = this.getTestFilterOptions();
        for (PXLAlbumFilterOptions fo : filterOptions) {
            testAlbum.setFilterOptions(fo);
            Log.d("AlbumTests", "making call");
            requestCount++;
            testAlbum.loadNextPageOfPhotos(new PXLAlbum.RequestHandlers() {
                @Override
                public void DataLoadedHandler(ArrayList<PXLPhoto> photos) {
                    Log.d("testFilters", String.format("Fetched %s photos", photos.size()));
                    requestCount--;
                }

                @Override
                public void DataLoadFailedHandler(String error) {
                    Log.d("testFilters", "test failure");
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
        //update api key and photo id to match
        String id = "187177895";
        PXLPhoto.getPhotoWithId(InstrumentationRegistry.getTargetContext(), id, new PXLPhoto.PhotoLoadHandlers() {
            @Override
            public void photoLoaded(PXLPhoto photo) {
                Log.d("testphoto", String.format("%s", photo.cdnSmallUrl));
                Log.d("testphoto", String.format("%s", photo.cdnMediumUrl));
                Log.d("testphoto", String.format("%s", photo.cdnLargeUrl));
                Log.d("testphoto", String.format("%s", photo.cdnOriginalUrl));
                requestCount--;
            }

            @Override
            public void photoLoadFailed(String error) {
                requestCount--;
            }
        });

        PXLPhoto photo = new PXLPhoto(InstrumentationRegistry.getTargetContext(), id);
        requestCount++;
        photo.loadFromId(new PXLPhoto.PhotoLoadHandlers() {
            @Override
            public void photoLoaded(PXLPhoto photo) {
                Log.d("testphoto", String.format("%s", photo.cdnSmallUrl));
                Log.d("testphoto", String.format("%s", photo.cdnMediumUrl));
                Log.d("testphoto", String.format("%s", photo.cdnLargeUrl));
                Log.d("testphoto", String.format("%s", photo.cdnOriginalUrl));
                requestCount--;
            }

            @Override
            public void photoLoadFailed(String error) {
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
        filterOptions.contentSource.add(PXLContentSource.INSTAGRAM);
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
