package com.pixlee.pixleeandroidsdk.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.radiobutton.MaterialRadioButton
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewInRecyclerViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import kotlinx.android.synthetic.main.fragment_ktx_gallery.*
import kotlinx.android.synthetic.main.module_search.*

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
class KtxGalleryFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_ktx_album
    }

    val viewModel: KtxGalleryViewModel by lazy {
        // get PXLClient
        val client = PXLClient.getInstance(context!!)
        KtxGalleryViewModel(client.ktxBasicRepo, client.ktxAnalyticsRepo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ktx_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addViewModelListeners()
        addClickListeners()
        configureViews()

        pxlPhotoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlPhotoRecyclerView == null)
                        return

                    val cellHeightInPixel = pxlPhotoRecyclerView.measuredHeight / 2
                    viewModel.cellHeightInPixel = cellHeightInPixel
                    loadAlbum()
                    pxlPhotoRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    override fun onStop() {
        super.onStop()
        pxlPhotoRecyclerView.onStop()
    }

    fun addViewModelListeners() {
        viewModel.loading.observe(this, Observer {
            lottieView.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.resultEvent.observe(this, EventObserver {
            when (it) {
                is KtxGalleryViewModel.Command.Data -> {
                    if(it.isFistPage) {
                        pxlPhotoRecyclerView.replaceList(it.list)
                        pxlPhotoRecyclerView.onResume()
                    } else {
                        pxlPhotoRecyclerView.addList(it.list)
                    }

                }
            }
        })
    }

    fun addClickListeners() {
        pxlPhotoRecyclerView.setOnItemClicked {
            context?.also { ctx ->
                moveToViewer(it)
            }
        }
        // set filter buttons
        fabFilter.setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }
        btnCloseFilter.setOnClickListener { drawerLayout.closeDrawer(GravityCompat.END) }
        btnApply.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            loadAlbum()
        }
    }

    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private fun loadAlbum() {

        context?.also {
            var searchSetting: KtxGalleryViewModel.SearchSetting? = null
            // initiate album
            for (i in 0 until radioGroupAlbum.childCount) {
                val rb = radioGroupAlbum.getChildAt(i) as MaterialRadioButton
                if (radioGroupAlbum.checkedRadioButtonId == rb.id) {
                    val text = rb.text.toString()
                    if (text == getString(R.string.radio_album)) {
                        searchSetting = KtxGalleryViewModel.SearchSetting.Album(BuildConfig.PIXLEE_ALBUM_ID)
                    } else if (text == getString(R.string.radio_pdp)) {
                        searchSetting = KtxGalleryViewModel.SearchSetting.Album(BuildConfig.PIXLEE_SKU)
                    }
                    break
                }
            }

            if (searchSetting == null) {
                // if album is not properly started, stop loading it.
                showDialog("No Album", "Album is not properly set. Please check the code and try again")
            } else {
                // set GET request parameters for the API
                viewModel.init(searchSetting)
                viewModel.perPage = readPerPage()
                viewModel.filterOptions = readFilterOptionsFromUI()
                viewModel.sortOptions = readSortOptionsFromUI()
                viewModel.getFirstPage()
            }
        }

    }

    private fun configureViews() {
        pxlPhotoRecyclerView.linearLayoutManager
        pxlPhotoRecyclerView.addOnScrollListener(object :
                androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    dx: Int,
                    dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                pxlPhotoRecyclerView.linearLayoutManager.apply {
                    viewModel.listScrolled(childCount, findLastVisibleItemPosition(), itemCount)
                }

            }
        })
    }

    fun readPerPage(): Int {
        // Set textViewPerPage filter if text is not empty
        val minTwitterFollowers = textViewPerPage.text.toString()
        return if (!minTwitterFollowers.isEmpty()) {
            Integer.valueOf(minTwitterFollowers)
        } else 20

        // a default for perPage
    }

    fun readSortOptionsFromUI(): PXLAlbumSortOptions {
        val sortOptions = PXLAlbumSortOptions()
        // Set sortType filter if a radio button is selected
        for (i in 0 until radioGroupSortType.childCount) {
            val rb = radioGroupSortType.getChildAt(i) as MaterialRadioButton
            if (radioGroupSortType.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == PXLAlbumSortType.RECENCY.value) sortOptions.sortType = PXLAlbumSortType.RECENCY else if (text == PXLAlbumSortType.APPROVED_TIME.value) sortOptions.sortType = PXLAlbumSortType.APPROVED_TIME else if (text == PXLAlbumSortType.RANDOM.value) sortOptions.sortType = PXLAlbumSortType.RANDOM else if (text == PXLAlbumSortType.PIXLEE_SHARES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_SHARES else if (text == PXLAlbumSortType.PIXLEE_LIKES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_LIKES else if (text == PXLAlbumSortType.POPULARITY.value) sortOptions.sortType = PXLAlbumSortType.POPULARITY else if (text == PXLAlbumSortType.DYNAMIC.value) sortOptions.sortType = PXLAlbumSortType.DYNAMIC
                break
            }
        }

        // Set sorting direction
        if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionASC.id) sortOptions.descending = false else if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionDESC.id) sortOptions.descending = true
        return sortOptions
    }

    fun readFilterOptionsFromUI(): PXLAlbumFilterOptions {
        val filterOptions = PXLAlbumFilterOptions()

        // Set minTwitterFollowers filter if text is not empty
        val minTwitterFollowers = textViewMinTwitterFollowers.text.toString()
        if (!minTwitterFollowers.isEmpty()) {
            filterOptions.minTwitterFollowers = Integer.valueOf(minTwitterFollowers)
        }

        // Set minInstagramFollowers filter if text is not empty
        val minInstagramFollowers = textViewMinInstagramFollowers.text.toString()
        if (!minInstagramFollowers.isEmpty()) {
            filterOptions.minInstagramFollowers = Integer.valueOf(minInstagramFollowers)
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until radioGroupHasPermission.childCount) {
            val rb = radioGroupHasPermission.getChildAt(i) as MaterialRadioButton
            if (radioGroupHasPermission.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasPermission = false else if (text == getString(R.string.radio_true)) filterOptions.hasPermission = true
                break
            }
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until radioGroupHasProduct.childCount) {
            val rb = radioGroupHasProduct.getChildAt(i) as MaterialRadioButton
            if (radioGroupHasProduct.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasProduct = false else if (text == getString(R.string.radio_true)) filterOptions.hasProduct = true
                break
            }
        }

        // Set inStockOnly filter if false or not true is set
        for (i in 0 until radioGroupInStockOnly.childCount) {
            val rb = radioGroupInStockOnly.getChildAt(i) as MaterialRadioButton
            if (radioGroupInStockOnly.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.inStockOnly = false else if (text == getString(R.string.radio_true)) filterOptions.inStockOnly = true
                break
            }
        }

        // Set contentSource filter if any of its check boxes is selected
        val contentSource: ArrayList<PXLContentSource> = ArrayList()
        if (radioGroupContentSourceInstagramFeed.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_FEED)
        if (radioGroupContentSourceInstagramStory.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_STORY)
        if (radioGroupContentSourceTwitter.isChecked) contentSource.add(PXLContentSource.TWITTER)
        if (radioGroupContentSourceFacebook.isChecked) contentSource.add(PXLContentSource.FACEBOOK)
        if (radioGroupContentSourceApi.isChecked) contentSource.add(PXLContentSource.API)
        if (radioGroupContentSourceDesktop.isChecked) contentSource.add(PXLContentSource.DESKTOP)
        if (radioGroupContentSourceEmail.isChecked) contentSource.add(PXLContentSource.EMAIL)
        if (contentSource.isNotEmpty()) filterOptions.contentSource = contentSource

        // Set contentType filter if any of its check boxes is selected
        val contentType: ArrayList<PXLContentType> = ArrayList()
        if (radioGroupContentTypeImage.isChecked) contentType.add(PXLContentType.IMAGE)
        if (radioGroupContentTypeVideo.isChecked) contentType.add(PXLContentType.VIDEO)
        if (contentType.isNotEmpty()) filterOptions.contentType = contentType

        // Apart from the examples above, there are more filters you can implement in you app.
        // These are the example codes

        // ###### date filter examples ######
        // fo.submittedDateEnd = new Date(2019, 7, 16);
        // fo.submittedDateStart = new Date(2019, 7, 17);
        // fo.filterByRadius = "21.3069,-157.8583,20";  radius filter example

        // ###### in_categories filter example ######
        // ArrayList incategories = new ArrayList<Integer>();
        // incategories.add(1234);
        // incategories.add(5678);
        // fo.inCategories = incategories;

        // ###### filter_by_userhandle filter example ######
        // HashMap userHandleFilter = new HashMap<String, Object> ();
        // userHandleFilter.put("contains", new String[] {"test1", "test2"});
        // fo.filterByUserhandle = userHandleFilter;

        // ###### computer_vision filter example ######
        // HashMap computerVisionFilter = new HashMap<String, Object> ();
        // computerVisionFilter.put("contains", new String[] {"hat"});
        // fo.computerVision = computerVisionFilter;
        return filterOptions
    }


    enum class PhotoLauncher(@StringRes val stringRes: Int) {
        ViewerActivity(R.string.title_pxlphoto_activity),
        PXLPhotoView(R.string.title_pxlphotoview),
        PXLPhotoViewInRecyclerView(R.string.title_pxlphotoview_in_recyclerview)
    }

    /**
     * When an item in the list is clicked, this method call Video player or Image viewer using PXLPhoto.isVideo()
     *
     * @param photo
     */
    fun moveToViewer(photo: PXLPhoto) {
        val list = listOf(PhotoLauncher.ViewerActivity, PhotoLauncher.PXLPhotoView, PhotoLauncher.PXLPhotoViewInRecyclerView)

        val listTexts = arrayOfNulls<String>(list.size)
        for (i in list.indices) {
            listTexts[i] = getString(list[i].stringRes)
        }

        context?.also {
            class ItemSelected(var position: Int = 0)
            val itemSelected = ItemSelected()
            androidx.appcompat.app.AlertDialog.Builder(it)
                    .setTitle(getString(R.string.galleryPhotoLauncherMessage))
                    .setSingleChoiceItems(listTexts, itemSelected.position) { dialog, which ->
                        itemSelected.position = which
                    }
                    .setPositiveButton(R.string.next) { dialog, which ->
                        when (list[itemSelected.position]) {
                            PhotoLauncher.ViewerActivity -> ViewerActivity.launch(it, photo)
                            PhotoLauncher.PXLPhotoView -> addFragmentToActivity(PXLPhotoViewFragment.getInstance(photo))
                        }
                    }
                    //.setNegativeButton(getString(R.string.dialog_cancel), null)
                    .show()
        }
        //PXLPhotoViewerActivity.launch(context!!, photo)
    }
}
