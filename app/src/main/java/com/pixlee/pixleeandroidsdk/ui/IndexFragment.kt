package com.pixlee.pixleeandroidsdk.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pixlee.pixleeandroidsdk.MainActivity
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.data.LocalDataSource
import com.pixlee.pixleeandroidsdk.data.LocalRepository.Companion.getInstance
import com.pixlee.pixleeandroidsdk.databinding.FragmentIndexBinding
import com.pixlee.pixleeandroidsdk.ui.analytics.AnalyticsFragment
import com.pixlee.pixleeandroidsdk.ui.analytics.KtxAnalyticsFragment
import com.pixlee.pixleeandroidsdk.ui.gallery.GalleryFragment
import com.pixlee.pixleeandroidsdk.ui.gallery.KtxGalleryGridFragment
import com.pixlee.pixleeandroidsdk.ui.gallery.KtxGalleryListFragment
import com.pixlee.pixleeandroidsdk.ui.uioptions.ProductViewFragment
import com.pixlee.pixleeandroidsdk.ui.uploader.ImageUploaderFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.HotspotsActivity

/**
 * This is an index page of the app.
 *
 * Created by sungjun on 2020-02-13.
 */
class IndexFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.app_name
    }

    private var _binding: FragmentIndexBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndexBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val localDataSource: LocalDataSource by lazy {
        getInstance(requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initKtxButtons()
        initJavaButtons()
        initWidgetButtons()
    }

    fun initKtxButtons() {
        binding.btKtxAlbumList.setOnClickListener {
            addFragmentToActivity(KtxGalleryListFragment())
        }

        binding.btKtxAlbumGrid.setOnClickListener {
            addFragmentToActivity(KtxGalleryGridFragment())
        }

        binding.btKtxAnalytics.setOnClickListener {
            addFragmentToActivity(KtxAnalyticsFragment())
        }

        binding.btnProductView.setOnClickListener {
            addFragmentToActivity(ProductViewFragment())
        }

        binding.btnHotspots.setOnClickListener {
            context?.let { HotspotsActivity.launch(it) }
        }
    }

    fun initJavaButtons() {
        binding.btAlbum.setOnClickListener {
            addFragmentToActivity(GalleryFragment.getInstance(false))
        }

        binding.btImageUploader.setOnClickListener {
            addFragmentToActivity(ImageUploaderFragment())
        }

        binding.btAnalytics.setOnClickListener {
            addFragmentToActivity(AnalyticsFragment())
        }
    }

    fun initWidgetButtons() {
        localDataSource.getConfig().also {
            binding.switchDarkMode.isChecked = it.isDarkMode
        }

        binding.switchDarkMode.setOnClickListener {
            (localDataSource.getConfig()).also {
                it.isDarkMode = binding.switchDarkMode.isChecked
                localDataSource.setConfig(it)
                (activity as MainActivity).setConfig(it)
            }
        }
    }
}
