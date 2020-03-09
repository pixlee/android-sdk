package com.pixlee.pixleeandroidsdk.ui.viewer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentVideoViewerBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;

/**
 * This is a video player
 * Created by sungjun.
 */
public class VideoViewerFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_video_viewer;
    }

    FragmentVideoViewerBinding binding;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoViewerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                binding.vLoading.setVisibility(View.GONE);
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

        binding.videoView.setVideoPath(getVideoUrl());
        binding.videoView.start();
    }

    Handler handler = new Handler();
    Runnable runnableTimer = new Runnable(){

        @Override
        public void run() {
            boolean started = binding.videoView.isPlaying();
            if (!started || binding.videoView.isPlaying()) {
                if (!started) {
                    started = binding.videoView.isPlaying();
                }

                binding.tvTime.setText(showMMSS(binding.videoView.getDuration(), binding.videoView.getCurrentPosition()));
                handler.postDelayed(runnableTimer, 1000);
            }else{
                binding.tvTime.setText(showMMSS(binding.videoView.getDuration(), binding.videoView.getDuration()));
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
