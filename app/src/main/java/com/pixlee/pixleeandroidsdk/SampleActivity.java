package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewSwitcher;

import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;

public class SampleActivity extends AppCompatActivity implements PXLAlbum.RequestHandlers {
    private ArrayList<CreateList> imageList;
    private ArrayList<PXLPhoto> photoList;
    private RecyclerViewEndlessScrollListener scrollListener;
    private RecyclerViewEndlessScrollListener scrollListener2;
    private PXLAlbum album;

    private final String image_titles[] = {
            "Img1",
            "Img2",
            "Img3",
            "Img4",
            "Img5",
            "Img6"
    };

    private final Integer image_ids[] = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6
    };

    public void switchVisibilities() {
        if (findViewById(R.id.detailview).getVisibility() == View.VISIBLE) {
            findViewById(R.id.detailview).setVisibility(View.GONE);
            findViewById(R.id.viewSwitcher1).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.detailview).setVisibility(View.VISIBLE);
            findViewById(R.id.viewSwitcher1).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher1);
                viewSwitcher.showNext();
            }
        });

        this.createAlbum();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        RecyclerView recyclerView2 = (RecyclerView)findViewById(R.id.imagelist);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        imageList = this.prepareData();
        photoList = new ArrayList<>();
        recyclerView2.setLayoutManager(layoutManager2);
        ArrayList<CreateList> createLists = prepareData();
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(getApplicationContext(), photoList, this);
        MyListAdapter adapter2 = new MyListAdapter(getApplicationContext(), photoList, this);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);

        scrollListener = new RecyclerViewEndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMorePhotos();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        scrollListener2 = new RecyclerViewEndlessScrollListener(layoutManager2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMorePhotos();
            }
        };

        recyclerView2.addOnScrollListener(scrollListener2);
    }

    private ArrayList<CreateList> prepareData(){
        ArrayList<CreateList> theimage = new ArrayList<>();
        for(int i = 0; i< image_titles.length; i++){
            CreateList createList = new CreateList();
            createList.setImage_title(image_titles[i]);
            createList.setImage_ID(image_ids[i]);
            theimage.add(createList);
        }
        return theimage;
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
        album.loadNextPageOfPhotos(rh);

        Log.w("sampleactivity", "created album");
    }

    private void loadMorePhotos() {
        this.album.loadNextPageOfPhotos(this);
    }

    @Override
    public void DataLoadedHandler(ArrayList<PXLPhoto> photos) {
        if (photos == null) {
            return;
        }
        this.photoList.clear();
        this.photoList.addAll(photos);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        RecyclerView recyclerViewList = (RecyclerView)findViewById(R.id.imagelist);
        /*
        for (int i = 0; i < photos.size(); i++) {
            PXLPhoto photo = photos.get(i);
            Log.d("sampleactivity", photo.toString());
            this.imageList.get(i).setImage_title(photo.photoTitle);
            this.imageList.get(i).setImagePath(photo.thumbnailUrl);
        }
        */
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerViewList.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void DataLoadFailedHandler(String error) {

    }
}
