package com.pixlee.pixleeandroidsdk.ui.uploader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAnalytics;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLWidgetType;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Image Upload example
 * Created by sungjun on 2020-02-13.
 */
public class ImageUploaderFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_upload_image;
    }

    @BindView(R.id.v_progress)
    View v_progress;

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.bt_open_widget)
    View bt_open_widget;

    @BindView(R.id.bt_load_more)
    View bt_load_more;

    @BindView(R.id.bt_opened_lightbox)
    View bt_opened_lightbox;

    @BindView(R.id.bt_action_clicked)
    View bt_action_clicked;

    @BindView(R.id.bt_add_to_cart)
    View bt_add_to_cart;

    @BindView(R.id.bt_conversion)
    View bt_conversion;

    PXLAlbum album;
    PXLAnalytics analytics;
    ArrayList<PXLPhoto> photos = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_uploader, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // UI Settings
        v_progress.setVisibility(View.VISIBLE);
        enableAlbumButtons(false);
        setClickListeners();

        // Pixlee Settings
        setPixleeCredentials();
        initPixleeAlbum();
        initPixleeAnalytics();
    }

    private void setPixleeCredentials() {
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
    }

    private void initPixleeAlbum() {
        PXLClient client = PXLClient.getInstance(getContext());
        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
        album.loadNextPageOfPhotos(new PXLBaseAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
            @Override
            public void onComplete(ArrayList<PXLPhoto> result) {
                photos.addAll(result);
                tv_status.setText(R.string.album_loading_complete);
                enableAlbumButtons(true);
                v_progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {
                tv_status.setText(getString(R.string.album_loading_failed, error));
                v_progress.setVisibility(View.INVISIBLE);
            }
        });
        tv_status.setText(R.string.album_loading_ing);
    }

    private void initPixleeAnalytics() {
        PXLClient client = PXLClient.getInstance(getContext());
        analytics = new PXLAnalytics(client);
    }

    private void setClickListeners() {
        bt_open_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("openWidget(..)");
                album.openedWidget(PXLWidgetType.photowall);
                // Alternatives
                // album.openedWidget(PXLWidgetType.photowall);
                // album.openedWidget("<Customized name>");
            }
        });

        bt_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_status.setText("Load More...");
                album.loadNextPageOfPhotos(new PXLBaseAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
                    @Override
                    public void onComplete(ArrayList<PXLPhoto> result) {
                        showAnalytics("loadMore()");
                        // todo: uncomment this showDialog("Load More", getString(R.string.guide_load_more));
                        // todo: implement load more accordingly

                        album.loadMore();

                        photos.addAll(result);
                        tv_status.setText(R.string.album_loading_complete);
                    }

                    @Override
                    public void onError(String error) {
                        String msg = getString(R.string.album_loading_failed, error);
                        showToast(msg);
                        tv_status.setText(msg);
                    }
                });
            }
        });

        bt_opened_lightbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("openedLightbox()");
                if (!photos.isEmpty()) {
                    album.openedLightbox(photos.get(0));
                }
            }
        });

        bt_action_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("actionClicked()");
                if (!photos.isEmpty()) {
                    album.actionClicked(photos.get(0), "<link you want>");
                }
            }
        });

        bt_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("addToCart()");
                analytics.addToCart(BuildConfig.PIXLEE_SKU, "12000", 3);
            }
        });

        bt_conversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("conversion()");
                ArrayList<HashMap<String, Object>> cartContents = new ArrayList();
                HashMap<String, Object> cart1 = new HashMap();
                cart1.put("price", "123");
                cart1.put("product_sku", BuildConfig.PIXLEE_SKU);
                cart1.put("quantity", "4");
                cartContents.add(cart1);
                analytics.conversion(cartContents, "123", 4);
            }
        });
    }

    private void enableAlbumButtons(boolean enabled) {
        bt_load_more.setEnabled(enabled);
        bt_opened_lightbox.setEnabled(enabled);
        bt_action_clicked.setEnabled(enabled);
    }

    private void showAnalytics(String methodName) {
        String message = getString(R.string.xxx_is_called, methodName);
        tv_status.setText(message);
        showToast(message);
    }
}
