package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleeandroidsdk.gallery.GridAdapter;
import com.pixlee.pixleeandroidsdk.gallery.ListAdapter;
import com.pixlee.pixleeandroidsdk.gallery.PXLProductView;
import com.pixlee.pixleeandroidsdk.gallery.RecyclerViewEndlessScrollListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleActivity extends BaseActivity {
    private ArrayList<PXLPhoto> photoList;

    private ImageView detailImage;
    private TextView detailText;
    private ImageView detailSourceIcon;
    private TextView detailUser;
    private TextView detailLastMod;

    private LinearLayout actionLinksLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoList = new ArrayList<>();

        this.detailImage = (ImageView) findViewById(R.id.detailImage);
        this.detailText = (TextView) findViewById(R.id.detailText);
        this.detailSourceIcon = (ImageView) findViewById(R.id.detailSourceIcon);
        this.detailUser = (TextView) findViewById(R.id.userName);
        this.detailLastMod = (TextView) findViewById(R.id.lastModified);
        actionLinksLayout = (LinearLayout) findViewById(R.id.actionLinksLayout);

    }

    /***
     * Callback for loadNextPageOfPhotos.  Update our list of photos and notify the adapters.
     * @param photos - the complete list of photos (both the latest page and all previous)
     */
//    @Override
//    public void DataLoadedHandler(List<PXLPhoto> photos) {
//        if (photos == null) {
//            return;
//        }
//        album.openedWidget();
//        album.loadMore();
//        this.photoList.clear();
//        this.photoList.addAll(photos);
//        gridView.getAdapter().notifyDataSetChanged();
//        listView.getAdapter().notifyDataSetChanged();
//    }

    /***
     * Callback for a failed call to loadNextPageOfPhotos
     * @param error
     */
//    @Override
//    public void DataLoadFailedHandler(String error) {
//        Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", error));
//    }


    private void updateDetailView(PXLPhoto photo) {
        Context c = this.getApplicationContext();
        if(photo.sourceIconImage()!=null){
            this.detailSourceIcon.setImageResource(photo.sourceIconImage());
        }

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


        //album.openedLightbox(photo); // Opened Lightbox Analytics Example
        //album.actionClicked(photo, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");

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


}
