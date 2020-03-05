package com.pixlee.pixleeandroidsdk.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleesdk.PXLAnalytics;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPdpAlbum;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLWidgetType;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This shows how you can fire all analytics of Pixlee.
 * Created by sungjun on 2020-02-13.
 */
public class AnalyticsFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_analytics;
    }

    @BindView(R.id.v_progress)
    View v_progress;

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.bt_widget_example)
    View bt_widget_example;

    @BindView(R.id.bt_open_widget)
    View bt_open_widget;

    @BindView(R.id.bt_widget_visible)
    View bt_widget_visible;

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

    @BindView(R.id.v_widget_box)
    View v_widget_box;

    @BindView(R.id.v_widget)
    View v_widget;

    @BindView(R.id.tv_msg1)
    TextView tv_msg1;

    @BindView(R.id.tv_msg2)
    TextView tv_msg2;

    @BindView(R.id.tv_widget_status)
    TextView tv_widget_status;

    @BindView(R.id.scroll_widget)
    NestedScrollView scroll_widget;

    PXLBaseAlbum album;
    PXLAnalytics analytics;
    ArrayList<PXLPhoto> photos = new ArrayList<>();

    @Override
    public boolean isBackInUse() {
        if (v_widget_box != null && v_widget_box.isShown()) {
            closeWidget();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // UI Settings
        setClickListeners();
        setScrollView();

        // Pixlee Settings
        setPixleeCredentials();
        initPixleeAnalytics();
        initPixleeAlbum();
        loadPixleeAlbum();
    }

    private void setPixleeCredentials() {
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
    }

    private void initPixleeAlbum() {
        PXLClient client = PXLClient.getInstance(getContext());
        album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, client);
        // Alternative: album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
    }

    private void loadPixleeAlbum() {
        v_progress.setVisibility(View.VISIBLE);
        enableAlbumButtons(false);
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
        bt_widget_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWidget();
            }
        });

        bt_open_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("openedWidget(..)");
                album.openedWidget(PXLWidgetType.photowall);
                // Alternative: album.openedWidget("<Customized name>");
            }
        });

        bt_widget_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("widgetVisible(..)");
                album.widgetVisible(PXLWidgetType.photowall);
                // Alternative: album.widgetVisible("<Customized name>");
            }
        });

        bt_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_status.setText("Load More...");
                album.loadNextPageOfPhotos(new PXLBaseAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
                    @Override
                    public void onComplete(ArrayList<PXLPhoto> result) {
                        // TODO: uncomment this showDialog("Load More", getString(R.string.guide_load_more));
                        showMessage("loadMore()");

                        // TODO: implement load more accordingly
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
                showMessage("openedLightbox()");
                if (!photos.isEmpty()) {
                    album.openedLightbox(photos.get(0));
                }
            }
        });

        bt_action_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("actionClicked()");
                if (!photos.isEmpty()) {
                    album.actionClicked(photos.get(0), "<link you want>");
                }
            }
        });

        bt_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("addToCart()");
                analytics.addToCart(BuildConfig.PIXLEE_SKU, "12000", 3);

                // Alternative: analytics.addToCart(BuildConfig.PIXLEE_SKU, "13000",2, "AUD");
            }
        });

        bt_conversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("conversion()");
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

    StringBuilder widgetStatus;
    boolean widgetVisible = false;

    void openWidget() {
        v_widget_box.setVisibility(View.VISIBLE);
        String message = "openedWidget " +
                (album.openedWidget(PXLWidgetType.photowall) ? "success" : "failed");
        addWidgetStaus(true, message);

        showToast(message + "!!\n\nScroll down to fire Widget Visible");
        widgetVisible = false;
        final Rect scrollBounds = new Rect();
        scroll_widget.getHitRect(scrollBounds);
        scroll_widget.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v_widget != null) {

                    if (v_widget.getLocalVisibleRect(scrollBounds)) {

                        if (!widgetVisible) {
                            String message2 = "visibleWidget " +
                                    (album.widgetVisible(PXLWidgetType.photowall) ? "success" : "failed");
                            addWidgetStaus(false, message2);
                            showToast(message2 + "!!");
                            widgetVisible = true;
                        }
                        if (!v_widget.getLocalVisibleRect(scrollBounds)
                                || scrollBounds.height() < v_widget.getHeight()) {
                            Log.i("PXLAnalytics", "BTN APPEAR PARCIALY");
                        } else {
                            Log.i("PXLAnalytics", "BTN APPEAR FULLY!!!");
                        }
                    }
                }
            }
        });
    }

    void closeWidget() {
        scroll_widget.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            }
        });
        scroll_widget.fullScroll(ScrollView.FOCUS_UP);
        v_widget_box.setVisibility(View.GONE);
    }

    void addWidgetStaus(boolean clearHistory, String message) {
        if (widgetStatus == null || clearHistory)
            widgetStatus = new StringBuilder();

        widgetStatus.append("- " + message).append("\n");
        tv_widget_status.setText(widgetStatus.toString());
    }

    void setScrollView() {
        tv_msg1.setText(getListMsg("Before Widget Visible", false));
        tv_msg2.setText(getListMsg("After Widget Visible", true));
    }

    String getListMsg(String text, boolean isASC) {
        StringBuilder sb = new StringBuilder();
        if (isASC) {
            for (int i = 1; i <= 100; i++)
                sb.append("----- " + text + " " + String.format("%03d", i) + " ----\n");
        } else {
            for (int i = 100; i > 0; i--)
                sb.append("----- " + text + " " + String.format("%03d", i) + " ----\n");
        }
        return sb.toString();
    }

    private void enableAlbumButtons(boolean enabled) {
        // This conditional means that you can use openWidget() and widgetVisible() on PXLPdpAlbum after receiving album data by firing loadNextPageOfPhotos() is successfully done.
        if (album instanceof PXLPdpAlbum) {
            bt_open_widget.setEnabled(enabled);
            bt_widget_visible.setEnabled(enabled);
            bt_widget_example.setEnabled(enabled);
        }

        bt_load_more.setEnabled(enabled);
        bt_opened_lightbox.setEnabled(enabled);
        bt_action_clicked.setEnabled(enabled);
    }

    private void showMessage(String methodName) {
        String message = getString(R.string.xxx_is_called, methodName);
        tv_status.setText(message);
        showToast(message);
    }
}
