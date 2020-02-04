package com.pixlee.pixleesdk;

import android.util.Log;

import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;

import retrofit2.Call;

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
     * Constructor requires the album id and context, which will be passed along to the PXLClient
     * for volley configuration.
     * @param id
     * @param client PXLClient
     */
    public PXLAlbum(String id, PXLClient client) {
        this(id, client.getBasicRepo(), client.getAnalyticsRepo());
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
}
