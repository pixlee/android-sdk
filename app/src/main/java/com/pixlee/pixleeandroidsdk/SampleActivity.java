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

import com.android.volley.toolbox.ImageLoader;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;

public class SampleActivity extends AppCompatActivity implements PXLAlbum.RequestHandlers {
    private ArrayList<CreateList> imageList;
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
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        imageList = prepareData();
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(getApplicationContext(), imageList);
        MyListAdapter adapter2 = new MyListAdapter(getApplicationContext(), imageList);
        recyclerView.setAdapter(adapter);
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
        PXLAlbum album = new PXLAlbum("1568132", c);
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

    @Override
    public void DataLoadedHandler(ArrayList<PXLPhoto> photos) {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        ImageLoader il = PXLClient.getInstance(this).getImageLoader();
        for (int i = 0; i < photos.size() && i < this.imageList.size(); i++) {
            PXLPhoto photo = photos.get(i);
            Log.d("sampleactivity", photo.toString());
            this.imageList.get(i).setImage_title(photo.photoTitle);
            this.imageList.get(i).setImagePath(photo.thumbnailUrl);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void DataLoadFailedHandler(String error) {

    }
}