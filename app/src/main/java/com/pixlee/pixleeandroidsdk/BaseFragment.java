package com.pixlee.pixleeandroidsdk;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
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
}
