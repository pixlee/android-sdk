package com.pixlee.pixleesdk.client;

import com.pixlee.pixleesdk.data.PXLPhoto;
import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;

import java.util.ArrayList;

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
        this(id, client.getBasicDataSource(), client.getAnalyticsDataSource());
    }

    /**
     * This is for unit test. Not for the use
     * @return
     */
    @Override
    Call<PhotoResult> makeGetAlbumCall(RequestHandlers<ArrayList<PXLPhoto>> handlers) {
        if (album_id == null) {
            handlers.onError("No album id specified");
            return null;
        }
        if (!this.hasMore) {
            handlers.onError("No need to load more");
            return null;
        }

        int desiredPage = this.lastPageLoaded + 1;
        if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
            handlers.onError(String.format("page %s already loading", desiredPage));
            return null;
        }
        this.pagesLoading.put(desiredPage, true);

        return basicRepo.getPhotosWithID(
                this.album_id,
                filterOptions != null ? filterOptions.toParamString() : null,
                sortOptions != null ? sortOptions.toParamString() : null,
                perPage,
                desiredPage,
                regionId
        );
    }
}
