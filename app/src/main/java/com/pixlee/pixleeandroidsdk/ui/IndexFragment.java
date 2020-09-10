package com.pixlee.pixleeandroidsdk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentIndexBinding;
import com.pixlee.pixleeandroidsdk.ui.gallery.GalleryFragment;
import com.pixlee.pixleeandroidsdk.ui.uploader.ImageUploaderFragment;
import com.pixlee.pixleeandroidsdk.ui.widgets.WidgetsFragment;

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

    FragmentIndexBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIndexBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.btAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new GalleryFragment());
            }
        });

        binding.btWidgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new WidgetsFragment());
            }
        });

        binding.btImageUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new ImageUploaderFragment());
            }
        });

        binding.btAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToActivity(new AnalyticsFragment());
            }
        });
    }

}
