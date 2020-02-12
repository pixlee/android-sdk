package com.pixlee.pixleeandroidsdk;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

abstract public class BaseFragment extends Fragment {
    //this is to display a title in Toolbar
    public abstract int getTitleResource();

    //this is to display a a custom title in Toolbar. This has a higher priority over getTitleResource()
    String getCustomTitle(){
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
            Toast.makeText(getContext(), "need to add addFragmentToActivity() to your activity", Toast.LENGTH_SHORT).show();;
        }
    }

    Toast toast;
    public void showToast(String message){
        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
