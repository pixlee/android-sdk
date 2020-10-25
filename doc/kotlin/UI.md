# UI components
You can use these UI components after you retrive PXLPhoto data via our API [API doc](API.md)

##### Notice: Due to the limitations of hardware specs on some android devices, this SDK only for now doesn't play 2 movies concurrently to make the SDK stable. But, playing multiple videos simultaneously will be availble soon.

## Index
- [PXLPhotoProductView](#PXLPhotoProductView) : A fullscreen view displaying PXLPhoto with a list of PXLPhoto
- [PXLPhotoRecyclerView](#PXLPhotoRecyclerView) : A RecyclerView displaying a list of PXLPhoto (auto video playing, an infinite scroll) 
- [PXLPhotoRecyclerViewInGrid](#PXLPhotoRecyclerViewInGrid) : A RecyclerView displaying a list of PXLPhoto (grid list, no auto video playing, no infinite scroll)
- [PXLPhotoView](#PXLPhotoView) : A view to display PXLPhoto

### PXLPhotoProductView
This shows a fullscreen PXLPhoto with its PXLProduct list. There is an example in ViewerActivity.kt

Add this to your xml
```xml
#!XML
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoProductView
    android:id="@+id/pxlPhotoProductView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
Add this to your Activity or Fragment
```kotlin
#!kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(...)
    ...
    ...
    val item: PhotoWithVideoInfo? = i.getParcelableExtra("photoWithVideoInfo")
    // if item is null, close this image view
    if (item == null) {
        finish()
        return
    }
    pxlPhotoProductView.loadContent(photoInfo = item,
        configuration = ProductViewHolder.Configuration().apply {
            circleIcon = ProductViewHolder.CircleIcon().apply {
                icon = R.drawable.<your drawable>
                iconColor = <set color:int, if you want to change icon's color>
                backgroundColor = <circle background color>
                padding = <padding size in pixel>
            }
            mainTextStyle = TextStyle().apply {
                size = 14.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                color = Color.WHITE
                typeface = null
            }
            subTextStyle = TextStyle().apply {
                size = 12.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                color = Color.WHITE
                typeface = null
            }
            bookmarkDrawable = ProductViewHolder.Bookmark().apply {
                isVisible = true
                selectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_black_36
                unselectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_border_black_36
            }
            priceTextStyle = CurrencyTextStyle().apply {
                defaultCurrency = "EUR" // or null
                leftText = TextStyle().apply {
                    color = Color.BLACK
                    size = 24.px
                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                    typeface = null
                }

                rightText = TextStyle().apply {
                    color = Color.BLACK
                    size = 14.px
                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                    typeface = null
                }
            }
            priceTextStyle = TextStyle().apply {
                size = 24.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                color = Color.WHITE
                typeface = null
            }
        },
        bookmarkMap = readBookmarks(pxlPhoto),
        onBookmarkClicked = { productId, isBookmarkChecked ->
            Toast.makeText(this, "productId: $productId\nisBookmarkChecked: $isBookmarkChecked", Toast.LENGTH_SHORT).show()
            // add your business logic here
        },
        onProductClicked = {
            Toast.makeText(this, "product clicked, product id: ${it.id}", Toast.LENGTH_SHORT).show()
            // add your business logic here
        })
}


fun readBookmarks(pxlPhoto: PXLPhoto): HashMap<String, Boolean> {
    // this code should be replaced by your own bookmarks
    val bookmarkMap = HashMap<String, Boolean>()
    if (pxlPhoto.products != null) {
        for (product in pxlPhoto.products) {
            bookmarkMap[product.id] = Random().nextBoolean()
        }
    }
    return bookmarkMap

}

If you want to change the bookmark
```kotlin
#!kotlin
bookmarkDrawable = ProductViewHolder.Bookmark().apply {
    isVisible = true
    selectedIcon = R.drawable.<your selectedIcon> 
    unselectedIcon = R.drawable.<your unselectedIcon>
}
```
If you want to change the bookmark with ColorFilter, add the code to ProductViewHolder.Configuration()
```kotlin
#!kotlin
bookmarkDrawable = ProductViewHolder.Bookmark().apply {
    isVisible = true
    selectedIcon = R.drawable.<your selectedIcon> 
        unselectedIcon = R.drawable.<your unselectedIcon>
    filterColor = ProductViewHolder.Bookmark.FilterColor(<your selectedColor>, <your unselectedColor>)
}
```

If you want to custom the look of price and currency symbol, add the code to ProductViewHolder.Configuration()
```kotlin
#!kotlin
priceTextStyle = CurrencyTextStyle().apply {
    defaultCurrency = "EUR" // or null
    leftText = TextStyle().apply {
        color = Color.BLACK
        size = 24.px
        sizeUnit = TypedValue.COMPLEX_UNIT_PX
        typeface = null
    }

    rightText = TextStyle().apply {
        color = Color.BLACK
        size = 14.px
        sizeUnit = TypedValue.COMPLEX_UNIT_PX
        typeface = null
    }
}
```
Play and stop the video
- Option 1: Automatic using androidx.lifecycle.Lifecycle(Jetpack)
  - Prerequisite: add the dependencies in the doc (https://developer.android.com/jetpack/androidx/releases/lifecycle) to your app gradle. You can also see the sample on app/build.gradle in the demo app.   
  - Add the codes 
    ```kotlin
    #!kotlin
    class YourActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            ...
            pxlPhotoProductView.useLifecycleObserver(lifecycle)
        }
    }
    ```   
- Option 2: Manual (do this if you want to play and stop the video when you need)
```kotlin
#!kotlin
class YourActivity : AppCompatActivity() {
    // play video
    override fun onResume() {
        super.onResume()
        pxlPhotoProductView.playVideo()
    }
    
    // stop video
    override fun onPause() {
        super.onPause()
        pxlPhotoProductView.stopVideo()
    }
}
```

Mute the video
```kotlin
#!kotlin
pxlPhotoProductView.mute()
```

Unmute the Video
```kotlin
#!kotlin
pxlPhotoProductView.unmute()
``` 

## PXLPhotoRecyclerView
this is a class that extends RecyclerView providing an PXLPhotoAdapter, PXLPhotoView and PXLPhotoViewHolder.
- you can customize most of ui elements if needed
- infinite scroll is available.
- playing a video

Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoRecyclerView
    android:id="@+id/pxlPhotoRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

Control the video with
```kotlin
#!kotlin
PhotoWithImageScaleType(pxlPhoto = pxlPhoto,  //data
    imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP, // [CENTER_CROP, FIT_CENTER]
    heightInPixel = cellSize, // the height cell size in RecyclerView
    isLoopingVideo = true,    // true: loop the video, false; play it once and stop it
    soundMuted = true        // true: muted, false: unmuted
)
```

Add this to your Activity or Fragment
```kotlin
#!kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(...)
    ...
    ...
    // you can customize color, size if you need
    pxlPhotoRecyclerView.initiate(infiniteScroll = true, // or false
            showingDebugView = false, // false: for production, true: development only when you want to see the debug info
            alphaForStoppedVideos = 0.5f, // this is the alpha(opacity) of visible items in recyclerview except the first fully visible view(always 1f) 
            configuration = PXLPhotoView.Configuration().apply {
                // Customize image size
                pxlPhotoSize = PXLPhotoSize.ORIGINAL
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
                    isButtonVisible = true
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

            }, onButtonClickedListener = { view, pxlPhoto ->
                context?.also { ctx ->
                    // you can add your business logic here
                    Toast.makeText(ctx, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                    moveToViewer(pxlPhoto)
                }
            }, onPhotoClickedListener = { view, pxlPhoto ->
                context?.also { ctx ->
                    // you can add your business logic here
                    Toast.makeText(ctx, "onItemClickedListener", Toast.LENGTH_SHORT).show()
                }
            })

    pxlPhotoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            try {
                if (pxlPhotoRecyclerView == null)
                    return

                // this is to display two items at a time on the screen
                val cellHeightInPixel = pxlPhotoRecyclerView.measuredHeight * 0.6f
                startList(cellHeightInPixel)
                pxlPhotoRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    })

    fun startList(cellSize: Int) {
        // write codes to get photos first. Read API doc.
        // you should convert List<PXLPhoto> into List<PhotoWithImageScaleType>
        val pxlPhotos: List<PXLPhoto> = ....

        // turn the list into List<PhotoWithImageScaleType> to set ImageScaleType[CENTER_CROP, FIT_CENTER], and the cells' height size
        val list = ArrayList<PhotoWithImageScaleType>()
        pxlPhotos.forEach { pxlPhoto ->
            list.add(PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                                            imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP,
                                            heightInPixel = cellSize,
                                            isLoopingVideo = true,
                                            soundMuted = true))
            list.add(PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                                            imageScaleType = PXLPhotoView.ImageScaleType.FIT_CENTER,
                                            heightInPixel = cellSize,
                                            isLoopingVideo = true,
                                            soundMuted = true))
        }


        // start the list UI by passing these arguments
        pxlPhotoRecyclerView.replaceList(list)

        // if you just want to use List<PXLPhoto>, you can do that by following these steps
        // alternative step 1: val photos: List<PXLPhoto> = ....
        // alternative step 2: pxlPhotoRecyclerView.replaceList(photos.toList(), PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize)
    }
}
```

Play and stop the video
- Option 1: Automatic using androidx.lifecycle.Lifecycle(Jetpack)
  - Prerequisite: add the dependencies in the doc (https://developer.android.com/jetpack/androidx/releases/lifecycle) to your app gradle. You can also see the sample on app/build.gradle in the demo app.   
  - Add the codes 
    ```kotlin
    #!kotlin
    class YourActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            ...
            pxlPhotoRecyclerView.useLifecycleObserver(lifecycle)
        }
    }
    ```   
- Option 2: Manual (do this if you want to play and stop the video when you need)
```kotlin
#!kotlin
class YourActivity : AppCompatActivity() {
    // play video
    override fun onResume() {
        super.onResume()
        pxlPhotoRecyclerView.playVideo()
    }
    
    // stop video
    override fun onPause() {
        super.onPause()
        pxlPhotoRecyclerView.stopVideo()
    }
}
```

## PXLPhotoRecyclerViewInGrid
this is a class that extends RecyclerView providing an PXLPhotoAdapter, PXLPhotoView and PXLPhotoViewHolder. Please check KtxGalleryGridFragment.kt for example codes in the demo app.
- you can customize most of ui elements if needed.
- you can add a header text to the list.
- you set a header to the grid list.
- you customize the height of items.
- infinite scroll is not available here.
- auto video playing is not available.

Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.list.PXLPhotoRecyclerViewInGrid
    android:id="@+id/pxlPhotoRecyclerViewInGrid"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

isLoopingVideo and soundMuted will be ignored in PXLPhotoRecyclerViewInGrid because PXLPhotoRecyclerViewInGrid does not support playing videos in the list 
```kotlin
#!kotlin
PhotoWithImageScaleType(pxlPhoto = pxlPhoto,  //data
    imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP, // [CENTER_CROP, FIT_CENTER]
    heightInPixel = cellSize, // the height cell size in RecyclerView
    isLoopingVideo = true,    // true: loop the video, false; play it once and stop it
    soundMuted = true        // true: muted, false: unmuted
)
```

Add this to your Activity or Fragment
```kotlin
#!kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(...)
    ...
    ...
    // you can customize color, size if you need
    pxlPhotoRecyclerViewInGrid.initiate(gridSpan = 2, // the number of cells in a row in the grid list
                    lineSpace = Space().apply {
                        lineWidthInPixel = 4.px.toInt() // space in pixel between cells
                        includingEdge = false           // true: if you want to have the space out side of the list, false: no space out side of the list  
                    },
                    listHeader = getTitleGif(), // you can custom your spannable either using getTitleSpannable() or getTitleGif(), examples of how you can implement your spannable  
                    showingDebugView = false,
                    configuration = PXLPhotoView.Configuration().apply {
                        // Customize image size, not a video
                        pxlPhotoSize = PXLPhotoSize.ORIGINAL
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
    
                    }, onButtonClickedListener = { view, photoWithImageScaleType ->
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

    pxlPhotoRecyclerViewInGrid.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            try {
                if (pxlPhotoRecyclerViewInGrid == null)
                    return

                // this is to display two items at a time on the screen
                val cellHeightInPixel = pxlPhotoRecyclerViewInGrid.measuredHeight * 0.6f
                startList(cellHeightInPixel)
                pxlPhotoRecyclerViewInGrid.viewTreeObserver.removeOnGlobalLayoutListener(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
})

fun startList(cellSize: Int) {
    // write codes to get photos first. Read API doc.
    // you should convert List<PXLPhoto> into List<PhotoWithImageScaleType>
    val pxlPhotos: List<PXLPhoto> = ....

    // turn the list into List<PhotoWithImageScaleType> to set ImageScaleType[CENTER_CROP, FIT_CENTER], and the cells' height size
    val list = ArrayList<PhotoWithImageScaleType>()
    pxlPhotos.forEach { pxlPhoto ->
        list.add(PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                                        imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP,
                                        heightInPixel = cellSize,
                                        isLoopingVideo = true,
                                        soundMuted = true))
        list.add(PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                                        imageScaleType = PXLPhotoView.ImageScaleType.FIT_CENTER,
                                        heightInPixel = cellSize,
                                        isLoopingVideo = true,
                                        soundMuted = true))
    }


    // start the list UI by passing these arguments
    pxlPhotoRecyclerView.replaceList(list)

    // if you just want to use List<PXLPhoto>, you can do that by following these steps
    // alternative step 1: val photos: List<PXLPhoto> = ....
    // alternative step 2: pxlPhotoRecyclerView.replaceList(photos.toList(), PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize)
}

fun getTitleSpannable(): ListHeader{
    val top = "PXLEE\nSHOPPERS"
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

fun getTitleGif(): ListHeader{
    return ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.FIT_CENTER)
}
```

## PXLPhotoView
If you want to display your a PXLPhoto without a list of PXLProduct in your layout, you can use this codes.

Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
    android:id="@+id/pxlPhotoView"
    android:layout_width="match_parent"
    android:layout_height="250dp"  /* You can change the height to what you need. This is just an example. */
/>
```

Add this to your Activity or Fragment
```kotlin
#!kotlin
class YourActivity: AppCompatActivity, LifecycleObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(...)
        ...
        ...
        lifecycle.addObserver(this)
        val configuration = PXLPhotoView.Configuration().apply {
            // Customize image size
            pxlPhotoSize = PXLPhotoSize.ORIGINAL
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
                isButtonVisible = true
                text = "Action Button"
                size = 20.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                stroke = PXLPhotoView.Stroke().apply {
                    width = 2.px.toInt()
                    color = Color.WHITE
                    radiusInPixel = 25.px
                    stroke = PXLPhotoView.Stroke().apply {
                        width = 2.px.toInt()
                        color = Color.WHITE
                        radiusInPixel = 25.px
                    }
                    padding = PXLPhotoView.Padding().apply {
                        left = 20.px.toInt()
                        centerRight = 40.px.toInt()
                        topBottom = 10.px.toInt()
                    }
                }
            }
    
        }
        pxlPhotoView.setConfiguration(configuration)

        val item: PhotoWithImageScaleType? = arguments?.getParcelable("photoWithImageScaleType") // read PhotoWithImageScaleType
        pxlPhotoView.loadContent(item, PXLPhotoView.ImageScaleType.CENTER_CROP)   
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideo() {
        // play video, you can also use this code onCreate or when getting data from the API
        pxlPhotoView.playVideo() // play the video
    }
    
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideo() {
        // stop any video
        PXLPhotoView.releaseAllVideos()
    }
}

```