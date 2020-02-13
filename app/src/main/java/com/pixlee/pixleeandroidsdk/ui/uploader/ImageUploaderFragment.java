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
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.MediaResult;

import java.util.ArrayList;

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

    @BindView(R.id.bt_start)
    View bt_start;

    @BindView(R.id.v_progress)
    View v_progress;

    @BindView(R.id.tv_status)
    TextView tv_status;


    PXLAlbum album;
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
        tv_status.setText("Ready");
        v_progress.setVisibility(View.GONE);
        setClickListeners();

        // Pixlee Settings
        setPixleeCredentials();
        initPixleeAlbum();


    }

    public void setPixleeCredentials() {
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
    }

    private void initPixleeAlbum() {
        PXLClient client = PXLClient.getInstance(getContext());
        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
    }


    private void setClickListeners() {
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics("uploadLocalImage(..)");
                album.uploadLocalImage(
                        "yosemite",
                        "sungjun.app@gmail.com",
                        "jun",
                        "https://a.cdn-hotels.com/gdcs/production180/d1647/96f1181c-6751-4d1b-926d-e39039f30d66.jpg",
                        true,
                        new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                            @Override
                            public void onComplete(MediaResult result) {
                                showAnalytics("Upload Success: "  + result);

                            }

                            @Override
                            public void onError(String error) {
                                tv_status.setText(error);
                            }
                        });

            }
        });
    }

    private void showAnalytics(String methodName) {
        String message = getString(R.string.xxx_is_called, methodName);
        tv_status.setText(message);
        showToast(message);
    }
}
