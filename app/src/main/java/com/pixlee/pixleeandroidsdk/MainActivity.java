package com.pixlee.pixleeandroidsdk;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.pixlee.pixleeandroidsdk.data.Config;
import com.pixlee.pixleeandroidsdk.data.LocalRepository;
import com.pixlee.pixleeandroidsdk.databinding.ActivityMainBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleeandroidsdk.ui.IndexFragment;

/**
 * This activity only manage Fragments and a Toolbar.
 */
public class MainActivity extends BaseActivity {
    final String TAG = "MainActivity";
    public int frameLayoutId = R.id.contentFrame;

    public ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (binding.toolbar.getNavigationIcon() != null) {
            binding.toolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_60),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
        Log.e("ma", "getString(R.string.was_old_price, \"10\"): " + getString(R.string.was_old_price, "10"));
        Log.e("ma", "getString(R.string.was_old_price, \"10\"): " + getString(R.string.percent_off, "10"));

        setConfig(LocalRepository.Companion.getInstance(this).getConfig());

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(onBackStackChangedListener);

        replaceFragmentInActivity(frameLayoutId, new IndexFragment(), null);

    }

    public void setConfig(Config config){
        if(config!=null && config.isDarkMode()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setSystemBarColor(R.color.grey_5);
            setSystemBarLight();
        }
    }

    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            FragmentManager fm = getSupportFragmentManager();
            int fragmentCount = fm.getFragments().size();
            String title = "";
            if (fragmentCount > 0) {
                if(fm.getFragments().get(fragmentCount - 1) instanceof BaseFragment){
                    BaseFragment fragment = (BaseFragment) fm.getFragments().get(fragmentCount - 1);
                    if (fragment.getCustomTitle() != null) {
                        title = fragment.getCustomTitle();
                    } else {
                        title = getString(fragment.getTitleResource());
                    }
                }
            } else {
                title = getString(R.string.app_name);
            }

            Log.d(TAG, "fragmentCount: " + fragmentCount);

            setSupportActionBar(binding.toolbar);
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
            BaseFragment baseFragment = getCurrentFragment();
            if (baseFragment != null && baseFragment.isBackInUse()) {
                return;
            }

            if (fragmentStackSize <= 1) {
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }
}
