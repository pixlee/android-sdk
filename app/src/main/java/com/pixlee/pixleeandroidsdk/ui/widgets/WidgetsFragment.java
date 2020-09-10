package com.pixlee.pixleeandroidsdk.ui.widgets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentGalleryBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleeandroidsdk.ui.gallery.GalleryClickListener;
import com.pixlee.pixleeandroidsdk.ui.gallery.GridAdapter;
import com.pixlee.pixleeandroidsdk.ui.gallery.ListAdapter;
import com.pixlee.pixleeandroidsdk.ui.gallery.RecyclerViewEndlessScrollListener;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLContentSource;
import com.pixlee.pixleesdk.PXLContentType;
import com.pixlee.pixleesdk.PXLPdpAlbum;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.ui.activity.PXLPhotoViewerActivity;

import java.util.ArrayList;

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
public class WidgetsFragment extends BaseFragment implements PXLAlbum.RequestHandlers<ArrayList<PXLPhoto>> {
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
        // set filter buttons
        binding.fabFilter.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.END);
        });

        binding.btnCloseFilter.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        });

        binding.btnApply.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
            loadAlbum();
        });

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
        // set credentials for the SDK
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);

        // get PXLClient
        PXLClient client = PXLClient.getInstance(getContext().getApplicationContext());

        // initiate album
        for (int i = 0; i < binding.radioGroupAlbum.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) binding.radioGroupAlbum.getChildAt(i);
            if (binding.radioGroupAlbum.getCheckedRadioButtonId() == rb.getId()) {
                String text = rb.getText().toString();
                if (text.equals(getString(R.string.radio_album)))
                    album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
                else if (text.equals(getString(R.string.radio_pdp)))
                    album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, client.getBasicRepo(), client.getAnalyticsRepo());
                break;
            }
        }

        // if album is not properly started, stop loading it.
        if (album == null) {
            showDialog("No Album", "Album is not properly set. Please check the code and try again");
            return;
        }

        // show a loading UI on the mobile screen
        setLoading(true);

        // set GET request parameters for the API
        album.setPerPage(readPerPage());
        album.setFilterOptions(readFilterOptionsFromUI());
        album.setSortOptions(readSortOptionsFromUI());

        // start requesting the API
        album.loadNextPageOfPhotos(this);
    }

    int readPerPage() {
        // Set textViewPerPage filter if text is not empty
        String minTwitterFollowers = binding.textViewPerPage.getText().toString();
        if (!minTwitterFollowers.isEmpty()) {
            return Integer.valueOf(minTwitterFollowers);
        }

        // a default for perPage
        return 20;
    }

    PXLAlbumSortOptions readSortOptionsFromUI() {
        PXLAlbumSortOptions sortOptions = new PXLAlbumSortOptions();
        // Set sortType filter if a radio button is selected
        for (int i = 0; i < binding.radioGroupSortType.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) binding.radioGroupSortType.getChildAt(i);
            if (binding.radioGroupSortType.getCheckedRadioButtonId() == rb.getId()) {
                String text = rb.getText().toString();
                if (text.equals(PXLAlbumSortType.RECENCY.value))
                    sortOptions.sortType = PXLAlbumSortType.RECENCY;
                else if (text.equals(PXLAlbumSortType.APPROVED_TIME.value))
                    sortOptions.sortType = PXLAlbumSortType.APPROVED_TIME;
                else if (text.equals(PXLAlbumSortType.RANDOM.value))
                    sortOptions.sortType = PXLAlbumSortType.RANDOM;
                else if (text.equals(PXLAlbumSortType.PIXLEE_SHARES.value))
                    sortOptions.sortType = PXLAlbumSortType.PIXLEE_SHARES;
                else if (text.equals(PXLAlbumSortType.PIXLEE_LIKES.value))
                    sortOptions.sortType = PXLAlbumSortType.PIXLEE_LIKES;
                else if (text.equals(PXLAlbumSortType.POPULARITY.value))
                    sortOptions.sortType = PXLAlbumSortType.POPULARITY;
                else if (text.equals(PXLAlbumSortType.DYNAMIC.value))
                    sortOptions.sortType = PXLAlbumSortType.DYNAMIC;
                break;
            }
        }

        // Set sorting direction
        if (binding.radioGroupSortDirection.getCheckedRadioButtonId() == binding.radioGroupSortDirectionASC.getId())
            sortOptions.descending = false;
        else if (binding.radioGroupSortDirection.getCheckedRadioButtonId() == binding.radioGroupSortDirectionDESC.getId())
            sortOptions.descending = true;

        return sortOptions;
    }

    PXLAlbumFilterOptions readFilterOptionsFromUI() {
        PXLAlbumFilterOptions filterOptions = new PXLAlbumFilterOptions();

        // Set minTwitterFollowers filter if text is not empty
        String minTwitterFollowers = binding.textViewMinTwitterFollowers.getText().toString();
        if (!minTwitterFollowers.isEmpty()) {
            filterOptions.minTwitterFollowers = Integer.valueOf(minTwitterFollowers);
        }

        // Set minInstagramFollowers filter if text is not empty
        String minInstagramFollowers = binding.textViewMinInstagramFollowers.getText().toString();
        if (!minInstagramFollowers.isEmpty()) {
            filterOptions.minInstagramFollowers = Integer.valueOf(minInstagramFollowers);
        }

        // Set hasProduct filter if false or not true is set
        for (int i = 0; i < binding.radioGroupHasPermission.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) binding.radioGroupHasPermission.getChildAt(i);
            if (binding.radioGroupHasPermission.getCheckedRadioButtonId() == rb.getId()) {
                String text = rb.getText().toString();
                if (text.equals(getString(R.string.radio_false)))
                    filterOptions.hasPermission = false;
                else if (text.equals(getString(R.string.radio_true)))
                    filterOptions.hasPermission = true;
                break;
            }
        }

        // Set hasProduct filter if false or not true is set
        for (int i = 0; i < binding.radioGroupHasProduct.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) binding.radioGroupHasProduct.getChildAt(i);
            if (binding.radioGroupHasProduct.getCheckedRadioButtonId() == rb.getId()) {
                String text = rb.getText().toString();
                if (text.equals(getString(R.string.radio_false)))
                    filterOptions.hasProduct = false;
                else if (text.equals(getString(R.string.radio_true)))
                    filterOptions.hasProduct = true;
                break;
            }
        }

        // Set inStockOnly filter if false or not true is set
        for (int i = 0; i < binding.radioGroupInStockOnly.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) binding.radioGroupInStockOnly.getChildAt(i);
            if (binding.radioGroupInStockOnly.getCheckedRadioButtonId() == rb.getId()) {
                String text = rb.getText().toString();
                if (text.equals(getString(R.string.radio_false)))
                    filterOptions.inStockOnly = false;
                else if (text.equals(getString(R.string.radio_true)))
                    filterOptions.inStockOnly = true;
                break;
            }
        }

        // Set contentSource filter if any of its check boxes is selected
        ArrayList<PXLContentSource> contentSource = new ArrayList();
        if (binding.radioGroupContentSourceInstagramFeed.isChecked())
            contentSource.add(PXLContentSource.INSTAGRAM_FEED);

        if (binding.radioGroupContentSourceInstagramStory.isChecked())
            contentSource.add(PXLContentSource.INSTAGRAM_STORY);

        if (binding.radioGroupContentSourceTwitter.isChecked())
            contentSource.add(PXLContentSource.TWITTER);

        if (binding.radioGroupContentSourceFacebook.isChecked())
            contentSource.add(PXLContentSource.FACEBOOK);

        if (binding.radioGroupContentSourceApi.isChecked())
            contentSource.add(PXLContentSource.API);

        if (binding.radioGroupContentSourceDesktop.isChecked())
            contentSource.add(PXLContentSource.DESKTOP);

        if (binding.radioGroupContentSourceEmail.isChecked())
            contentSource.add(PXLContentSource.EMAIL);

        if (!contentSource.isEmpty())
            filterOptions.contentSource = contentSource;

        // Set contentType filter if any of its check boxes is selected
        ArrayList<PXLContentType> contentType = new ArrayList();
        if (binding.radioGroupContentTypeImage.isChecked())
            contentType.add(PXLContentType.IMAGE);

        if (binding.radioGroupContentTypeVideo.isChecked())
            contentType.add(PXLContentType.VIDEO);

        if (!contentType.isEmpty())
            filterOptions.contentType = contentType;

        // Apart from the examples above, there are more filters you can implement in you app.
        // These are the example codes

        // ###### date filter examples ######
        // fo.submittedDateEnd = new Date(2019, 7, 16);
        // fo.submittedDateStart = new Date(2019, 7, 17);
        // fo.filterByRadius = "21.3069,-157.8583,20";  radius filter example

        // ###### in_categories filter example ######
        // ArrayList incategories = new ArrayList<Integer>();
        // incategories.add(1234);
        // incategories.add(5678);
        // fo.inCategories = incategories;

        // ###### filter_by_userhandle filter example ######
        // HashMap userHandleFilter = new HashMap<String, Object> ();
        // userHandleFilter.put("contains", new String[] {"test1", "test2"});
        // fo.filterByUserhandle = userHandleFilter;

        // ###### computer_vision filter example ######
        // HashMap computerVisionFilter = new HashMap<String, Object> ();
        // computerVisionFilter.put("contains", new String[] {"hat"});
        // fo.computerVision = computerVisionFilter;
        return filterOptions;
    }


    void setLoading(boolean visible) {
        if (visible) {
            binding.lottieView.setVisibility(View.VISIBLE);
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

        if (!mode.isGridMode)
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
        // optional: PXLPhotoViewerActivity.launch(getContext(), photo, "photo name");
        PXLPhotoViewerActivity.launch(getContext(), photo);
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
