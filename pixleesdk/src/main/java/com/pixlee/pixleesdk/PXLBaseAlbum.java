package com.pixlee.pixleesdk;

import android.util.Log;

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

    public abstract boolean loadNextPageOfPhotos(final RequestHandlers handlers);


    public interface PhotoLoadHandlers {
        void photoLoaded(PXLPhoto photo);
        void photoLoadFailed(String error);
    }
    /**
     * Retrieve a PXLPhoto with album_photo_id
     * @param photo this is to get PXLPhoto.albumPhotoId
     * @param callback
     */
    public void getPhotoWithId(PXLPhoto photo, final PhotoLoadHandlers callback) {
        if (photo==null || photo.albumPhotoId == null) {
            Log.e(TAG, "no album_photo_id given");
            return;
        }

        getPhotoWithId(photo.albumPhotoId, callback);
    }

    /**
     * Retrieve a PXLPhoto with album_photo_id
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
     * Analytics methods
     * @param albumId PXLPhoto.albumid
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param actionLink
     */
    public void actionClicked(String albumId, String albumPhotoId, String actionLink) {
        JSONObject body = new JSONObject();

        try {
            body.put("album_id", Integer.parseInt(albumId));
            body.put("album_photo_id", Integer.parseInt(albumPhotoId));
            body.put("action_link_url", actionLink);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall("events/actionClicked", body);
    }

    /**
     * Analytics openedLightbox
     * @param albumId PXLPhoto.albumid
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @return
     */
    public boolean openedLightbox(String albumId, String albumPhotoId) {
        JSONObject body = new JSONObject();
        try {
            body.put("album_id", Integer.parseInt(albumId));
            body.put("album_photo_id", Integer.parseInt(albumPhotoId));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall("events/openedLightbox", body);
        return true;
    }
}
