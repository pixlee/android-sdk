package com.pixlee.pixleeandroidsdk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleeandroidsdk.gallery.GridAdapter;
import com.pixlee.pixleeandroidsdk.gallery.ListAdapter;
import com.pixlee.pixleeandroidsdk.gallery.RecyclerViewEndlessScrollListener;
import com.pixlee.pixleeandroidsdk.viewer.ImageViewerFragment;
import com.pixlee.pixleeandroidsdk.viewer.VideoViewerFragment;
import com.pixlee.pixleesdk.PXLAlbum;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment {
    public void replaceFragmentInActivity(Fragment fragment) {
        if (getActivity() instanceof SampleActivity) {
            SampleActivity mainActivity = (SampleActivity) getActivity();

            mainActivity.replaceFragmentInActivity(mainActivity.frameLayoutId, fragment, null);
        } else {
            Toast.makeText(getContext(), "need to add replaceFragmentInActivity() to your activity", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFragmentToActivity(Fragment fragment) {
        if (getActivity() instanceof SampleActivity) {
            SampleActivity mainActivity = (SampleActivity) getActivity();

            mainActivity.addFragmentToActivity(mainActivity.frameLayoutId, fragment, null);
        } else {
            Toast.makeText(getContext(), "need to add addFragmentToActivity() to your activity", Toast.LENGTH_SHORT).show();;
        }
    }
}
