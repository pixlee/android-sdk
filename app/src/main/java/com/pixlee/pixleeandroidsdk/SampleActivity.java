package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.toolbox.NetworkImageView;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;

import java.util.ArrayList;
import java.util.Date;

public class SampleActivity extends AppCompatActivity implements PXLAlbum.RequestHandlers {
    private ArrayList<PXLPhoto> photoList;
    private PXLAlbum album;
    private NetworkImageView detailImage;
    private TextView detailText;
    private ImageView detailSourceIcon;
    private TextView detailUser;
    private TextView detailLastMod;
    private ViewSwitcher viewSwitcher;
    private ImageView gridToggleButton;
    private int lastImg;
    private RecyclerView gridView;
    private RecyclerView listView;

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
        this.detailImage = (NetworkImageView) findViewById(R.id.detailImage);
        this.detailText = (TextView) findViewById(R.id.detailText);
        this.detailSourceIcon = (ImageView) findViewById(R.id.detailSourceIcon);
        this.detailUser = (TextView) findViewById(R.id.userName);
        this.detailLastMod = (TextView) findViewById(R.id.lastModified);
        gridView = (RecyclerView)findViewById(R.id.imagegallery);
        listView = (RecyclerView)findViewById(R.id.imagelist);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Callback for loadNextPageOfPhotos.  Update our list of photos and notify the adapters.
     * @param photos - the complete list of photos (both the latest page and all previous)
     */
    @Override
    public void DataLoadedHandler(ArrayList<PXLPhoto> photos) {
        if (photos == null) {
            return;
        }
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
        Context c = this.getApplicationContext();
        PXLClient.initialize("zk4wWCOaHAo4Hi8HsE");
        album = new PXLAlbum("1568132", c);
        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
        fo.minTwitterFollowers = 0;
        fo.minInstagramFollowers = 3147141;
        PXLAlbumSortOptions so = new PXLAlbumSortOptions();
        so.sortType = PXLAlbumSortType.PHOTORANK;
        so.descending = true;
        album.setPerPage(10);
        album.setFilterOptions(fo);
        album.setSortOptions(so);
        PXLAlbum.RequestHandlers rh = this;
        album.loadNextPageOfPhotos(rh);
    }

    /***
     * callback for endless scroller
     */
    private void loadMorePhotos() {
        this.album.loadNextPageOfPhotos(this);
    }

    private void updateDetailView(PXLPhoto photo) {
        this.detailSourceIcon.setImageResource(photo.sourceIconImage());
        this.detailImage.setImageUrl(photo.getUrlForSize(PXLPhotoSize.MEDIUM).toString(), PXLClient.getInstance(this).getImageLoader());
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
    }
}
