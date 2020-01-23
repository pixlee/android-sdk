package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.squareup.moshi.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * ViewModel of MVVM architecture
 *
 * Represents a Pixlee album. Constructs appropriate API calls to fetch the desired set of photos.
 * Specify all sort/filter/etc. parameters before calling 'loadNextPageOfPhotos'. Basic usage:
 * --construct an album object
 * --specify photos per page, sort options, and filter options
 * --call 'loadNextPageOfPhotos'
 */
public class PXLAlbum extends PXLBaseAlbum{
    private static final String TAG = "PXLAlbum";

    /**
     * Constructor requires the album id and context, which will be passed along to the PXLClient
     * for volley configuration.
     *
     * @param id            - album id
     * @param basicRepo     Restful API for photos
     * @param analyticsRepo Restful API for analytics
     */
    public PXLAlbum(String id, BasicDataSource basicRepo, AnalyticsDataSource analyticsRepo) {
        super(basicRepo, analyticsRepo);
        this.album_id = id;
    }

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param handlers - called upon success/failure of the request
     * @return true if the request was attempted, false if aborted before the attempt was made
     */
    @Override
    public boolean loadNextPageOfPhotos(final RequestHandlers handlers) {
        if (album_id == null) {
            Log.w(TAG, "No album id specified");
            return false;
        }
        if (this.hasMore) {
            int desiredPage = this.lastPageLoaded + 1;
            if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
                Log.d(TAG, String.format("page %s already loading", desiredPage));
                return false;
            }
            this.pagesLoading.put(desiredPage, true);
            try {
                basicRepo.getPhotosWithID(
                        this.album_id,
                        PXLClient.apiKey,
                        filterOptions != null ? filterOptions.toParamString() : null,
                        sortOptions != null ? sortOptions.toParamString() : null,
                        perPage,
                        desiredPage
                ).enqueue(new Callback<PhotoResult>() {
                    @Override
                    public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                        setData(response.body(), handlers);
                    }

                    @Override
                    public void onFailure(Call<PhotoResult> call, Throwable t) {
                        if (handlers != null) {
                            handlers.DataLoadFailedHandler(t.toString());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param title - title or caption of the photo being uploaded
     * @param email - email address of the submitting user
     * @param username - username of the submitting user
     * @param photoURI - the URI of the photo being submitted (must be a public URI)
     * @param approved - boolean specifying whether the photo should be marked as approved on upload
     * @return true if the request was made, false if aborted before the attempt was made
     */
    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param title    - title or caption of the photo being uploaded
     * @param email    - email address of the submitting user
     * @param username - username of the submitting user
     * @param photoURI - the URI of the photo being submitted (must be a public URI)
     * @param approved - boolean specifying whether the photo should be marked as approved on upload
     * @param handlers - a callback fired after this api call is finished
     * @return true if the request was made, false if aborted before the attempt was made
     */
    public boolean uploadImage(String title, String email, String username, String photoURI, Boolean approved, final RequestHandlers handlers) {
        JSONObject body = new JSONObject();

        try {
            body.put("album_id", Integer.parseInt(this.album_id));
            body.put("title", title);
            body.put("email", email);
            body.put("username", username);
            body.put("photo_uri", photoURI);
            body.put("approved", approved);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            basicRepo.postMedia(
                    PXLClient.apiKey,
                    body
            ).enqueue(new Callback<PhotoResult>() {
                @Override
                public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                    //setData(response.body(), handlers);
                }

                @Override
                public void onFailure(Call<PhotoResult> call, Throwable t) {
                    if (handlers != null) {
                        handlers.DataLoadFailedHandler(t.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
