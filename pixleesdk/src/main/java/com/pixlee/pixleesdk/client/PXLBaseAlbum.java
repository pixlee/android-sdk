package com.pixlee.pixleesdk.client;

import android.util.Log;

import com.pixlee.pixleesdk.data.MediaResult;
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.data.PXLHttpError;
import com.pixlee.pixleesdk.data.PXLPhoto;
import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.enums.PXLWidgetType;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
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

    protected int accountId;
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

    /**
     * Gives hint for you whether to use loadNextPageOfPhotos()
     * @return hasMore  true: you can fire loadNextPageOfPhotos() to get more data,    false: you cannot load more data
     */
    public boolean isHasMore() {
        return hasMore;
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

    /**
     * Interface for network callbacks such as loadNextPageOfPhotos(...), getPhotoWithId(...) and uploadImage(...)
     */
    public interface RequestHandlers<T>{
        //http -> 200
        void onComplete(T result);

        //error cases
        void onError(String error);
    }

    /**
     * return success response body to where the requesting api was made
     * @param response
     * @param handlers
     * @param <T> return data type
     */
    public <T> void processReponse(Response response, RequestHandlers handlers){
        if (response.isSuccessful()) {
            if (handlers != null) {
                handlers.onComplete(response.body());
            }
        } else {
            if (handlers != null) {
                handlers.onError(extractErrorText(response));
            }
        }
    }

    /**
     * This is for unit test
     *
     * @return retrofit2.Call
     */
    abstract Call<PhotoResult> makeGetAlbumCall(RequestHandlers<ArrayList<PXLPhoto>> handlers);

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param handlers - called upon success/failure of the request
     */
    public void loadNextPageOfPhotos(final RequestHandlers<ArrayList<PXLPhoto>> handlers) {
        Call<PhotoResult> call = makeGetAlbumCall(handlers);

        if (call == null)
            return;

        call.enqueue(new Callback<PhotoResult>() {
            @Override
            public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                setData(response, handlers);
            }

            @Override
            public void onFailure(Call<PhotoResult> call, Throwable t) {
                t.printStackTrace();
                if (handlers != null) {
                    handlers.onError(t.toString());
                }
            }
        });
    }

    /**
     * This is for loadNextPageOfPhotos(RequestHandlers handlers)
     * save API response data and fire RequestHandlers.onComplete(PXLPhoto) as a callback
     *
     * @param response API response data
     * @param handlers A callback
     */
    public void setData(Response<PhotoResult> response, RequestHandlers<ArrayList<PXLPhoto>> handlers) {
        if (response.isSuccessful()) {
            PhotoResult result = response.body();
            //Log.e("retrofit result", "retrofit result:" + result.total);
            //Log.e("retrofit result", "retrofit result:" + result.photos.size());

            accountId = result.accountId;
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
                handlers.onComplete(photos);
            }
        } else {
            ResponseBody errorBody = response.errorBody();

            String message = "status: " + response.code();
            try {
                if (errorBody != null) {
                    String errorString = errorBody.string();
                    if (errorString != null && errorString.length() > 0) {
                        PXLHttpError error = NetworkModule.provideMoshi().adapter(PXLHttpError.class).lenient().fromJson(errorString);
                        message = error.toString();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (handlers != null)
                    handlers.onError(message);
            }

        }
    }

    /**
     * Retrieve a PXLPhoto with album_photo_id
     *
     * @param photo    this is to get PXLPhoto.albumPhotoId
     * @param callback
     */
    public void getPhotoWithId(PXLPhoto photo, final RequestHandlers callback) {
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
     * @param handlers
     */
    public void getPhotoWithId(String album_photo_id, final RequestHandlers<PXLPhoto> handlers) {
        if (album_photo_id == null) {
            Log.e(TAG, "no album_photo_id given");
            return;
        }

        basicRepo.getMedia(album_photo_id, PXLClient.Companion.getApiKey())
                .enqueue(new Callback<PXLPhoto>() {
                             @Override
                             public void onResponse(Call<PXLPhoto> call, Response<PXLPhoto> response) {
                                 processReponse(response, handlers);
                             }

                             @Override
                             public void onFailure(Call<PXLPhoto> call, Throwable t) {
                                 if (handlers != null) {
                                     handlers.onError(t.toString());
                                 }
                             }
                         }
                );
    }

    Call<MediaResult> makePostUploadImage(String title, String email, String username, String photoURI, Boolean approved) {
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

        return basicRepo.postMediaWithURI(body);
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
     */
    public void postMediaWithURI(String title, String email, String username, String photoURI, Boolean approved, final RequestHandlers<MediaResult> handlers) {
        try {
            makePostUploadImage(title, email, username, photoURI, approved)
                    .enqueue(new Callback<MediaResult>() {
                        @Override
                        public void onResponse(Call<MediaResult> call, Response<MediaResult> response) {
                            processReponse(response, handlers);
                        }

                        @Override
                        public void onFailure(Call<MediaResult> call, Throwable t) {
                            if (handlers != null) {
                                handlers.onError(t.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Call<MediaResult> postMediaWithFile(String title, String email, String username, Boolean approved, String localPhotoPath) {
        JSONObject body = new JSONObject();
        try {
            body.put("album_id", Integer.parseInt(this.album_id));
            body.put("title", title);
            body.put("email", email);
            body.put("username", username);
            body.put("approved", approved);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return basicRepo.postMediaWithURI(body, localPhotoPath);
    }

    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param title    - title or caption of the photo being uploaded
     * @param email    - email address of the submitting user
     * @param username - username of the submitting user
     * @param localPhotoPath - local image file path
     * @param approved - boolean specifying whether the photo should be marked as approved on upload
     * @param handlers - a callback fired after this api call is finished
     */
    public void uploadLocalImage(String title, String email, String username, Boolean approved, String localPhotoPath, final RequestHandlers<MediaResult> handlers) {
        try {
            postMediaWithFile(title, email, username, approved, localPhotoPath)
                    .enqueue(new Callback<MediaResult>() {
                        @Override
                        public void onResponse(Call<MediaResult> call, Response<MediaResult> response) {
                            processReponse(response, handlers);
                        }

                        @Override
                        public void onFailure(Call<MediaResult> call, Throwable t) {
                            if (handlers != null) {
                                handlers.onError(t.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * actionClicked Analytics
     *
     * @param photo      This is to get PXLPhoto.albumPhotoId
     * @param actionLink
     */
    public void actionClicked(PXLPhoto photo, String actionLink) {
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

        analyticsRepo.makeAnalyticsCall("events/actionClicked", body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

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

        analyticsRepo.makeAnalyticsCall("events/openedLightbox", body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    /**
     * widgetVisible Analytics
     * @param widgetType: PXLWidgetType enum class
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    public boolean widgetVisible(PXLWidgetType widgetType) {
        return widgetVisible(widgetType.getType());
    }

    /**
     * widgetVisible Analytics
     * @param widgetType: String
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    public boolean widgetVisible(String widgetType) {
        return fireWidgetCall("events/widgetVisible", widgetType);
    }

    /**
     * openedWidget Analytics
     * @param widgetType: PXLWidgetType enum class
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    public boolean openedWidget(PXLWidgetType widgetType) {
        return openedWidget(widgetType.getType());
    }

    /**
     * openedWidget Analytics
     * @param widgetType: String
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    public boolean openedWidget(String widgetType) {
        return fireWidgetCall("events/openedWidget", widgetType);
    }

    private boolean fireWidgetCall(String requestPath, String widgetType){
        if (album_id == null) {
            Log.w(
                    TAG,
                    "missing album id. " +
                            "When using PXLAlbum, you need to set album_id or when using PXLPdpAlbum, you should use loadNextPageOfPhotos() and get 200(http code) from the api so that this object will receive album_id from it"
            );
            return false;
        }

        JSONObject body = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.photos.size(); i++) {
            try {
                stringBuilder.append(this.photos.get(i).id);
                if (i != this.photos.size() - 1) {
                    stringBuilder.append(",");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            body.put("widget", widgetType);
            body.put("album_id", Integer.parseInt(this.album_id));
            body.put("per_page", this.perPage);
            body.put("page", this.page);
            body.put("photos", stringBuilder.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall(requestPath, body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

        return true;
    }

    public boolean loadMore() {
        if (album_id == null) {
            Log.w(TAG, "missing album id");
            return false;
        }
        if (this.page < 2) {
            Log.w(TAG, "first load detected");
            return false;
        }
        JSONObject body = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        int lastIdx = ((this.page - 1) * this.perPage);
        for (int i = lastIdx; i < this.photos.size(); i++) {
            try {
                stringBuilder.append(this.photos.get(i).id);
                if (i != this.photos.size() - 1) {
                    stringBuilder.append(",");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            body.put("album_id", Integer.parseInt(this.album_id));
            body.put("per_page", this.perPage);
            body.put("page", this.page);
            body.put("photos", stringBuilder.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        analyticsRepo.makeAnalyticsCall("events/loadMore", body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
        return true;
    }

    /**
     * This extracts an error message
     *
     * @param response Any types of retrofit2.Response
     * @return error message,   ex)   status: 404, error: Product does not exist.
     */
    private <T> String extractErrorText(Response<T> response) {
        ResponseBody errorBody = response.errorBody();
        String message = "status: " + response.code();
        try {
            if (errorBody != null) {
                String errorString = errorBody.string();
                if (errorString != null && errorString.length() > 0) {
                    PXLHttpError error = NetworkModule.provideMoshi().adapter(PXLHttpError.class).lenient().fromJson(errorString);
                    message = error.toString();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
