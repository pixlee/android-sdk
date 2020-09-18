package com.pixlee.pixleeandroidsdk.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentAnalyticsBinding;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAnalytics;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPdpAlbum;
import com.pixlee.pixleesdk.data.PXLPhoto;
import com.pixlee.pixleesdk.enums.PXLWidgetType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This shows how you can fire all analytics of Pixlee.
 * Created by sungjun on 2020-02-13.
 */
public class AnalyticsFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_analytics;
    }

    FragmentAnalyticsBinding binding;

    PXLBaseAlbum album;
    PXLAnalytics analytics;
    ArrayList<PXLPhoto> photos = new ArrayList<>();

    @Override
    public boolean isBackInUse() {
        if (binding != null && binding.vWidgetBox.isShown()) {
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
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
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
        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
        // Alternative: album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, client);
    }

    private void loadPixleeAlbum() {
        binding.vProgress.setVisibility(View.VISIBLE);
        enableAlbumButtons(false);
        album.loadNextPageOfPhotos(new PXLBaseAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
            @Override
            public void onComplete(ArrayList<PXLPhoto> result) {
                photos.addAll(result);
                binding.tvStatus.setText(R.string.album_loading_complete);
                enableAlbumButtons(true);
                binding.vProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {
                binding.tvStatus.setText(getString(R.string.album_loading_failed, error));
                binding.vProgress.setVisibility(View.INVISIBLE);
            }
        });
        binding.tvStatus.setText(R.string.album_loading_ing);
    }

    private void initPixleeAnalytics() {
        PXLClient client = PXLClient.getInstance(getContext());
        analytics = new PXLAnalytics(client);
    }

    private void setClickListeners() {
        binding.btWidgetExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWidget();
            }
        });

        binding.btOpenWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("openedWidget(..)");
                album.openedWidget(PXLWidgetType.photowall);
                // Alternative: album.openedWidget("<Customized name>");
            }
        });

        binding.btWidgetVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("widgetVisible(..)");
                album.widgetVisible(PXLWidgetType.photowall);
                // Alternative: album.widgetVisible("<Customized name>");
            }
        });

        binding.btLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvStatus.setText("Load More...");
                album.loadNextPageOfPhotos(new PXLBaseAlbum.RequestHandlers<ArrayList<PXLPhoto>>() {
                    @Override
                    public void onComplete(ArrayList<PXLPhoto> result) {
                        // TODO: uncomment this showDialog("Load More", getString(R.string.guide_load_more));
                        showMessage("loadMore()");

                        // TODO: implement load more accordingly
                        album.loadMore();

                        photos.addAll(result);
                        binding.tvStatus.setText(R.string.album_loading_complete);
                    }

                    @Override
                    public void onError(String error) {
                        String msg = getString(R.string.album_loading_failed, error);
                        showToast(msg);
                        binding.tvStatus.setText(msg);
                    }
                });
            }
        });

        binding.btOpenedLightbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("openedLightbox()");
                if (!photos.isEmpty()) {
                    album.openedLightbox(photos.get(0));
                }
            }
        });

        binding.btActionClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("actionClicked()");
                if (!photos.isEmpty()) {
                    album.actionClicked(photos.get(0), "<link you want>");
                }
            }
        });

        binding.btAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("addToCart()");
                analytics.addToCart(BuildConfig.PIXLEE_SKU, "12000", 3);

                // Alternative: analytics.addToCart(BuildConfig.PIXLEE_SKU, "13000",2, "AUD");
            }
        });

        binding.btConversion.setOnClickListener(new View.OnClickListener() {
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
        binding.vWidgetBox.setVisibility(View.VISIBLE);
        String openedWidgetStatus = "openedWidget " +
                (album.openedWidget(PXLWidgetType.photowall) ? "success" : "failed");
        addWidgetStaus(true, openedWidgetStatus);

        showToast(openedWidgetStatus + "!!\n\nScroll down to fire Widget Visible");
        widgetVisible = false;
        final Rect scrollBounds = new Rect();
        binding.scrollWidget.getHitRect(scrollBounds);
        binding.scrollWidget.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (binding.vWidget != null) {

                    if (binding.vWidget.getLocalVisibleRect(scrollBounds)) {

                        if (!widgetVisible) {
                            String visibleWidgetStatus = "visibleWidget " +
                                    (album.widgetVisible(PXLWidgetType.photowall) ? "success" : "failed");
                            addWidgetStaus(false, visibleWidgetStatus);
                            showToast(visibleWidgetStatus + "!!");
                            widgetVisible = true;
                        }
                        if (!binding.vWidget.getLocalVisibleRect(scrollBounds)
                                || scrollBounds.height() < binding.vWidget.getHeight()) {
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
        binding.scrollWidget.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            }
        });
        binding.scrollWidget.fullScroll(ScrollView.FOCUS_UP);
        binding.vWidgetBox.setVisibility(View.GONE);
    }

    void addWidgetStaus(boolean clearHistory, String message) {
        if (widgetStatus == null || clearHistory)
            widgetStatus = new StringBuilder();

        widgetStatus.append("- " + message).append("\n");
        binding.tvWidgetStatus.setText(widgetStatus.toString());
    }

    void setScrollView() {
        binding.tvMsg1.setText(getListMsg("Before Widget Visible", false));
        binding.tvMsg2.setText(getListMsg("After Widget Visible", true));
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
            binding.btOpenWidget.setEnabled(enabled);
            binding.btWidgetVisible.setEnabled(enabled);
            binding.btWidgetExample.setEnabled(enabled);
        }

        binding.btLoadMore.setEnabled(enabled);
        binding.btOpenedLightbox.setEnabled(enabled);
        binding.btActionClicked.setEnabled(enabled);
    }

    private void showMessage(String methodName) {
        String message = getString(R.string.xxx_is_called, methodName);
        binding.tvStatus.setText(message);
        showToast(message);
    }
}
