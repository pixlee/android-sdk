package com.pixlee.pixleeandroidsdk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.ui.gallery.GalleryFragment;
import com.pixlee.pixleeandroidsdk.ui.uploader.ImageUploaderFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is an index page of the app.
 *
 * Created by sungjun on 2020-02-13.
 */
public class IndexFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.app_name;
    }

    @BindView(R.id.bt_album)
    View bt_album;

    @BindView(R.id.bt_image_uploader)
    View bt_image_uploader;

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

        bt_image_uploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new ImageUploaderFragment());
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
