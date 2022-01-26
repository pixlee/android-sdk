package com.pixlee.pixleeandroidsdk.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.databinding.FragmentGalleryBinding
import com.pixlee.pixleeandroidsdk.databinding.ModuleSearchBinding
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewInRecyclerViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLAlbum
import com.pixlee.pixleesdk.client.PXLBaseAlbum
import com.pixlee.pixleesdk.client.PXLBaseAlbum.RequestHandlers
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLPdpAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.px

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
class GalleryFragment : BaseFragment(), RequestHandlers<ArrayList<PXLPhoto>?> {
    override fun getTitleResource(): Int {
        return R.string.title_java_album
    }

    private var album: PXLBaseAlbum? = null
    private var photoList: ArrayList<PXLPhoto>? = null
    private var lastImg = R.drawable.grid_2x
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        photoList = ArrayList()
    }

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // set filter buttons
        binding.moduleSearchLayout.radioGroupContentTypeVideo.isChecked = false

        binding.fabFilter.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.END) }
        binding.moduleSearchLayout.btnCloseFilter.setOnClickListener { binding.drawerLayout.closeDrawer(GravityCompat.END) }
        binding.moduleSearchLayout.btnApply.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            loadAlbum()
        }
        binding.gridToggleButton.setImageResource(lastImg)
        binding.gridToggleButton.setOnClickListener {
            binding.viewSwitcher.showNext()
            lastImg = if (lastImg == R.drawable.grid_2x) {
                R.drawable.column_2x
            } else {
                R.drawable.grid_2x
            }
            binding.gridToggleButton.setImageResource(lastImg)
        }
        if (gridAdapter == null) {
            loadAlbum()
        }
        configureViews()
    }

    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private fun loadAlbum() {
        context?.also{
            // set credentials for the SDK
            PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY)

            // get PXLClient
            val client = PXLClient.getInstance(it)

            // initiate album
            for (i in 0 until binding.moduleSearchLayout.radioGroupAlbum.childCount) {
                val rb = binding.moduleSearchLayout.radioGroupAlbum.getChildAt(i) as MaterialRadioButton
                if (binding.moduleSearchLayout.radioGroupAlbum.checkedRadioButtonId == rb.id) {
                    val text = rb.text.toString()
                    if (text == getString(R.string.radio_album)) album = PXLAlbum(BuildConfig.PIXLEE_ALBUM_ID, client) else if (text == getString(R.string.radio_pdp)) album = PXLPdpAlbum(BuildConfig.PIXLEE_SKU, client.basicDataSource, client.analyticsDataSource)
                    break
                }
            }

            // if album is not properly started, stop loading it.
            if (album == null) {
                showDialog("No Album", "Album is not properly set. Please check the code and try again")
                return
            }

            // show a loading UI on the mobile screen
            setLoading(true)

            // set GET request parameters for the API
            album?.setPerPage(readPerPage())
            album?.setFilterOptions(readFilterOptionsFromUI())
            album?.setSortOptions(readSortOptionsFromUI())
            album?.setRegionId(readRegionIdFromUI())

            // start requesting the API
            album?.loadNextPageOfPhotos(this)
        }

    }

    fun readPerPage(): Int {
        // Set textViewPerPage filter if text is not empty
        val minTwitterFollowers = binding.moduleSearchLayout.textViewPerPage.text.toString()
        return if (!minTwitterFollowers.isEmpty()) {
            Integer.valueOf(minTwitterFollowers)
        } else 20

        // a default for perPage
    }

    fun readRegionIdFromUI(): Int?{
        val data = binding.moduleSearchLayout.textViewRegionId.text.toString()
        return if (data.isNotEmpty()) {
            Integer.valueOf(data)
        } else null
    }

    fun readSortOptionsFromUI(): PXLAlbumSortOptions {
        val sortOptions = PXLAlbumSortOptions()
        // Set sortType filter if a radio button is selected
        for (i in 0 until binding.moduleSearchLayout.radioGroupSortType.childCount) {
            val rb = binding.moduleSearchLayout.radioGroupSortType.getChildAt(i) as MaterialRadioButton
            if (binding.moduleSearchLayout.radioGroupSortType.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == PXLAlbumSortType.RECENCY.value) sortOptions.sortType = PXLAlbumSortType.RECENCY else if (text == PXLAlbumSortType.APPROVED_TIME.value) sortOptions.sortType = PXLAlbumSortType.APPROVED_TIME else if (text == PXLAlbumSortType.RANDOM.value) sortOptions.sortType = PXLAlbumSortType.RANDOM else if (text == PXLAlbumSortType.PIXLEE_SHARES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_SHARES else if (text == PXLAlbumSortType.PIXLEE_LIKES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_LIKES else if (text == PXLAlbumSortType.POPULARITY.value) sortOptions.sortType = PXLAlbumSortType.POPULARITY else if (text == PXLAlbumSortType.DYNAMIC.value) sortOptions.sortType = PXLAlbumSortType.DYNAMIC
                break
            }
        }

        // Set sorting direction
        if (binding.moduleSearchLayout.radioGroupSortDirection.checkedRadioButtonId == binding.moduleSearchLayout.radioGroupSortDirectionASC.id) sortOptions.descending = false else if (binding.moduleSearchLayout.radioGroupSortDirection.checkedRadioButtonId == binding.moduleSearchLayout.radioGroupSortDirectionDESC.id) sortOptions.descending = true
        return sortOptions
    }

    fun readFilterOptionsFromUI(): PXLAlbumFilterOptions {
        val filterOptions = PXLAlbumFilterOptions()

        // Set minTwitterFollowers filter if text is not empty
        val minTwitterFollowers = binding.moduleSearchLayout.textViewMinTwitterFollowers.text.toString()
        if (!minTwitterFollowers.isEmpty()) {
            filterOptions.minTwitterFollowers = Integer.valueOf(minTwitterFollowers)
        }

        // Set minInstagramFollowers filter if text is not empty
        val minInstagramFollowers = binding.moduleSearchLayout.textViewMinInstagramFollowers.text.toString()
        if (!minInstagramFollowers.isEmpty()) {
            filterOptions.minInstagramFollowers = Integer.valueOf(minInstagramFollowers)
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until binding.moduleSearchLayout.radioGroupHasPermission.childCount) {
            val rb = binding.moduleSearchLayout.radioGroupHasPermission.getChildAt(i) as MaterialRadioButton
            if (binding.moduleSearchLayout.radioGroupHasPermission.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasPermission = false else if (text == getString(R.string.radio_true)) filterOptions.hasPermission = true
                break
            }
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until binding.moduleSearchLayout.radioGroupHasProduct.childCount) {
            val rb = binding.moduleSearchLayout.radioGroupHasProduct.getChildAt(i) as MaterialRadioButton
            if (binding.moduleSearchLayout.radioGroupHasProduct.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasProduct = false else if (text == getString(R.string.radio_true)) filterOptions.hasProduct = true
                break
            }
        }

        // Set inStockOnly filter if false or not true is set
        for (i in 0 until binding.moduleSearchLayout.radioGroupInStockOnly.childCount) {
            val rb = binding.moduleSearchLayout.radioGroupInStockOnly.getChildAt(i) as MaterialRadioButton
            if (binding.moduleSearchLayout.radioGroupInStockOnly.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.inStockOnly = false else if (text == getString(R.string.radio_true)) filterOptions.inStockOnly = true
                break
            }
        }

        // Set contentSource filter if any of its check boxes is selected
        val contentSource: ArrayList<PXLContentSource> = ArrayList()
        if (binding.moduleSearchLayout.radioGroupContentSourceInstagramFeed.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_FEED)
        if (binding.moduleSearchLayout.radioGroupContentSourceInstagramStory.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_STORY)
        if (binding.moduleSearchLayout.radioGroupContentSourceTwitter.isChecked) contentSource.add(PXLContentSource.TWITTER)
        if (binding.moduleSearchLayout.radioGroupContentSourceFacebook.isChecked) contentSource.add(PXLContentSource.FACEBOOK)
        if (binding.moduleSearchLayout.radioGroupContentSourceApi.isChecked) contentSource.add(PXLContentSource.API)
        if (binding.moduleSearchLayout.radioGroupContentSourceDesktop.isChecked) contentSource.add(PXLContentSource.DESKTOP)
        if (binding.moduleSearchLayout.radioGroupContentSourceEmail.isChecked) contentSource.add(PXLContentSource.EMAIL)
        if (contentSource.isNotEmpty()) filterOptions.contentSource = contentSource

        // Set contentType filter if any of its check boxes is selected
        val contentType: ArrayList<PXLContentType> = ArrayList()
        if (binding.moduleSearchLayout.radioGroupContentTypeImage.isChecked) contentType.add(PXLContentType.IMAGE)
        if (binding.moduleSearchLayout.radioGroupContentTypeVideo.isChecked) contentType.add(PXLContentType.VIDEO)
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

    fun setLoading(visible: Boolean) {
        binding.lottieView?.also{
            if (visible) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    class ListMode(var isGridMode: Boolean = true)

    val listMode: ListMode by lazy {
        ListMode()
    }
    var gridAdapter: GridAdapter? = null
    var listAdapter: ListAdapter? = null
    private fun configureViews() {
        binding.gridView.setHasFixedSize(true)
        binding.listView.setHasFixedSize(true)
        val gridLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext().applicationContext, 2)
        val listLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext().applicationContext, 1)
        binding.gridView.layoutManager = gridLayoutManager
        binding.listView.layoutManager = listLayoutManager
        val li = GalleryClickListener { view, photo -> moveToViewer(view, photo) }
        if (gridAdapter == null) {
            gridAdapter = GridAdapter(requireContext().applicationContext, photoList, li)
            listAdapter = ListAdapter(requireContext().applicationContext, photoList, li)
            menuList?.apply { findItem(R.id.action_live_list).isVisible = false }
        }else{
            menuList?.apply { findItem(R.id.action_live_list).isVisible = true }
        }
        binding.gridView.adapter = gridAdapter
        binding.listView.adapter = listAdapter
        val gridScrollListener: RecyclerViewEndlessScrollListener = object : RecyclerViewEndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMorePhotos()
            }
        }
        val listScrollListener: RecyclerViewEndlessScrollListener = object : RecyclerViewEndlessScrollListener(listLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMorePhotos()
            }
        }
        binding.gridView.addOnScrollListener(gridScrollListener)
        binding.listView.addOnScrollListener(listScrollListener)
        listMode.also {
            if (!it.isGridMode) binding.viewSwitcher.showNext()
        }
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
    fun moveToViewer(view: View, photo: PXLPhoto) {
        // You can choose images by using this example below.
        // PXLPhotoSize Options: [ORIGINAL, BIG, MEDIUM, THUMBNAIL]
        // optional: PXLPhotoViewerActivity.launch(getContext(), photo, "photo name");
        val b = arguments
        if (b != null) {
            if (b.getBoolean("openWidgets")) {
                val list = listOf(PhotoLauncher.ViewerActivity, PhotoLauncher.PXLPhotoView, PhotoLauncher.PXLPhotoViewInRecyclerView)

                val listTexts = arrayOfNulls<String>(list.size)
                for (i in list.indices) {
                    listTexts[i] = getString(list[i].stringRes)
                }

                addFragmentToActivity(PXLPhotoViewInRecyclerViewFragment.getInstance(photo))
//                context?.also {
//                    class ItemSelected(var position: Int = 0)
//                    val itemSelected = ItemSelected()
//                    androidx.appcompat.app.AlertDialog.Builder(it)
//                            .setTitle(getString(R.string.galleryPhotoLauncherMessage))
//                            .setSingleChoiceItems(listTexts, itemSelected.position) { dialog, which ->
//                                itemSelected.position = which
//                            }
//                            .setPositiveButton(R.string.next) { dialog, which ->
//                                when (list[itemSelected.position]) {
//                                    PhotoLauncher.ViewerActivity -> ViewerActivity.launch(it, photo)
//                                    PhotoLauncher.PXLPhotoView -> addFragmentToActivity(PXLPhotoViewFragment.getInstance(photo))
//                                    PhotoLauncher.PXLPhotoViewInRecyclerView -> addFragmentToActivity(PXLPhotoViewInRecyclerViewFragment.getInstance(photo))
//                                }
//                            }
//                            //.setNegativeButton(getString(R.string.dialog_cancel), null)
//                            .show()
//                }

            } else {
                val info = PhotoWithVideoInfo(pxlPhoto = photo,
                        configuration = PXLPhotoView.Configuration().apply {
                            // Customize image size, not a video
                            pxlPhotoSize = PXLPhotoSize.ORIGINAL
                            // Cystomize scale type
                            imageScaleType = ImageScaleType.CENTER_CROP
                            // Customize Main TextView
                            mainTextViewStyle = TextViewStyle().apply {
                                text = "Main Text"
                                size = 30.px
                                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                typeface = null
                            }
                            // Customize Sub TextView
                            subTextViewStyle = TextViewStyle().apply {
                                text = "Sub Text"
                                size = 18.px
                                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                typeface = null
                            }
                            // Customize Button
                            buttonStyle = PXLPhotoView.ButtonStyle().apply {
                                text = "Action Button"
                                size = 20.px
                                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                typeface = null
                                buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                                stroke = PXLPhotoView.Stroke().apply {
                                    width = 2.px.toInt()
                                    color = Color.WHITE
                                    radiusInPixel = 25.px
                                    padding = PXLPhotoView.Padding().apply {
                                        left = 20.px.toInt()
                                        centerRight = 40.px.toInt()
                                        topBottom = 10.px.toInt()
                                    }
                                }
                            }
                        },
                        isLoopingVideo = true,
                        soundMuted = true)
                ViewerActivity.launch(requireContext(), info)
            }
        }
    }

    /***
     * callback for endless scroller
     */
    private fun loadMorePhotos() {
        album!!.loadNextPageOfPhotos(this)
    }

    private fun samplePhotoLoad(photo: PXLPhoto) {
        // load PXLPhoto with album_photo_id
        album!!.getPhotoWithId(photo.albumPhotoId, object : RequestHandlers<PXLPhoto?> {
            override fun onComplete(result: PXLPhoto?) {}
            override fun onError(error: String) {
                Log.e("sampleactivity", "failed to load photo: $error")
            }
        })

        // load PXLPhoto with pxlphoto object
        album!!.getPhotoWithId(photo, object : RequestHandlers<PXLPhoto?> {
            override fun onComplete(photo: PXLPhoto?) {}
            override fun onError(error: String) {
                Log.e("sampleactivity", "failed to load photo: $error")
            }
        })
    }

    /***
     * Callback for loadNextPageOfPhotos.  Update our list of photos and notify the adapters.
     * @param photos - the complete list of photos (both the latest page and all previous)
     */
    override fun onComplete(photos: ArrayList<PXLPhoto>?) {
        setLoading(false)
        if (photos == null) {
            return
        }
        //album.openedWidget(PXLWidgetType.horizontal);
        //album.openedWidget("gallery");
        //album.loadMore();
        photoList!!.clear()
        photoList!!.addAll(photos)
        binding.gridView.adapter!!.notifyDataSetChanged()
        binding.listView.adapter!!.notifyDataSetChanged()
        if (photos.size > 0) {
            samplePhotoLoad(photos[0])
        }
        menuList?.apply { findItem(R.id.action_live_list).isVisible = photos.size > 0 }
    }

    /***
     * Callback for a failed call to loadNextPageOfPhotos
     * @param error
     */
    override fun onError(error: String) {
        Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", error))
    }

    private var menuList: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_gallery, menu)
        menuList = menu
        menuList?.apply { findItem(R.id.action_live_list).isVisible = photoList!=null && photoList!!.size > 0 }
        changeMenuForList(listMode!!.isGridMode)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.viewSwitcher.showNext()
        when (item.itemId) {
            R.id.action_grid -> {
                changeMenuForList(false)
            }
            R.id.action_list -> {
                changeMenuForList(true)
            }
            R.id.action_live_list->{
                photoList?.also {
                    addFragmentToActivity(PXLPhotoViewInRecyclerViewFragment.getInstance(it))
                }

            }
        }
        return false
    }

    fun changeMenuForList(gridMode: Boolean) {
        if (gridMode) {
            listMode!!.isGridMode = true
            menuList!!.findItem(R.id.action_grid).isVisible = true
            menuList!!.findItem(R.id.action_list).isVisible = false

        } else {
            listMode!!.isGridMode = false
            menuList!!.findItem(R.id.action_grid).isVisible = false
            menuList!!.findItem(R.id.action_list).isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuList = null
        _binding = null
    }

    companion object {
        fun getInstance(openWidgets: Boolean): Fragment {
            val f: Fragment = GalleryFragment()
            val bundle = Bundle()
            bundle.putBoolean("openWidgets", openWidgets)
            f.arguments = bundle
            return f
        }
    }
}
