package com.pixlee.pixleesdk;

import android.util.Log;

import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;

import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     * This is for unit test. Not for the use
     * @return
     */
    Call<PhotoResult> makeCall() {
        if (album_id == null) {
            Log.w(TAG, "No album id specified");
            return null;
        }
        if (!this.hasMore) {
            return null;
        }

        int desiredPage = this.lastPageLoaded + 1;
        if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
            Log.d(TAG, String.format("page %s already loading", desiredPage));
            return null;
        }
        this.pagesLoading.put(desiredPage, true);

        return basicRepo.getPhotosWithID(
                this.album_id,
                PXLClient.apiKey,
                filterOptions != null ? filterOptions.toParamString() : null,
                sortOptions != null ? sortOptions.toParamString() : null,
                perPage,
                desiredPage
        );
    }

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
