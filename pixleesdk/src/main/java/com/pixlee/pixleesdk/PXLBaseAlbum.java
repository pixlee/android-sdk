package com.pixlee.pixleesdk;

import android.util.Log;

import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Parent Class for PXLAlbum.java and PXLPdpAlbum.java
 */
public abstract class PXLBaseAlbum {
    public static final String TAG = "PXLBaseAlbum";
    public static final int DefaultPerPage = 20;

    //For API calls
    protected BasicDataSource basicRepo;
    protected AnalyticsDataSource analyticsRepo;

    /**
     * This is 'album_id' in API response data
     */
    public String album_id;

    //For searching
    protected int page;
    protected int perPage;
    protected boolean hasMore;
    protected int lastPageLoaded;
    protected ArrayList<PXLPhoto> photos;
    protected PXLAlbumFilterOptions filterOptions;
    protected PXLAlbumSortOptions sortOptions;
    protected HashMap<Integer, Boolean> pagesLoading;

    /**
     * Constructor requires two Network classes
     *
     * @param basicRepo     Restful API for photos
     * @param analyticsRepo Restful API for analytics
     */
    public PXLBaseAlbum(BasicDataSource basicRepo, AnalyticsDataSource analyticsRepo) {
        this.basicRepo = basicRepo;
        this.analyticsRepo = analyticsRepo;

        this.page = 0;
        this.perPage = DefaultPerPage;
        this.hasMore = true;
        this.lastPageLoaded = 0;
        this.photos = new ArrayList<>();
        this.pagesLoading = new HashMap<>();
    }

    /***
     * Sets the amount of photos fetched per call of 'loadNextPageOfPhotos'.  Will purge previously
     * fetched photos. Call 'loadNextPageOfPhotos' after setting.
     * @param perPage - number of photos per page
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
        this.resetState();
    }

    /***
     * Sets the filter options for the album. Will purge previously fetched photos. Call
     * 'loadNextPageOfPhotos' after setting.
     * @param filterOptions
     */
    public void setFilterOptions(PXLAlbumFilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        this.resetState();
    }

    /***
     * Sets the sort options for the album. Will purge previously fetched photos. Call
     * 'loadNextPageOfPhotos' after setting.
     * @param sortOptions
     */
    public void setSortOptions(PXLAlbumSortOptions sortOptions) {
        this.sortOptions = sortOptions;
        this.resetState();
    }

    protected void resetState() {
        this.photos.clear();
        this.lastPageLoaded = 0;
        this.hasMore = true;
        this.pagesLoading.clear();
    }

    /***
     * Interface for callbacks for loadNextPageOfPhotos
     */
    public interface RequestHandlers {
        void DataLoadedHandler(List<PXLPhoto> photos);

        void DataLoadFailedHandler(String error);
    }

    public abstract boolean loadNextPageOfPhotos(RequestHandlers handlers);

    /**
     * This is for loadNextPageOfPhotos(RequestHandlers handlers)
     * save API response data and fire RequestHandlers.DataLoadedHandler(PXLPhoto) as a callback
     * @param result API response data
     * @param handlers A callback
     */
    protected void setData(PhotoResult result, RequestHandlers handlers) {
        Log.e("retrofit result", "retrofit result:" + result.total);
        Log.e("retrofit result", "retrofit result:" + result.photos.size());
        for (PXLPhoto photo : result.photos) {
            Log.e("retrofit result", "retrofit smallUrl:" + photo.cdnPhotos.mediumUrl);

        }
        page = result.page;
        perPage = result.perPage;
        hasMore = result.next;
        if (album_id == null) {
            album_id = String.valueOf(result.albumId);
        }
        //add placeholders for photos if they haven't been loaded yet
        if (photos.size() < (page - 1) * perPage) {
            for (int i = photos.size(); i < (page - 1) * perPage; i++) {
                photos.add(null);
            }
        }

        photos.addAll(result.photos);
        lastPageLoaded = Math.max(page, lastPageLoaded);

        //handlers set when making the original 'loadNextPageOfPhotos' call
        if (handlers != null) {
            handlers.DataLoadedHandler(photos);
        }
    }

    public interface PhotoLoadHandlers {
        void photoLoaded(PXLPhoto photo);

        void photoLoadFailed(String error);
    }

    /**
     * Retrieve a PXLPhoto with album_photo_id
     *
     * @param photo    this is to get PXLPhoto.albumPhotoId
     * @param callback
     */
    public void getPhotoWithId(PXLPhoto photo, final PhotoLoadHandlers callback) {
        if (photo == null || photo.albumPhotoId == null) {
            Log.e(TAG, "no album_photo_id given");
            return;
        }

        getPhotoWithId(photo.albumPhotoId, callback);
    }

    /**
     * Retrieve a PXLPhoto with album_photo_id
     *
     * @param album_photo_id PXLPhoto.albumPhotoId
     * @param callback
     */
    public void getPhotoWithId(String album_photo_id, final PhotoLoadHandlers callback) {
        if (album_photo_id == null) {
            Log.e(TAG, "no album_photo_id given");
            return;
        }

        basicRepo.getMedia(album_photo_id, PXLClient.apiKey)
                .enqueue(new Callback<PXLPhoto>() {
                             @Override
                             public void onResponse(Call<PXLPhoto> call, Response<PXLPhoto> response) {
                                 PXLPhoto photo = response.body();
                                 if (photo == null) {
                                     Log.e(TAG, "no data from successful api call");
                                 } else {
                                     if (callback != null) {
                                         callback.photoLoaded(photo);
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<PXLPhoto> call, Throwable t) {
                                 if (callback != null) {
                                     callback.photoLoadFailed(t.toString());
                                 }
                             }
                         }
                );
    }

    /**
     * actionClicked Analytics
     *
     * @param photo This is to get PXLPhoto.albumPhotoId
     * @param actionLink
     */
    public void actionClicked(PXLPhoto photo, String actionLink){
        actionClicked(photo.albumPhotoId, actionLink);
    }

    /**
     * actionClicked Analytics
     *
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param actionLink
     */
    public void actionClicked(String albumPhotoId, String actionLink) {
        if (album_id == null) {
            throw new IllegalArgumentException("no album_id");
        }

        JSONObject body = new JSONObject();

        try {
            body.put("album_id", Integer.parseInt(album_id));
            body.put("album_photo_id", Integer.parseInt(albumPhotoId));
            body.put("action_link_url", actionLink);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall("events/actionClicked", body);
    }


    /**
     * openedLightbox Analytics
     *
     * @param photo This is to get PXLPhoto.albumPhotoId
     */
    public void openedLightbox(PXLPhoto photo) {
        openedLightbox(photo.albumPhotoId);
    }

    /**
     * openedLightbox Analytics
     *
     * @param albumPhotoId PXLPhoto.albumPhotoId
     */
    public void openedLightbox(String albumPhotoId) {
        if (album_id == null) {
            throw new IllegalArgumentException("no album_id");
        }

        JSONObject body = new JSONObject();
        try {
            body.put("album_id", Integer.parseInt(album_id));
            body.put("album_photo_id", Integer.parseInt(albumPhotoId));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall("events/openedLightbox", body);
    }
}
