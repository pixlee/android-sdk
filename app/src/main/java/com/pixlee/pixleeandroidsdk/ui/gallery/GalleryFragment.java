package com.pixlee.pixleeandroidsdk.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentGalleryBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleeandroidsdk.ui.util.AssetUtil;
import com.pixlee.pixleeandroidsdk.ui.viewer.ImageViewerActivity;
import com.pixlee.pixleeandroidsdk.ui.viewer.VideoViewerActivity;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
public class GalleryFragment extends BaseFragment implements PXLAlbum.RequestHandlers<ArrayList<PXLPhoto>> {
    @Override
    public int getTitleResource() {
        return R.string.title_album;
    }

    private PXLBaseAlbum album;
    private ArrayList<PXLPhoto> photoList;

    private int lastImg = R.drawable.grid_2x;

    FragmentGalleryBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        photoList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.gridToggleButton.setImageResource(lastImg);
        binding.gridToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewSwitcher.showNext();
                if (lastImg == R.drawable.grid_2x) {
                    lastImg = R.drawable.column_2x;
                } else {
                    lastImg = R.drawable.grid_2x;
                }
                binding.gridToggleButton.setImageResource(lastImg);
            }
        });

        if (gridAdapter == null) {
            loadAlbum();
        }

        configureViews();
    }


    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private void loadAlbum() {
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
        PXLClient client = PXLClient.getInstance(getContext().getApplicationContext());

        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
        //Alternative:
//         album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, client.getBasicRepo(), client.getAnalyticsRepo());

        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
//        fo.minTwitterFollowers = 0;
//        fo.minInstagramFollowers = 0;
//        fo.hasPermission = true;
//        fo.hasProduct = true;



        /* ~~~ content source and content filter examples ~~~
          ArrayList contentSource = new ArrayList();
          contentSource.add(PXLContentSource.INSTAGRAM_FEED);
          contentSource.add(PXLContentSource.INSTAGRAM_STORY);
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
        //album.setPerPage(9);
        album.setFilterOptions(fo);
        album.setSortOptions(so);
        PXLAlbum.RequestHandlers rh = this;

        setLoading(true);
        album.loadNextPageOfPhotos(rh);
    }

    void setLoading(boolean visible) {
        if (visible) {
            binding.lottieView.setVisibility(View.VISIBLE);
            String json = AssetUtil.getLottieLoadingJson(getContext());
            binding.lottieView.setAnimationFromJson(json, json);
            binding.lottieView.playAnimation();
        } else {
            binding.lottieView.setVisibility(View.GONE);
        }

    }

    class Mode {
        boolean isGridMode = true;
    }

    Mode mode;
    GridAdapter gridAdapter;
    ListAdapter listAdapter;

    private void configureViews() {
        binding.gridView.setHasFixedSize(true);
        binding.listView.setHasFixedSize(true);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), 2);
        RecyclerView.LayoutManager listLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), 1);
        binding.gridView.setLayoutManager(gridLayoutManager);
        binding.listView.setLayoutManager(listLayoutManager);

        GalleryClickListener li = new GalleryClickListener() {
            @Override
            public void onItemClicked(View view, PXLPhoto photo) {
                moveToViewer(view, photo);
            }
        };

        if (gridAdapter == null) {
            gridAdapter = new GridAdapter(getContext().getApplicationContext(), photoList, li);
            listAdapter = new ListAdapter(getContext().getApplicationContext(), photoList, li);
        }

        binding.gridView.setAdapter(gridAdapter);
        binding.listView.setAdapter(listAdapter);

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

        binding.gridView.addOnScrollListener(gridScrollListener);
        binding.listView.addOnScrollListener(listScrollListener);

        if (mode == null) {
            mode = new Mode();
        }

        if(!mode.isGridMode)
            binding.viewSwitcher.showNext();
    }

    /**
     * When an item in the list is clicked, this method call Video player or Image viewer using PXLPhoto.isVideo()
     *
     * @param photo
     */
    void moveToViewer(View view, PXLPhoto photo) {
        // You can choose images by using this example below.
        // PXLPhotoSize Options: [ORIGINAL, BIG, MEDIUM, THUMBNAIL]
        if (photo.isVideo()) {
            VideoViewerActivity.launch(getContext(), photo);
        } else {
            ImageViewerActivity.launch(getContext(), photo);
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
        album.getPhotoWithId(photo.albumPhotoId, new PXLBaseAlbum.RequestHandlers<PXLPhoto>() {

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
        setLoading(false);
        if (photos == null) {
            return;
        }
        //album.openedWidget(PXLWidgetType.horizontal);
        //album.openedWidget("gallery");
        //album.loadMore();

        this.photoList.clear();
        this.photoList.addAll(photos);

        binding.gridView.getAdapter().notifyDataSetChanged();
        binding.listView.getAdapter().notifyDataSetChanged();

        if (photos.size() > 0) {
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

    private Menu menuList;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gallery, menu);
        menuList = menu;
        changeMenuForList(mode.isGridMode);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        binding.viewSwitcher.showNext();
        switch (item.getItemId()) {
            case R.id.action_grid: {
                changeMenuForList(false);
                break;
            }
            case R.id.action_list: {
                changeMenuForList(true);
                break;
            }
        }
        return false;
    }

    void changeMenuForList(boolean gridMode) {
        if (gridMode) {
            mode.isGridMode = true;
            menuList.findItem(R.id.action_grid).setVisible(true);
            menuList.findItem(R.id.action_list).setVisible(false);
        } else {
            mode.isGridMode = false;
            menuList.findItem(R.id.action_grid).setVisible(false);
            menuList.findItem(R.id.action_list).setVisible(true);

        }
    }
}
