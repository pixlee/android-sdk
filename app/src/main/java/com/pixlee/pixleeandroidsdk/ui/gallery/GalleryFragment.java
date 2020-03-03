package com.pixlee.pixleeandroidsdk.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.ui.viewer.ImageViewerFragment;
import com.pixlee.pixleeandroidsdk.ui.viewer.VideoViewerFragment;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLAnalytics;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;
import com.pixlee.pixleesdk.PXLWidgetType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
public class GalleryFragment extends BaseFragment implements PXLAlbum.RequestHandlers<ArrayList<PXLPhoto>>{
    @Override
    public int getTitleResource() {
        return R.string.title_album;
    }

    private PXLAlbum album;
    private ArrayList<PXLPhoto> photoList;

    private int lastImg = R.drawable.grid_2x;

    @BindView(R.id.viewSwitcher1)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.gridToggle)
    ImageView gridToggleButton;

    @BindView(R.id.imagegallery)
    RecyclerView gridView;

    @BindView(R.id.imagelist)
    RecyclerView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoList = new ArrayList<>();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridToggleButton.setImageResource(lastImg);
        gridToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showNext();
                if (lastImg == R.drawable.grid_2x) {
                    lastImg = R.drawable.column_2x;
                } else {
                    lastImg = R.drawable.grid_2x;
                }
                gridToggleButton.setImageResource(lastImg);
            }
        });

        loadAlbum();
        configureViews();
    }



    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private void loadAlbum() {
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
        PXLClient client = PXLClient.getInstance(getContext().getApplicationContext());

        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
        //Alternative
        //album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client.getBasicRepo(), client.getAnalyticsRepo());

        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
        fo.minTwitterFollowers = 0;
        fo.minInstagramFollowers = 0;


        /* ~~~ content source and content filter examples ~~~
          ArrayList contentSource = new ArrayList();
          contentSource.add(PXLContentSource.INSTAGRAM);
          fo.contentSource = contentSource;

          ArrayList contentType = new ArrayList();
          contentType.add(PXLContentType.IMAGE);
          fo.contentType = contentType;
        */


        /* ~~~ date filter examples ~~~
          fo.submittedDateEnd = new Date(2019, 7, 16);
          fo.submittedDateStart = new Date(2019, 7, 17);
        */


        // fo.filterByRadius = "21.3069,-157.8583,20";  radius filter example


        /* ~~~ in_categories filter example ~~~
          ArrayList incategories = new ArrayList<Integer>();
          incategories.add(1234);
          incategories.add(5678);
          fo.inCategories = incategories;
         */

        /* ~~~ filter_by_userhandle filter example ~~~

          HashMap userHandleFilter = new HashMap<String, Object> ();
          userHandleFilter.put("contains", new String[] {"test1", "test2"});
          fo.filterByUserhandle = userHandleFilter;

         */

         /* ~~~ computer_vision filter example ~~~

          HashMap computerVisionFilter = new HashMap<String, Object> ();
          computerVisionFilter.put("contains", new String[] {"hat"});
          fo.computerVision = computerVisionFilter;

         */


        // fo.hasProduct = false;
        // fo.hasPermission = false;
        // fo.inStockOnly = false;

        PXLAlbumSortOptions so = new PXLAlbumSortOptions();
        so.sortType = PXLAlbumSortType.APPROVED_TIME;
        so.descending = true;
        album.setPerPage(20);
        album.setFilterOptions(fo);
        album.setSortOptions(so);
        PXLAlbum.RequestHandlers rh = this;
        album.loadNextPageOfPhotos(rh);


        PXLAnalytics analytics = new PXLAnalytics(client);
        //Alternative
        //PXLAnalytics analytics = new PXLAnalytics(client.getAnalyticsRepo());

        analytics.addToCart(BuildConfig.PIXLEE_SKU, "13000",2, "AUD");
        /* ~~~ content upload example ~~~

          album.uploadImage("test",
                "kb@pixleeteam.com",
                "K.B.",
                "https://timedotcom.files.wordpress.com/2019/05/drake-nba-finals-warning.jpg",
                true,
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
            @Override
            public void onComplete(MediaResult result) {

            }

            @Override
            public void onError(String error) {

            }
        });

        */

    }

    private void configureViews() {
        gridView.setHasFixedSize(true);
        listView.setHasFixedSize(true);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), 2);
        RecyclerView.LayoutManager listLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), 1);
        gridView.setLayoutManager(gridLayoutManager);
        listView.setLayoutManager(listLayoutManager);

        GalleryClickListener li = new GalleryClickListener() {
            @Override
            public void onItemClicked(PXLPhoto photo) {
                moveToViewer(photo);
            }
        };

        GridAdapter gridAdapter = new GridAdapter(getContext().getApplicationContext(), photoList, li);
        ListAdapter listAdapter = new ListAdapter(getContext().getApplicationContext(), photoList, li);
        gridView.setAdapter(gridAdapter);
        listView.setAdapter(listAdapter);

        RecyclerViewEndlessScrollListener gridScrollListener = new RecyclerViewEndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePhotos();
            }
        };
        RecyclerViewEndlessScrollListener listScrollListener = new RecyclerViewEndlessScrollListener(listLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePhotos();
            }
        };

        gridView.addOnScrollListener(gridScrollListener);
        listView.addOnScrollListener(listScrollListener);
    }

    /**
     * When an item in the list is clicked, this method call Video player or Image viewer using PXLPhoto.isVideo()
     * @param photo
     */
    void moveToViewer(PXLPhoto photo) {
        // You can choose images by using this example below.
        // PXLPhotoSize Options: [ORIGINAL, BIG, MEDIUM, THUMBNAIL]
        String url = photo.getUrlForSize(PXLPhotoSize.ORIGINAL).toString();
        if(photo.isVideo()){
            addFragmentToActivity(VideoViewerFragment.getInstance(url));
        }else{
            addFragmentToActivity(ImageViewerFragment.getInstance(url));
        }
    }

    /***
     * callback for endless scroller
     */
    private void loadMorePhotos() {
        this.album.loadNextPageOfPhotos(this);
    }

    private void samplePhotoLoad(PXLPhoto photo) {
        // load PXLPhoto with album_photo_id
        album.getPhotoWithId(photo.albumPhotoId,new PXLBaseAlbum.RequestHandlers<PXLPhoto>(){

            @Override
            public void onComplete(PXLPhoto result) {

            }

            @Override
            public void onError(String error) {
                Log.e("sampleactivity", "failed to load photo: " + error);
            }
        });

        // load PXLPhoto with pxlphoto object
        album.getPhotoWithId(photo, new PXLBaseAlbum.RequestHandlers<PXLPhoto>() {
            @Override
            public void onComplete(PXLPhoto photo) {

            }

            @Override
            public void onError(String error) {
                Log.e("sampleactivity", "failed to load photo: " + error);
            }
        });
    }

    /***
     * Callback for loadNextPageOfPhotos.  Update our list of photos and notify the adapters.
     * @param photos - the complete list of photos (both the latest page and all previous)
     */
    @Override
    public void onComplete(ArrayList<PXLPhoto> photos) {
        if (photos == null) {
            return;
        }
        album.openedWidget(PXLWidgetType.horizontal);
        album.openedWidget("gallery");
        album.loadMore();

        this.photoList.clear();
        this.photoList.addAll(photos);
        gridView.getAdapter().notifyDataSetChanged();
        listView.getAdapter().notifyDataSetChanged();

        if(photos.size()>0){
            samplePhotoLoad(photos.get(0));
        }
    }

    /***
     * Callback for a failed call to loadNextPageOfPhotos
     * @param error
     */
    @Override
    public void onError(String error) {
        Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", error));
    }
}
