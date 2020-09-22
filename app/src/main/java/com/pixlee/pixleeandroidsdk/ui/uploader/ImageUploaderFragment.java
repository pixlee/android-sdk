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
import com.pixlee.pixleesdk.data.PXLPhoto;
import com.pixlee.pixleesdk.data.MediaResult;

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
        setPixleeCredentials();
        initPixleeAlbum();


    }

    public void setPixleeCredentials() {
        PXLClient.Companion.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);
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
                "uploaded from SDK-" + System.currentTimeMillis() + " using a image link",
                "sungjun@pixleeteam.com",
                "android sdk user",
                "https://cdn.pixabay.com/photo/2017/01/17/10/39/italy-1986418_960_720.jpg",
                true,
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
    private void uploadFile(String filePath) {
        showMessage("Uploading  " + filePath);
        setUploadingUI(true);
        album.uploadLocalImage(
                "uploaded from SDK-" + System.currentTimeMillis() + " using a file",
                "sungjun@pixleeteam.com",
                "jun",
                true,
                filePath,
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


    void callMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, REQ_MEDIA_PICKER);
    }

    private final int REQ_MEDIA_PICKER = 1314;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQ_MEDIA_PICKER) {
                extractImage(data);
            }
        }
    }

    void extractImage(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};
        if (selectedImage != null) {
            Cursor cursor = null;
            try {
                cursor = getContext().getContentResolver().query(
                        selectedImage,
                        filePathColumn, null, null, null
                );
                if (cursor != null) {
                    cursor.moveToFirst();

                    String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    uploadFile(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
    }

    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

    private void setupExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            callMediaPicker();
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{permission},
                    reqStorage
            );
        }

    }

    /**
     * Put any random number for reqStorage in ActivityCompat.requestPermissions
     * Example:
     * ActivityCompat.requestPermissions(
     * getActivity(),
     * new String[]{permission},
     * reqStorage
     * );
     * <p>
     * Try to filter requestCode with the same number inside onRequestPermissionsResult(int requestCode).
     * This will aloow you to receive the result about permission
     */
    private final int reqStorage = 1729;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == reqStorage) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMediaPicker();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0]);
                if (!showRationale) {
                    createDialogForStoragePermission();
                }

            }
        }
    }

    private void createDialogForStoragePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.storage_permission_for_uploading);
        builder.setPositiveButton(R.string.button_setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getActivity().getPackageName()))
                );
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }


    AlertDialog loadingDialog;

    void makeLoading(Boolean show) {
        if (show) {
            if (loadingDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.dialog_progress, null);
                builder.setView(dialogLayout);
                builder.setCancelable(false);
                loadingDialog = builder.show();
            } else {
                loadingDialog.show();
            }

        } else {
            if (loadingDialog != null && loadingDialog.isShowing())
                loadingDialog.dismiss();
        }
    }
}
