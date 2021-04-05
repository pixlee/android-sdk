package com.pixlee.pixleeandroidsdk.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.pixlee.pixleeandroidsdk.MainActivity;
import com.pixlee.pixleeandroidsdk.R;

/**
 * This helps Fragments and Dialogs to be loaded
 */
abstract public class BaseFragment extends Fragment {
    //this is to display a title in Toolbar
    public abstract int getTitleResource();

    public boolean isBackInUse(){
        return false;
    }

    //this is to display a a custom title in Toolbar. This has a higher priority over getTitleResource()
    public String getCustomTitle() {
        return null;
    }

    public void replaceFragmentInActivity(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            mainActivity.replaceFragmentInActivity(mainActivity.frameLayoutId, fragment, null);
        } else {
            Toast.makeText(getContext(), "need to add replaceFragmentInActivity() to your activity", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFragmentToActivity(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            mainActivity.addFragmentToActivity(mainActivity.frameLayoutId, fragment, null);
        } else {
            Toast.makeText(getContext(), "need to add addFragmentToActivity() to your activity", Toast.LENGTH_SHORT).show();
        }
    }

    Toast toast;
    public void showToast(String message) {
        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        if (title != null) b.setTitle(title);
        if (message != null) b.setMessage(message);

        b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        b.show();

    }


    void callMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/* video/*");
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

    // override this method in your fragment
    public void uploadFile(String filePath) {

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

    public void setupExternalStoragePermission() {
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
