package com.pixlee.pixleeandroidsdk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pixlee.pixleeandroidsdk.gallery.GalleryFragment;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLWidgetType;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnalyticsFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.app_name;
    }

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
        initPixlee();
        setClickListeners();
    }

    void initPixlee(){
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
        PXLClient client = PXLClient.getInstance(getContext());
        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
    }

    void setClickListeners(){

        bt_open_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                album.openedWidget(PXLWidgetType.photowall);
                // Alternatives
                // album.openedWidget(PXLWidgetType.photowall);
                // album.openedWidget("<Customized name>");
            }
        });

        bt_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_opened_lightbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_action_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_conversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
