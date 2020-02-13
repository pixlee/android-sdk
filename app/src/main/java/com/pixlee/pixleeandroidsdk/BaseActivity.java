package com.pixlee.pixleeandroidsdk;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * This helps Fragments to be added with proper animation and change colors in the app.
 */
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
        t.commitAllowingStateLoss();
    }

    public void replaceFragmentInActivity(
            int frameId,
            Fragment fragment,
            View sharedView) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        changeFragmentInActivity(frameId, fragment, sharedView, 0,0,0,0);
    }

    /**
     * The `fragment` is added to the container view with proposalIndex. The operation is
     * performed by the `fragmentManager`.
     */

    public void addFragmentToActivity(
            int frameId,
            Fragment fragment,
            View sharedView) {

        changeFragmentInActivity(frameId, fragment, sharedView,
                R.anim.slide_in_right_left,
                R.anim.slide_out_right_left,
                R.anim.slide_in_left_right,
                R.anim.slide_out_left_right);
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

    public void setSystemBarLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = findViewById(android.R.id.content);
            int flags = view.getSystemUiVisibility();
            flags = flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public void setSystemBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    public void setSystemBarColor(@ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, colorRes));
        }
    }
}
