package com.pixlee.pixleeandroidsdk.ui.gallery

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLRegion
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLLoading
import com.pixlee.pixleesdk.ui.widgets.list.BaseRecyclerView
import com.pixlee.pixleesdk.ui.widgets.list.PXLPhotoRecyclerView
import kotlinx.android.synthetic.main.module_search.*

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
abstract class BaseKtxGalleryFragment : BaseFragment() {
    abstract val viewModel: KtxGalleryViewModel
    abstract val _v_body: View
    abstract val _switchSound: SwitchMaterial?
    abstract val _pxlPhotoRecyclerView: BaseRecyclerView
    abstract val _fabFilter: FloatingActionButton
    abstract val _drawerLayout: DrawerLayout
    abstract val _lottieView: PXLLoading
    abstract fun initRecyclerView()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        radioGroupContentTypeVideo.isChecked = true

        _switchSound?.also{ switch ->
            switch?.isChecked = false
            switch?.setOnClickListener {

                if(_pxlPhotoRecyclerView is PXLPhotoRecyclerView){
                    val soundList = _pxlPhotoRecyclerView as PXLPhotoRecyclerView
                    if(switch.isChecked)
                        soundList.unmute()
                    else
                        soundList.mute()
                }

            }
        }

        viewModel.loadRegions()
        initRecyclerView()
        addViewModelListeners()
        initFilterClickListeners()
        configureViews()

        _v_body.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (_v_body == null)
                        return

                    val cellHeightInPixel = _v_body.measuredHeight * 0.5f
                    viewModel.cellHeightInPixel = cellHeightInPixel.toInt()
                    loadAlbum()
                    _v_body.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun addViewModelListeners() {
        viewModel.regions.observe(this, Observer {
            when (it) {
                is BaseViewModel.RegionCommand.NoRegion -> {
                    vRegionSingle.visibility = View.VISIBLE
                    vRegionSingle.isEnabled = true
                    spinnerRegion.visibility = View.GONE
                }

                is BaseViewModel.RegionCommand.Loading -> {
                    vRegionSingle.visibility = View.VISIBLE
                    vRegionSingle.isEnabled = false
                    spinnerRegion.visibility = View.GONE
                }

                is BaseViewModel.RegionCommand.Data -> {
                    vRegionSingle.visibility = View.GONE
                    spinnerRegion.visibility = View.VISIBLE

                    context?.also{ctx->
                        val spinnerArrayAdapter: ArrayAdapter<PXLRegion> = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, it.list)
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerRegion.adapter = spinnerArrayAdapter
                        spinnerRegion.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }


                }
            }
        })

        viewModel.loading.observe(this, Observer {
            _lottieView.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.searchResultEvent.observe(this, EventObserver {
            when (it) {
                is BaseViewModel.Command.Data -> {
                    if (it.isFirstPage) {
                        _pxlPhotoRecyclerView.replaceList(it.list)

                        if(_pxlPhotoRecyclerView is PXLPhotoRecyclerView){
                            val soundList = _pxlPhotoRecyclerView as PXLPhotoRecyclerView
                            soundList.playVideoOnResume()
                        }

                        if (it.list.isNotEmpty()) {
                            it.list.firstOrNull()?.pxlPhoto?.also {
                                viewModel.getPhotoFromRegion(it, readRegionIdFromUI()) // add your own region id
                            }
                        }

                        // if no result in the first page, open search panel so that the SDK developers will try out different filters
                        if (it.list.isEmpty()) {
                            Toast.makeText(context, "success!! but you got an empty list.\nwhat about trying different searching options here?", Toast.LENGTH_LONG).show()
                            _drawerLayout.openDrawer(GravityCompat.END)
                        }
                    } else {
                        _pxlPhotoRecyclerView.addList(it.list)
                    }

                }
            }
        })
    }

    fun initFilterClickListeners() {
        // set filter buttons
        _fabFilter.setOnClickListener { _drawerLayout.openDrawer(GravityCompat.END) }
        btnCloseFilter.setOnClickListener { _drawerLayout.closeDrawer(GravityCompat.END) }
        btnApply.setOnClickListener {
            _drawerLayout.closeDrawer(GravityCompat.END)
            loadAlbum()
        }
    }

    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private fun loadAlbum() {

        context?.also {
            var searchId: PXLKtxBaseAlbum.SearchId? = null
            // initiate album
            for (i in 0 until radioGroupAlbum.childCount) {
                val rb = radioGroupAlbum.getChildAt(i) as MaterialRadioButton
                if (radioGroupAlbum.checkedRadioButtonId == rb.id) {
                    val text = rb.text.toString()
                    if (text == getString(R.string.radio_album)) {
                        searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID)
                    } else if (text == getString(R.string.radio_pdp)) {
                        searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU)
                    }
                    break
                }
            }

            if (searchId == null) {
                // if album is not properly started, stop loading it.
                showDialog("No Album", "Album is not properly set. Please check the code and try again")
            } else {
                // set GET request parameters for the API
                viewModel.init(PXLKtxBaseAlbum.Params(
                        searchId = searchId,
                        perPage = readPerPage(),
                        filterOptions = readFilterOptionsFromUI(),
                        sortOptions = readSortOptionsFromUI(),
                        regionId = readRegionIdFromUI()
                ))
                PXLAlbumSortOptions().apply {
                    sortType = PXLAlbumSortType.RECENCY
                    descending = true
                }

                // retrieve the first page
                viewModel.getFirstPage()
            }
        }
    }

    private fun configureViews() {
        if(_pxlPhotoRecyclerView is PXLPhotoRecyclerView){
            val soundList = _pxlPhotoRecyclerView as PXLPhotoRecyclerView
            soundList.addOnScrollListener(object :
                    androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(
                        recyclerView: androidx.recyclerview.widget.RecyclerView,
                        dx: Int,
                        dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    soundList.linearLayoutManager.apply {
                        viewModel.listScrolled(childCount, findLastVisibleItemPosition(), itemCount)
                    }
                }
            })
        }

    }

    fun readPerPage(): Int {
        // Set textViewPerPage filter if text is not empty
        val minTwitterFollowers = textViewPerPage.text.toString()
        return if (!minTwitterFollowers.isEmpty()) {
            Integer.valueOf(minTwitterFollowers)
        } else 20

        // a default for perPage
    }

    fun readRegionIdFromUI(): Int?{
        if(spinnerRegion.visibility==View.VISIBLE){
            val regions = viewModel.regions.value
            return when(regions){
                is BaseViewModel.RegionCommand.Data -> {
                    regions.list[spinnerRegion.selectedItemPosition].id
                }
                else ->{
                   null
                }
            }
        }

        val data = textViewRegionId.text.toString()
        return if (data.isNotEmpty()) {
            Integer.valueOf(data)
        } else null
    }

    fun readSortOptionsFromUI(): PXLAlbumSortOptions {
        val sortOptions = PXLAlbumSortOptions()
        // Set sortType filter if a radio button is selected
        for (i in 0 until radioGroupSortType.childCount) {
            val rb = radioGroupSortType.getChildAt(i) as MaterialRadioButton
            if (radioGroupSortType.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == PXLAlbumSortType.RECENCY.value) sortOptions.sortType = PXLAlbumSortType.RECENCY
                else if (text == PXLAlbumSortType.APPROVED_TIME.value) sortOptions.sortType = PXLAlbumSortType.APPROVED_TIME
                else if (text == PXLAlbumSortType.RANDOM.value) sortOptions.sortType = PXLAlbumSortType.RANDOM
                else if (text == PXLAlbumSortType.PIXLEE_SHARES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_SHARES
                else if (text == PXLAlbumSortType.PIXLEE_LIKES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_LIKES
                else if (text == PXLAlbumSortType.POPULARITY.value) sortOptions.sortType = PXLAlbumSortType.POPULARITY
                else if (text == PXLAlbumSortType.DYNAMIC.value) sortOptions.sortType = PXLAlbumSortType.DYNAMIC
                break
            }
        }

        // Set sorting direction
        if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionASC.id) sortOptions.descending = false
        else if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionDESC.id) sortOptions.descending = true
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
    fun moveToViewer(photo: PhotoWithImageScaleType) {
        val list = listOf(PhotoLauncher.ViewerActivity, PhotoLauncher.PXLPhotoView/*, PhotoLauncher.PXLPhotoViewInRecyclerView*/)

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
