package com.pixlee.pixleeandroidsdk.viewer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.pixlee.pixleeandroidsdk.BaseFragment;
import com.pixlee.pixleeandroidsdk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoViewerFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_video_viewer;
    }

    @BindView(R.id.videoView)
    VideoView videoView;

    @BindView(R.id.v_loading)
    View v_loading;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_viewer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                v_loading.setVisibility(View.GONE);
                getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if(event==Lifecycle.Event.ON_DESTROY){
                            handler.postDelayed(runnableTimer, 0);
                        }else{
                            handler.removeCallbacks(runnableTimer);
                        }
                    }
                });
            }
        });

        videoView.setVideoPath(getVideoUrl());
        videoView.start();
    }

    Handler handler = new Handler();
    Runnable runnableTimer = new Runnable(){

        @Override
        public void run() {
            boolean started = videoView.isPlaying();
            if (!started || videoView.isPlaying()) {
                if (!started) {
                    started = videoView.isPlaying();
                }

                tv_time.setText(showMMSS(videoView.getDuration(), videoView.getCurrentPosition()));
                handler.postDelayed(runnableTimer, 1000);
            }else{
                tv_time.setText(showMMSS(videoView.getDuration(), videoView.getDuration()));
            }
        }
    };

    String showMMSS(int duration, int timeInMilli) {
        int gap = duration - timeInMilli;
        int sec = gap / 1000;
        int min = sec / 60;
        int secOfMin = sec % 60;
        return String.format("$min:%02d", secOfMin);
    }

    private String getVideoUrl() {
        return getArguments().getString("videoUrl");
    }

    public static Fragment getInstance(String imageUrl){
        Fragment f = new VideoViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoUrl", imageUrl);
        f.setArguments(bundle);
        return f;
    }
}
