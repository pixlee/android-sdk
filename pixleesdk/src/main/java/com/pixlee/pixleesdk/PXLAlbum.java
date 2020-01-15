package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Represents a Pixlee album. Constructs appropriate API calls to fetch the desired set of photos.
 * Specify all sort/filter/etc. parameters before calling 'loadNextPageOfPhotos'. Basic usage:
 * --construct an album object
 * --specify photos per page, sort options, and filter options
 * --call 'loadNextPageOfPhotos'
 */
public class PXLAlbum implements RequestCallbacks {
    private static final String TAG = "PXLAlbum";
    public static final int DefaultPerPage = 20;

    public String id = null;
    protected int page;
    protected int perPage;
    protected boolean hasMore;
    protected int lastPageLoaded;
    protected ArrayList<PXLPhoto> photos;
    protected PXLAlbumFilterOptions filterOptions;
    protected PXLAlbumSortOptions sortOptions;
    protected HashMap<Integer, Boolean> pagesLoading;
    protected RequestHandlers handlers;
    protected Context context;


    /***
     * Callback for a successful call to the api.  Parses the response and converts the json data
     * to PXLPhoto objects.
     * @param response - JSONObject from the request
     */
    @Override
    public void JsonReceived(JSONObject response) {
        try {
            this.page = response.getInt("page");
            this.perPage = response.getInt(("per_page"));
            this.hasMore = response.getBoolean(("next"));
            if (this.id == null) {
                this.id = String.valueOf(response.getInt("album_id"));
            }
            //add placeholders for photos if they haven't been loaded yet
            if (this.photos.size() < (this.page - 1) * this.perPage) {
                for (int i = this.photos.size(); i < (this.page - 1) * this.perPage; i++) {
                    this.photos.add(null);
                }
            }
            this.photos.addAll(this.photos.size(), PXLPhoto.fromJsonArray(response.getJSONArray("data"), this));
            this.lastPageLoaded = Math.max(this.page, this.lastPageLoaded);

            //handlers set when making the original 'loadNextPageOfPhotos' call
            if (handlers != null) {
                handlers.DataLoadedHandler(this.photos);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * Callback for errors that occur during the api request
     * @param error - error from volley
     */
    @Override
    public void ErrorResponse(Exception error) {
        if (handlers != null) {
            handlers.DataLoadFailedHandler(error.toString());
        }
    }

    /***
     * Interface for callbacks for loadNextPageOfPhotos
     */
    public interface RequestHandlers {
        void DataLoadedHandler(List<PXLPhoto> photos);

        void DataLoadFailedHandler(String error);
    }

    /***
     * Constructor requires the album id and context, which will be passed along to the PXLClient
     * for volley configuration.
     * @param id - album id
     * @param context - context which will be used for volley configuration
     */
    public PXLAlbum(String id, Context context) {
        this.id = id;
        this.page = 0;
        this.perPage = DefaultPerPage;
        this.hasMore = true;
        this.lastPageLoaded = 0;
        this.photos = new ArrayList<>();
        this.pagesLoading = new HashMap<>();
        this.context = context;
    }

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param handlers - called upon success/failure of the request
     * @return true if the request was attempted, false if aborted before the attempt was made
     */
    public boolean loadNextPageOfPhotos(final RequestHandlers handlers) {
        if (id == null) {
            Log.w(TAG, "No album id specified");
            return false;
        }
        if (this.hasMore) {
            int desiredPage = this.lastPageLoaded + 1;
            if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
                Log.d(TAG, String.format("page %s already loading", desiredPage));
                return false;
            }
            PXLClient pxlClient = PXLClient.getInstance(context);
            this.pagesLoading.put(desiredPage, true);
            this.handlers = handlers;

            try {
                pxlClient
                        .getBasicrepo()
                        .getPhotosWithID(
                                this.id,
                                PXLClient.apiKey,
                                filterOptions != null ? filterOptions.toParamString() : null,
                                sortOptions != null ? sortOptions.toParamString() : null,
                                perPage,
                                desiredPage
                        ).enqueue(
                        new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject json = new JSONObject(response.body());
                                    JsonReceived(json);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                if (handlers != null) {
                                    handlers.DataLoadFailedHandler(t.toString());
                                }
                            }
                        }
                );
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
    public boolean uploadImage(String title, String email, String username, String photoURI, Boolean approved) {
        PXLClient pxlClient = PXLClient.getInstance(context);
        JSONObject body = new JSONObject();

        try {
            body.put("album_id", Integer.parseInt(this.id));
            body.put("title", title);
            body.put("email", email);
            body.put("username", username);
            body.put("photo_uri", photoURI);
            body.put("approved", approved);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            pxlClient
                    .getBasicrepo()
                    .postMedia(
                            PXLClient.apiKey,
                            body
                            )
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject json = new JSONObject(response.body());
                                JsonReceived(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    protected HashMap<String, Object> getRequestParams(int desiredPage) {
        HashMap<String, Object> paramMap = new HashMap<>();
        if (filterOptions != null) {
            paramMap.put(PXLClient.KeyFilters, filterOptions.toParamString());
        }
        if (sortOptions != null) {
            paramMap.put(PXLClient.KeySort, sortOptions.toParamString());
        }
        paramMap.put(PXLClient.KeyPerPage, perPage);
        paramMap.put(PXLClient.KeyPage, desiredPage);
        return paramMap;
    }

    /***
     * Analytics methods
     */

    public boolean openedWidget() {
        PXLClient pxlClient = PXLClient.getInstance(context);
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
            body.put("album_id", Integer.parseInt(this.id));
            body.put("per_page", this.perPage);
            body.put("page", this.page);
            body.put("photos", stringBuilder.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/openedWidget", body);
        return true;
    }

    public boolean loadMore() {
        if (id == null) {
            Log.w(TAG, "missing album id");
            return false;
        }
        if (this.page < 2) {
            Log.w(TAG, "first load detected");
            return false;
        }
        PXLClient pxlClient = PXLClient.getInstance(context);
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
            body.put("album_id", Integer.parseInt(this.id));
            body.put("per_page", this.perPage);
            body.put("page", this.page);
            body.put("photos", stringBuilder.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/loadMore", body);
        return true;
    }
}
