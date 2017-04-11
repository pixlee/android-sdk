package com.pixlee.pixleesdk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AlbumTests {
    private final static String TestAlbumId = "1568132";
    private final static String TestApiKey = "zk4wWCOaHAo4Hi8HsE";
    private PXLAlbum testAlbum;
    private Random random;

    @Before
    public void setup() {
        Context c = InstrumentationRegistry.getTargetContext();
        PXLClient.initialize(TestApiKey);
        testAlbum = new PXLAlbum(TestAlbumId, c);
        this.random = new Random();
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
            testAlbum.loadNextPageOfPhotos(new PXLAlbum.RequestHandlers() {
                @Override
                public void DataLoadedHandler(ArrayList<PXLPhoto> photos) {
                    for (PXLPhoto p : photos) {
                        Log.d("testFilters", p.toString());
                    }
                }

                @Override
                public void DataLoadFailedHandler(String error) {
                    Log.d("testFilters", "test failure");
                    Assert.fail("unable to load photos");
                }
            });
        }

    }

    private ArrayList<PXLAlbumFilterOptions> getTestFilterOptions() throws IllegalAccessException {
        ArrayList<PXLAlbumFilterOptions> testCases = new ArrayList<PXLAlbumFilterOptions>();
        int numTests = 1;
        Field[] fields = PXLAlbumFilterOptions.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
            Object val = this.getTestVal(fields[i]);
            if (val != null) {
                fields[i].set(fo, val);
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
        } else {
            return null;
        }
    }
}
