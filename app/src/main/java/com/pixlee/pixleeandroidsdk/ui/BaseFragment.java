package com.pixlee.pixleeandroidsdk.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pixlee.pixleeandroidsdk.MainActivity;

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
}
