package com.pixlee.pixleeandroidsdk.ui.uploader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
                setupExternalStoragePermission();
            }
        });
    }

    void uploadImage(String filePath, String contentType) {
        showAnalytics("uploadLocalImage(filePath: " + filePath + ", contentType: " + contentType + ")");
        /*album.uploadLocalImage(
                "yosemite",
                "sungjun.app@gmail.com",
                "jun",
                filePath,
                true,
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        showAnalytics("Upload Success: " + result);

                    }

                    @Override
                    public void onError(String error) {
                        tv_status.setText(error);
                    }
                });*/
    }

    private void showAnalytics(String methodName) {
        String message = getString(R.string.xxx_is_called, methodName);
        tv_status.setText(message);
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
                    String mimeType = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
                    uploadImage(filePath, mimeType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
    }

    private void setupExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            ) {
                callMediaPicker();
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        reqStorage
                );
            }
        }

    }

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
        if (builder != null) {
            builder.setMessage(R.string.storage_permission_for_uploading);
            builder.setPositiveButton(R.string.button_setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(
                            new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${activity?.packageName}"))
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