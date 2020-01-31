package com.pixlee.pixleeandroidsdk;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.pixlee.pixleeandroidsdk.gallery.GalleryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    final String TAG = "MainActivity";
    int frameLayoutId = R.id.contentFrame;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_60),
                    PorterDuff.Mode.SRC_ATOP
            );
        }

        setSystemBarColor(R.color.grey_5);
        setSystemBarLight();

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(onBackStackChangedListener);

        replaceFragmentInActivity(frameLayoutId, new GalleryFragment(), null);

    }

    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            FragmentManager fm = getSupportFragmentManager();
            int fragmentCount = fm.getBackStackEntryCount();
            String title;
            if (fragmentCount > 0) {
                BaseFragment fragment = (BaseFragment) fm.getFragments().get(fm.getFragments().size() - 1);
                if (fragment.getCustomTitle() != null) {
                    title = fragment.getCustomTitle();
                } else {
                    title = getString(fragment.getTitleResource());
                }
            } else {
                title = getString(R.string.app_name);
            }

            Log.d(TAG, "fragmentCount: " + fragmentCount);

            setSupportActionBar(toolbar);
            ActionBar bar = getSupportActionBar();
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(fragmentCount > 1);
            bar.setDisplayShowHomeEnabled(fragmentCount > 1);

            invalidateOptionsMenu();
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            int fragmentStackSize = getSupportFragmentManager().getBackStackEntryCount();
            if (fragmentStackSize <= 1) {
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
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


//    private void updateDetailView(PXLPhoto photo) {
//        Context c = this.getApplicationContext();
//        if(photo.sourceIconImage()!=null){
//            this.detailSourceIcon.setImageResource(photo.sourceIconImage());
//        }
//
//        Glide.with(this)
//                .load(photo.getUrlForSize(PXLPhotoSize.MEDIUM).toString())
//                .into(this.detailImage);
//        this.detailText.setText(photo.photoTitle);
//        this.detailUser.setText(String.format("@%s", photo.userName));
//        String unit = "hour";
//        Date now = new Date();
//        //ms
//        long elapsed = now.getTime() - photo.taggedAt.getTime();
//        //s
//        elapsed /= 1000;
//        //hrs
//        elapsed /= 60 * 60;
//        if (elapsed > 1) {
//            unit += "s";
//        }
//        if (elapsed >= 24) {
//            elapsed /= 24;
//            unit = "day";
//            if (elapsed > 1) {
//                unit += "s";
//            }
//        }
//        this.detailLastMod.setText(String.format("%s %s ago", elapsed, unit));
//
//        if (photo.products.size() > 0) {
//            actionLinksLayout.setVisibility(View.VISIBLE);
//        } else {
//            actionLinksLayout.setVisibility(View.GONE);
//        }
//
//
//        //album.openedLightbox(photo); // Opened Lightbox Analytics Example
//        //album.actionClicked(photo, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");
//
//        /* ~~~ Add to cart analytics example ~~~
//
//        PXLAnalytics pixleeAnalytics = new PXLAnalytics(c);
//        pixleeAnalytics.addToCart("sku123", "123", 4);
//
//        */
//
//
//        /* ~~~ Conversion analytics example ~~~
//
//        ArrayList<HashMap<String, Object>> cartContents = new ArrayList();
//        HashMap<String, Object> cart1 = new HashMap();
//        cart1.put("price", "123");
//        cart1.put("product_sku", "test123");
//        cart1.put("quantity", "4");
//
//        cartContents.add(cart1);
//        pixleeAnalytics.conversion(cartContents, "123", 4);
//        */
//
//
//        this.populateDetailActions(photo);
//    }

//    private void populateDetailActions(PXLPhoto photo) {
//        actionLinksLayout.removeAllViews();
//        for (int i = 0; i < photo.products.size(); i++) {
//            PXLProductView pxlProductView = new PXLProductView(this, null);
//            pxlProductView.populate(photo.products.get(i));
//            actionLinksLayout.addView(pxlProductView);
//        }
//    }


}
