package com.pixlee.pixleeandroidsdk.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.radiobutton.MaterialRadioButton
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.databinding.FragmentKtxGalleryGridBinding
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.*
import com.pixlee.pixleesdk.network.observer.AnalyticsObserver
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.BaseRecyclerView
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.util.EventObserver
import com.pixlee.pixleesdk.util.px
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
class KtxGalleryGridFragment : BaseFragment(), LifecycleObserver {
    override fun getTitleResource(): Int {
        return R.string.title_ktx_album_grid
    }

    val viewModel: KtxGalleryViewModel by lazy {
        // get PXLKtxAlbum
        KtxGalleryViewModel(PXLKtxAlbum(requireContext()))
    }

    private var _binding: FragmentKtxGalleryGridBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentKtxGalleryGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listenAnalyticsForInstrumentTesting()
        setDataForAutoAnalytics()
        binding.moduleSearchLayout.radioGroupContentTypeVideo.isChecked = true
        initRecyclerView()
        addViewModelListeners()
        initFilterClickListeners()

        binding.vBody.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (binding.vBody == null)
                        return

                    val cellHeightInPixel = binding.vBody.measuredHeight * 0.5f
                    viewModel.cellHeightInPixel = cellHeightInPixel.toInt()
                    loadAlbum()
                    binding.vBody.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun listenAnalyticsForInstrumentTesting(){
        viewLifecycleOwner.lifecycleScope.launch {
            AnalyticsObserver.observe("Obsev.GalleryGrid", binding.tvDebugText)
        }
    }

    fun setDataForAutoAnalytics() {
        // if you want to delegate firing 'VisibleWidget' and 'OpenedWidget' analytics event to PXLPhotoRecyclerViewInGrid, use this code.
        // Prerequisite: PXLClient.autoAnalyticsEnabled = true located in in your application level. please check AppApplication.kt
        binding.pxlPhotoRecyclerViewInGrid.albumForAutoAnalytics = BaseRecyclerView.AlbumForAutoAnalytics(viewModel.pxlKtxAlbum, PXLWidgetType.photowall.type)

        // if you want to manually fire the two events, you don't use this and do need to implement our own analytics codes. Please check out KtxAnalyticsFragment.kt to get the sample codes.
    }

    fun addViewModelListeners() {
        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver{
            showToast(it)
        })

        viewModel.uploadStatus.observe(viewLifecycleOwner, Observer{
            binding.fabUpload.isEnabled = !it
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            binding.lottieView.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.searchResultEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is BaseViewModel.Command.Data -> {
                    if (it.isFirstPage) {
                        binding.pxlPhotoRecyclerViewInGrid.replaceList(it.list)
                        if (it.list.isNotEmpty()) {
                            it.list.firstOrNull()?.pxlPhoto?.also {
                                viewModel.getPhotoWithId(it)  // add your own region id
                            }
                        }

                        // if no result in the first page, open search panel so that the SDK developers will try out different filters
                        if (it.list.isEmpty()) {
                            Toast.makeText(context, "success!! but you got an empty list.\nwhat about trying different searching options here?", Toast.LENGTH_LONG).show()
                            binding.drawerLayout.openDrawer(GravityCompat.END)
                        }

                    } else {
                        binding.pxlPhotoRecyclerViewInGrid.addList(it.list)
                    }

                }
            }
        })
    }

    fun initRecyclerView() {
        initGrid()
    }

    fun getTitleSpannable(): ListHeader {
        val top = "PIXLEE\nSHOPPERS"
        val tv = "\nTV"
        val total = top + tv
        val spannable = SpannableString(total)

        spannable.setSpan(AbsoluteSizeSpan(40.px.toInt()), 0, top.length, 0); // set size
        spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, top.length, 0);// set color

        total.indexOf(tv).let { tvLocatedAt ->
            spannable.setSpan(AbsoluteSizeSpan(20.px.toInt()), tvLocatedAt, tvLocatedAt + tv.length, 0); // set size
            spannable.setSpan(ForegroundColorSpan(Color.BLACK), tvLocatedAt, tvLocatedAt + tv.length, 0);// set color
        }

        val padding = 20.px.toInt()
        return ListHeader.SpannableText(spannable = spannable,
                padding = TextPadding(left = padding, top = padding, right = padding, bottom = padding))
    }

    fun getTitleGif(): ListHeader {
        return ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.CENTER_CROP)
    }

    fun initGrid() {
        viewModel.customizedConfiguration = PXLPhotoView.Configuration().apply {
            // Customize image size, not a video
            pxlPhotoSize = PXLPhotoSize.ORIGINAL
            // Customize image scale type
            imageScaleType = ImageScaleType.CENTER_CROP
            // Customize Main TextView
            mainTextViewStyle = TextViewStyle().apply {
                text = "Spring\nColors"
                size = 30.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                textPadding = TextPadding(bottom = 30.px.toInt())
            }
            // Customize Sub TextView
            subTextViewStyle = null // you can hide this view by giving it null
            // Customize Button
            buttonStyle = PXLPhotoView.ButtonStyle().apply {
                text = "VER AHORA"
                size = 12.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                stroke = PXLPhotoView.Stroke().apply {
                    width = 1.px.toInt()
                    color = Color.WHITE
                    radiusInPixel = 25.px
                    padding = PXLPhotoView.Padding().apply {
                        left = 10.px.toInt()
                        centerRight = 20.px.toInt()
                        topBottom = 10.px.toInt()
                    }
                }
            }
        }
        // you can customize color, size if you need
        binding.pxlPhotoRecyclerViewInGrid.initiate(gridSpan = 2, // the number of cells in a row in the grid list
                lineSpace = Space().apply {
                    lineWidthInPixel = 4.px.toInt() // space in pixel between cells
                    includingEdge = false           // true: if you want to have the space out side of the list, false: no space out side of the list
                },
                listHeader = getTitleGif(), // you can custom your spannable either using getTitleSpannable() or getTitleGif(), examples of how you can implement your spannable
                showingDebugView = false,
                onButtonClickedListener = { view, photoWithImageScaleType ->
                    context?.also { ctx ->
                        // you can add your business logic here
                        Toast.makeText(ctx, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                        moveToViewer(photoWithImageScaleType)
                    }
                }, onPhotoClickedListener = { view, photoWithImageScaleType ->
            context?.also { ctx ->
                // you can add your business logic here
                Toast.makeText(ctx, "onItemClickedListener", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun initFilterClickListeners() {
        // set filter buttons
        binding.fabUpload.setOnClickListener { setupExternalStoragePermission() }
        binding.fabFilter.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.END) }
        binding.moduleSearchLayout.btnCloseFilter.setOnClickListener { binding.drawerLayout.closeDrawer(GravityCompat.END) }
        binding.moduleSearchLayout.btnApply.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            loadAlbum()
        }
    }

    override fun uploadFile(filePath: String) {
        val json = JSONObject()
        try {
            json.put("name", "Sample Name")
            json.put("age", 24)
            val arr = JSONArray()
            arr.put(10)
            arr.put(20)
            arr.put(35)
            json.put("points", arr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        viewModel.uploadPhoto(
                filePath,
                "uploaded from SDK-" + System.currentTimeMillis() + " using a file",
                "xxx@xxx.com",
                "replace this with your user name",
                true,
                null,
                null,
                json)
    }

    /***
     * Initializes the PXLClient and creates the PXLAlbum
     */
    private fun loadAlbum() {

        context?.also {
            var searchId: PXLKtxBaseAlbum.SearchId? = null
            // initiate album
            for (i in 0 until binding.moduleSearchLayout.radioGroupAlbum.childCount) {
                val rb = binding.moduleSearchLayout.radioGroupAlbum.getChildAt(i) as MaterialRadioButton
                if (binding.moduleSearchLayout.radioGroupAlbum.checkedRadioButtonId == rb.id) {
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
                        sortOptions = readSortOptionsFromUI()
                ))

                // Please be aware that the right place you implement to set PXLClient.regionId is in your Application level. please check AppApplication.kt
                PXLClient.regionId = readRegionIdFromUI()

                // retrieve the first page
                viewModel.getFirstPage()
            }
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

    fun readRegionIdFromUI(): Int? {
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
        if (binding.moduleSearchLayout.radioGroupSortDirection.checkedRadioButtonId == binding.moduleSearchLayout.radioGroupSortDirectionASC.id) sortOptions.descending = false
        else if (binding.moduleSearchLayout.radioGroupSortDirection.checkedRadioButtonId == binding.moduleSearchLayout.radioGroupSortDirectionDESC.id) sortOptions.descending = true
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
                    .show()
        }
    }

    fun isGrid(): Boolean {
        return arguments?.getBoolean("isGrid") ?: false
    }

    companion object {
        fun getGridInstance(): Fragment {
            val f: Fragment = KtxGalleryGridFragment()
            val bundle = Bundle()
            bundle.putBoolean("isGrid", true)
            f.arguments = bundle
            return f
        }

        fun getListInstance(): Fragment {
            val f: Fragment = KtxGalleryGridFragment()
            val bundle = Bundle()
            bundle.putBoolean("isGrid", false)
            f.arguments = bundle
            return f
        }
    }
}
