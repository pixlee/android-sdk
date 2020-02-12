package com.pixlee.pixleeandroidsdk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pixlee.pixleeandroidsdk.BaseFragment;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.gallery.GalleryFragment;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IndexFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.app_name;
    }

    @BindView(R.id.bt_album)
    View bt_album;

    @BindView(R.id.bt_from_sku)
    View bt_from_sku;

    @BindView(R.id.bt_analytics)
    View bt_analytics;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new GalleryFragment());
            }
        });

        bt_from_sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new AnalyticsFragment());
            }
        });
    }

}
