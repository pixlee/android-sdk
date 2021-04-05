package com.pixlee.pixleeandroidsdk.ui.uploader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.databinding.FragmentImageUploaderBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleesdk.client.PXLAlbum;
import com.pixlee.pixleesdk.client.PXLBaseAlbum;
import com.pixlee.pixleesdk.client.PXLClient;
import com.pixlee.pixleesdk.data.MediaResult;
import com.pixlee.pixleesdk.data.PXLPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Image Upload example
 * Created by sungjun on 2020-02-13.
 */
public class ImageUploaderFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_java_upload;
    }

    FragmentImageUploaderBinding binding;

    PXLAlbum album;
    ArrayList<PXLPhoto> photos = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageUploaderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // UI Settings
        binding.tvStatus.setText("Ready");
        binding.vProgress.setVisibility(GONE);
        setClickListeners();

        // Pixlee Settings
        initPixleeAlbum();
    }

    private void initPixleeAlbum() {
        PXLClient client = PXLClient.Companion.getInstance(getContext());
        album = new PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client);
    }

    private void setClickListeners() {
        binding.btLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadLink();
            }
        });
        binding.btFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupExternalStoragePermission();
            }
        });
    }

    // Sample 1: Upload an image using a link
    private void uploadLink() {
        setUploadingUI(true);
        album.postMediaWithURI(
                "https://cdn.pixabay.com/photo/2017/01/17/10/39/italy-1986418_960_720.jpg",
                "uploaded from SDK-" + System.currentTimeMillis() + " using a image link",
                "xxx@xxx.com",
                "android sdk user",
                true,
                null,
                null,
                null,
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        showMessage("Upload Success: " + result);
                        setUploadingUI(false);
                    }

                    @Override
                    public void onError(String error) {
                        binding.tvStatus.setText(error);
                        setUploadingUI(false);
                    }
                });
    }

    // Sample 2: Upload an image using a file
    @Override
    public void uploadFile(String filePath) {
        showMessage("Uploading  " + filePath);
        setUploadingUI(true);

        JSONObject json = new JSONObject();
        try {
            json.put("name", "Donald Trump");
            json.put("age", 73);
            json.put("email", "b@b.com");
            JSONArray arr = new JSONArray();
            arr.put(10);
            arr.put(20);
            arr.put(35);
            json.put("points", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        album.uploadLocalImage(
                filePath,
                "uploaded from SDK-" + System.currentTimeMillis() + " using a file",
                "sungjun@pixleeteam.com",
                "jun",
                true,
                null,
                null,
                json,
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        showMessage("Uploading Succeeded: " + result);
                        setUploadingUI(false);
                    }

                    @Override
                    public void onError(String error) {
                        binding.tvStatus.setText(error);
                        setUploadingUI(false);
                    }
                });
    }

    private void setUploadingUI(boolean enabled) {
        binding.btFile.setEnabled(!enabled);
        binding.btLink.setEnabled(!enabled);
        binding.vProgress.setVisibility(enabled ? View.VISIBLE: View.GONE );
    }

    private void showMessage(String message) {
        binding.tvStatus.setText(message);
        showToast(message);
    }
}
