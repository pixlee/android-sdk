package com.pixlee.pixleeandroidsdk;

import android.os.Build;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {
    private void changeFragmentInActivity(
            int frameId,
            Fragment fragment,
            View sharedView,
            int tranEnter, int tranExit, int tranPopEnter, int tranPopExit) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();

        if (sharedView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            t.addSharedElement(sharedView, sharedView.getTransitionName());
        else {
            t.setCustomAnimations(tranEnter, tranExit, tranPopEnter, tranPopExit);
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        String tag = fragment.getClass().getCanonicalName() != null ? fragment.getClass().getCanonicalName() : "";
        t.addToBackStack(tag);
        t.replace(frameId, fragment, tag);
    }

    public void replaceFragmentInActivity(
            int frameId,
            Fragment fragment,
            View sharedView,
            int tranEnter, int tranExit, int tranPopEnter, int tranPopExit) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        changeFragmentInActivity(frameId, fragment, sharedView, tranEnter, tranExit, tranPopEnter, tranPopExit);
    }

    /**
     * The `fragment` is added to the container view with proposalIndex. The operation is
     * performed by the `fragmentManager`.
     */
    public void addFragmentToActivity(
            int frameId,
            Fragment fragment,
            View sharedView) {

        changeFragmentInActivity(frameId, fragment, sharedView, 0, 0, 0, 0);
    }

    /**
     * The `fragment` is added to the container view with proposalIndex. The operation is
     * performed by the `fragmentManager`.
     */
    public void popAndAddFragmentToActivity(
            int frameId,
            Fragment fragment,
            View sharedView
    ) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();

        if (sharedView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            t.addSharedElement(sharedView, sharedView.getTransitionName());
        else {
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        String tag = fragment.getClass().getCanonicalName() != null ? fragment.getClass().getCanonicalName() : "";
        t.addToBackStack(tag);
        t.replace(frameId, fragment, tag);
    }

    /**
     * The `fragment` is restart on the container view with tag. The operation is
     * performed by the `fragmentManager`.
     */
    public void restartFragmentByTag(String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        Fragment f = fm.findFragmentByTag(fragmentTag);

        FragmentTransaction t = fm.beginTransaction();
        t.detach(f);
        t.attach(f);


    }
}
