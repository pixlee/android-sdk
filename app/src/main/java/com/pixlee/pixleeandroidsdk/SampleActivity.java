package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleActivity extends AppCompatActivity implements PXLAlbum.RequestHandlers {
    private ArrayList<PXLPhoto> photoList;
    private PXLAlbum album;
    private ImageView detailImage;
    private TextView detailText;
    private ImageView detailSourceIcon;
    private TextView detailUser;
    private TextView detailLastMod;
    private ViewSwitcher viewSwitcher;
    private ImageView gridToggleButton;
    private int lastImg;
    private RecyclerView gridView;
    private RecyclerView listView;
    private LinearLayout actionLinksLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoList = new ArrayList<>();

        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher1);
        gridToggleButton = (ImageView) findViewById(R.id.gridToggle);
        lastImg = R.drawable.grid_2x;
        this.detailImage = (ImageView) findViewById(R.id.detailImage);
        this.detailText = (TextView) findViewById(R.id.detailText);
        this.detailSourceIcon = (ImageView) findViewById(R.id.detailSourceIcon);
        this.detailUser = (TextView) findViewById(R.id.userName);
        this.detailLastMod = (TextView) findViewById(R.id.lastModified);
        gridView = (RecyclerView)findViewById(R.id.imagegallery);
        listView = (RecyclerView)findViewById(R.id.imagelist);
        actionLinksLayout = (LinearLayout) findViewById(R.id.actionLinksLayout);

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

        createAlbum();
        configureViews();
        samplePhotoLoad();
    }

    /***
     * Toggles between grid/list view and the detail view
     * @param photo
     */
    public void switchVisibilities(PXLPhoto photo) {
        if (findViewById(R.id.detailview).getVisibility() == View.VISIBLE) {
            findViewById(R.id.detailview).setVisibility(View.GONE);
            viewSwitcher.setVisibility(View.VISIBLE);
            gridToggleButton.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.detailview).setVisibility(View.VISIBLE);
            updateDetailView(photo);
            viewSwitcher.setVisibility(View.GONE);
            gridToggleButton.setVisibility(View.GONE);
        }
    }

    /***
     * Callback for loadNextPageOfPhotos.  Update our list of photos and notify the adapters.
     * @param photos - the complete list of photos (both the latest page and all previous)
     */
    @Override
    public void DataLoadedHandler(List<PXLPhoto> photos) {
        if (photos == null) {
            return;
        }
        album.openedWidget();
        album.loadMore();
        this.photoList.clear();
        this.photoList.addAll(photos);
        gridView.getAdapter().notifyDataSetChanged();
        listView.getAdapter().notifyDataSetChanged();
    }

    /***
     * Callback for a failed call to loadNextPageOfPhotos
     * @param error
     */
    @Override
    public void DataLoadFailedHandler(String error) {
        Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", error));
    }

    private void configureViews() {
        gridView.setHasFixedSize(true);
        listView.setHasFixedSize(true);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        RecyclerView.LayoutManager listLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridView.setLayoutManager(gridLayoutManager);
        listView.setLayoutManager(listLayoutManager);
        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), photoList, this);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext(), photoList, this);
        gridView.setAdapter(gridAdapter);
        listView.setAdapter(listAdapter);

        LinearLayout detailLayout = (LinearLayout) findViewById(R.id.detailview);
        detailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibilities(null);
            }
        });

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

    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private void createAlbum() {
        PXLClient.initialize("196i8ZzIAhKU8dO2kDe");
        PXLClient client = PXLClient.getInstance(this);

        album = new PXLAlbum("4503434", client.getBasicrepo(), client.getAnalyticsRepo());
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
        so.sortType = PXLAlbumSortType.RECENCY;
        so.descending = true;
        album.setPerPage(20);
        album.setFilterOptions(fo);
        album.setSortOptions(so);
        PXLAlbum.RequestHandlers rh = this;
        album.loadNextPageOfPhotos(rh);

        /* ~~~ content upload example ~~~

          album.uploadImage("test", "kb@pixleeteam.com", "K.B.", "https://timedotcom.files.wordpress.com/2019/05/drake-nba-finals-warning.jpg", true);

        */
    }

    /***
     * callback for endless scroller
     */
    private void loadMorePhotos() {
        this.album.loadNextPageOfPhotos(this);
    }

    private void updateDetailView(PXLPhoto photo) {
        Context c = this.getApplicationContext();
        this.detailSourceIcon.setImageResource(photo.sourceIconImage());
        Glide.with(this)
                .load(photo.getUrlForSize(PXLPhotoSize.MEDIUM).toString())
                .into(this.detailImage);
        this.detailText.setText(photo.photoTitle);
        this.detailUser.setText(String.format("@%s", photo.userName));
        String unit = "hour";
        Date now = new Date();
        //ms
        long elapsed = now.getTime() - photo.taggedAt.getTime();
        //s
        elapsed /= 1000;
        //hrs
        elapsed /= 60 * 60;
        if (elapsed > 1) {
            unit += "s";
        }
        if (elapsed >= 24) {
            elapsed /= 24;
            unit = "day";
            if (elapsed > 1) {
                unit += "s";
            }
        }
        this.detailLastMod.setText(String.format("%s %s ago", elapsed, unit));

        if (photo.products.size() > 0) {
            actionLinksLayout.setVisibility(View.VISIBLE);
        } else {
            actionLinksLayout.setVisibility(View.GONE);
        }


        album.openedLightbox(photo); // Opened Lightbox Analytics Example
        album.actionClicked(photo, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");

        /* ~~~ Add to cart analytics example ~~~

        PXLAnalytics pixleeAnalytics = new PXLAnalytics(c);
        pixleeAnalytics.addToCart("sku123", "123", 4);

        */


        /* ~~~ Conversion analytics example ~~~

        ArrayList<HashMap<String, Object>> cartContents = new ArrayList();
        HashMap<String, Object> cart1 = new HashMap();
        cart1.put("price", "123");
        cart1.put("product_sku", "test123");
        cart1.put("quantity", "4");

        cartContents.add(cart1);
        pixleeAnalytics.conversion(cartContents, "123", 4);
        */


        this.populateDetailActions(photo);
    }

    private void populateDetailActions(PXLPhoto photo) {
        actionLinksLayout.removeAllViews();
        for (int i = 0; i < photo.products.size(); i++) {
            PXLProductView pxlProductView = new PXLProductView(this, null);
            pxlProductView.populate(photo.products.get(i));
            actionLinksLayout.addView(pxlProductView);
        }
    }

    private void samplePhotoLoad() {
        String identifier = "282742015";
        album.getPhotoWithId(identifier, new PXLBaseAlbum.PhotoLoadHandlers() {
            @Override
            public void photoLoaded(PXLPhoto photo) {
                Log.d("testphoto", String.format("%s", photo.cdnSmallUrl));
                Log.d("testphoto", String.format("%s", photo.cdnMediumUrl));
                Log.d("testphoto", String.format("%s", photo.cdnLargeUrl));
                Log.d("testphoto", String.format("%s", photo.cdnOriginalUrl));
            }

            @Override
            public void photoLoadFailed(String error) {
                Log.e("sampleactivity", "failed to load photo: " + error);
            }
        });

        //load from pxlphoto object
        PXLPhoto photo = new PXLPhoto();
        photo.albumPhotoId = identifier;

        album.getPhotoWithId(photo, new PXLBaseAlbum.PhotoLoadHandlers() {
            @Override
            public void photoLoaded(PXLPhoto photo) {
                Log.d("testphoto", String.format("%s", photo.cdnSmallUrl));
                Log.d("testphoto", String.format("%s", photo.cdnMediumUrl));
                Log.d("testphoto", String.format("%s", photo.cdnLargeUrl));
                Log.d("testphoto", String.format("%s", photo.cdnOriginalUrl));
            }

            @Override
            public void photoLoadFailed(String error) {
                Log.e("sampleactivity", "failed to load photo: " + error);
            }
        });
    }
}
