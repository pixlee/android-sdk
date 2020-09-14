package com.pixlee.pixleeandroidsdk.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pixlee.pixleeandroidsdk.MainActivity
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.data.Config
import com.pixlee.pixleeandroidsdk.data.LocalDataSource
import com.pixlee.pixleeandroidsdk.data.LocalRepository.Companion.getInstance
import com.pixlee.pixleeandroidsdk.databinding.FragmentIndexBinding
import com.pixlee.pixleeandroidsdk.ui.gallery.GalleryFragment
import com.pixlee.pixleeandroidsdk.ui.uploader.ImageUploaderFragment
import kotlinx.android.synthetic.main.fragment_index.*

/**
 * This is an index page of the app.
 *
 * Created by sungjun on 2020-02-13.
 */
class IndexFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.app_name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_index, container, false)
    }

    private val localDataSource: LocalDataSource by lazy {
        getInstance(context!!)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btAlbum.setOnClickListener {
            addFragmentToActivity(GalleryFragment.getInstance(false))
        }

        btImageUploader.setOnClickListener {
            addFragmentToActivity(ImageUploaderFragment())
        }

        btAnalytics.setOnClickListener {
            addFragmentToActivity(AnalyticsFragment())
        }

        btWidgets.setOnClickListener {
            addFragmentToActivity(GalleryFragment.getInstance(true))
        }

        btPhotoOnList.setOnClickListener {

        }

        localDataSource.getConfig().also {
            switchDarkMode.isChecked = it.isDarkMode
        }

        switchDarkMode.setOnClickListener {
            (localDataSource.getConfig()).also {
                it.isDarkMode = switchDarkMode.isChecked
                localDataSource.setConfig(it)
                (activity as MainActivity).setConfig(it)
            }
        }
    }
}
