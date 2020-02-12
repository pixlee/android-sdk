package com.pixlee.pixleeandroidsdk;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    final String TAG = "MainActivity";
    int frameLayoutId = R.id.contentFrame;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_60),
                    PorterDuff.Mode.SRC_ATOP
            );
        }

        setSystemBarColor(R.color.grey_5);
        setSystemBarLight();

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(onBackStackChangedListener);

        replaceFragmentInActivity(frameLayoutId, new IndexFragment(), null);

    }

    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            FragmentManager fm = getSupportFragmentManager();
            int fragmentCount = fm.getBackStackEntryCount();
            String title;
            if (fragmentCount > 0) {
                BaseFragment fragment = (BaseFragment) fm.getFragments().get(fm.getFragments().size() - 1);
                if (fragment.getCustomTitle() != null) {
                    title = fragment.getCustomTitle();
                } else {
                    title = getString(fragment.getTitleResource());
                }
            } else {
                title = getString(R.string.app_name);
            }

            Log.d(TAG, "fragmentCount: " + fragmentCount);

            setSupportActionBar(toolbar);
            ActionBar bar = getSupportActionBar();
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(fragmentCount > 1);
            bar.setDisplayShowHomeEnabled(fragmentCount > 1);

            invalidateOptionsMenu();
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            int fragmentStackSize = getSupportFragmentManager().getBackStackEntryCount();
            if (fragmentStackSize <= 1) {
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }
}
