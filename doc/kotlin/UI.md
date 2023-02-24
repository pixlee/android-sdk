# UI components
You can use these UI components after you retrive PXLPhoto data via our API [API doc](API.md)

##### Notice: Due to the limitations of hardware specs on some android devices, this SDK only for now doesn't play 2 movies concurrently to make the SDK stable. But, playing multiple videos simultaneously will be availble soon.

## Index
- List
    - Version 1
        - [PXLPhotoRecyclerView](#PXLPhotoRecyclerView) : A RecyclerView displaying a list of PXLPhoto (auto video playing, an infinite scroll)
        - [PXLPhotoRecyclerViewInGrid](#PXLPhotoRecyclerViewInGrid) : A RecyclerView displaying a list of PXLPhoto (grid list, no auto video playing, no infinite scroll)
    - Version 2
        - [PXLWidgetView (Recommended)](#PXLWidgetView-(Recommended)) : A RecyclerView displaying a list of PXLPhoto (API, [list, grid], auto video playing, an infinite scroll)
- Detail with Product
    - [PXLPhotoProductView](#PXLPhotoProductView) : A fullscreen view displaying PXLPhoto with a list of PXLPhoto
- [PXLWidgetView (Recommended)](#pxlwidgetview-recommended)
   - [viewType Options](#viewtype-options)
- [PXLPhotoView](#PXLPhotoView) : A view to display PXLPhoto

## Automatic Analytics with UI Components
We support that you can delegate firing certain analytics events to UI components.
- [Guide of PXLPhotoProductView](#automatic-analytics-of-pxlphotoproductview) : `OpenLightbox` event
- [Guide of PXLPhotoRecyclerView](#automatic-analytics-of-pxlPhotoRecyclerView) : 'VisibleWidget' and 'OpenedWidget' events
- [Guide of PXLPhotoRecyclerViewInGrid](#automatic-analytics-of-pxlphotorecyclerviewingrid) : 'VisibleWidget' and 'OpenedWidget' events
- [Guide of PXLWidgetView](#automatic-analytics-of-PXLWidgetView) : 'VisibleWidget' and 'OpenedWidget' events


### PXLPhotoProductView
This shows a fullscreen PXLPhoto with its PXLProduct list. There is an example in ViewerActivity.kt

#### Add this to your xml
```xml
#!XML
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoProductView
    android:id="@+id/pxlPhotoProductView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
#### Add this to your Activity or Fragment
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
    pxlPhotoProductView.setContent(photoInfo = item,
        showHotspots = true,
        headerConfiguration = PXLPhotoProductView.Configuration().apply {
            backButton = PXLPhotoProductView.CircleButton().apply {
                icon = com.pixlee.pixleesdk.R.drawable.round_close_black_18
                iconColor = Color.BLACK
                backgroundColor = Color.WHITE
                padding = 10.px.toInt()
                onClickListener = {
                    // back button's click effect
                    Toast.makeText(this@ViewerActivity, "Replace this with your codes, currently 'onBackPressed()'", Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
            }
            muteCheckBox = PXLPhotoProductView.MuteCheckBox().apply {
                mutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_up_black_18
                unmutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_off_black_18
                iconColor = Color.BLACK
                backgroundColor = Color.WHITE
                padding = 10.px.toInt()
                onCheckedListener = {
                    Toast.makeText(this@ViewerActivity, "is muted: $it'", Toast.LENGTH_LONG).show()
                }
            }
        },
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
```

#### [Automatic Analytics of PXLPhotoProductView]
If you want to delegate firing `OpenLightbox` analytics event to PXLPhotoProductView, use this code. On the other hand, if you want to manually fire the event, you don't use this and implement our own analytics codes. Please check out KtxAnalyticsFragment.kt to get the sample codes.
- You need `PXLClient.autoAnalyticsEnabled = true` in your application that extends Application.
```kotlin
#!kotlin
class YourApplication: Application {
    override fun onCreate() {
        super.onCreate()
        // initializing SDK
        PXLClient.initialize(<your api key>, <your secret key>)

        PXLClient.autoAnalyticsEnabled = true <----- This activates this feature
        PXLClient.regionId = your region id <--- set it if you use multi-region.
    }
}

class YourActivityOrFrament: Activity or Fragment {
    // load this when you need
    func setup() {
        pxlPhotoProductView.setContent(...)
    }
}
```

#### you can add back and mute/unmute buttons to PXLPhotoProductView
`headerConfiguration:PXLPhotoProductView.Configuration` is added to pxlPhotoProductView.loadContent(). With this, you can customize icon color, icon, circle color and padding in the circle, and also listen click event for the back and mute/unmute buttons.
```kotlin
#!kotlin
pxlPhotoProductView.loadContent(...
    ...
    headerConfiguration = PXLPhotoProductView.Configuration().apply {
        backButton = PXLPhotoProductView.CircleButton().apply {
            icon = com.pixlee.pixleesdk.R.drawable.round_close_black_18
            iconColor = Color.BLACK
            backgroundColor = Color.WHITE
            padding = 10.px.toInt()
            onClickListener = {
                // back button's click effect
                Toast.makeText(this@ViewerActivity, "Replace this with your codes, currently 'onBackPressed()'", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
        muteCheckBox = PXLPhotoProductView.MuteCheckBox().apply {
            mutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_up_black_18
            unmutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_off_black_18
            iconColor = Color.BLACK
            backgroundColor = Color.WHITE
            padding = 10.px.toInt()
            onCheckedListener = {
                Toast.makeText(this@ViewerActivity, "is muted: $it'", Toast.LENGTH_LONG).show()
            }
        }
    },
    configuration = ...
    ....
)
```

#### If you want to show hotspots if available
```kotlin
#!kotlin
pxlPhotoProductView.loadContent(...
    showHotspots = true,
    ...
)
```

#### If you want to change the bookmark
```kotlin
#!kotlin
pxlPhotoProductView.loadContent(...
    ...
    bookmarkDrawable = ProductViewHolder.Bookmark().apply {
        isVisible = true
        selectedIcon = R.drawable.<your selectedIcon> 
        unselectedIcon = R.drawable.<your unselectedIcon>
    }
    ...
)
```
#### If you want to change the bookmark with ColorFilter, add the code to ProductViewHolder.Configuration()
```kotlin
#!kotlin
pxlPhotoProductView.loadContent(...
    ...
    configuration = ProductViewHolder.Configuration().apply {
        ...
        bookmarkDrawable = ProductViewHolder.Bookmark().apply {
            isVisible = true
            selectedIcon = R.drawable.<your selectedIcon> 
                unselectedIcon = R.drawable.<your unselectedIcon>
            filterColor = ProductViewHolder.Bookmark.FilterColor(<your selectedColor>, <your unselectedColor>)
        }
        ...
    }    
    ...
)
```

#### If you want to custom the look of price and currency symbol, add the code to ProductViewHolder.Configuration()
```kotlin
#!kotlin
pxlPhotoProductView.loadContent(...
    ...
    configuration = ProductViewHolder.Configuration().apply {
        ...
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
        ...
    }    
    ...
)
```

#!kotlin
class YourActivity : AppCompatActivity() {
    
    override fun onStart() {
        super.onStart()
        pxlPhotoProductView.playVideoOnStart()
    }

    override fun onResume() {
        super.onResume()
        pxlPhotoProductView.playVideoOnResume()
    }
    
    override fun onPause() {
        super.onPause()
        pxlPhotoProductView.stopVideoOnPause()
    }

    override fun onStop() {
        super.onStart()
        pxlPhotoProductView.stopVideoOnStop()
    }
}
```

#### Mute the video
```kotlin
#!kotlin
pxlPhotoProductView.mute()
```

#### Unmute the Video
```kotlin
#!kotlin
pxlPhotoProductView.unmute()
``` 

## PXLPhotoRecyclerView
this is a class that extends RecyclerView providing an PXLPhotoAdapter, PXLPhotoView and PXLPhotoViewHolder.
- you can customize most of ui elements if needed
- infinite scroll is available.
- playing a video

#### Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoRecyclerView
    android:id="@+id/pxlPhotoRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

#### Control the video with
```kotlin
#!kotlin
PhotoWithImageScaleType(pxlPhoto = pxlPhoto,  //data
    configuration = PXLPhotoView.Configuration().apply { // size, color of buttons and texts
        // Customize image size
        pxlPhotoSize = PXLPhotoSize.ORIGINAL
        // Customize image scale type
        imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP
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
    
    },
    heightInPixel = cellSize, // the height cell size in RecyclerView
    isLoopingVideo = true,    // true: loop the video, false; play it once and stop it
    soundMuted = true        // true: muted, false: unmuted
)
```

#### Add this to your Activity or Fragment
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
            onButtonClickedListener = { view, pxlPhoto ->
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
                configuration = PXLPhotoView.Configuration().apply {
                    // Customize image size
                    pxlPhotoSize = PXLPhotoSize.ORIGINAL
                    // Customize image scale type
                    imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP
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

                    },
                heightInPixel = cellSize,
                isLoopingVideo = true,
                soundMuted = true)
            )
        }


        // start the list UI by passing these arguments
        pxlPhotoRecyclerView.replaceList(list)

        // if you just want to use List<PXLPhoto>, you can do that by following these steps
        // alternative step 1: val photos: List<PXLPhoto> = ....
        // alternative step 2: pxlPhotoRecyclerView.replaceList(photos.toList(), PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize)
    }
}
```

#### [Automatic Analytics of PXLPhotoRecyclerView]
- If you want to delegate firing 'VisibleWidget' and 'OpenedWidget' analytics event to PXLPhotoRecyclerView, use this code. On the other hand, if you want to manually fire the two events, you don't use this and do need to implement our own analytics codes. Please check out KtxAnalyticsFragment.kt to get the sample codes.
- ** [Important] Please be aware of giving the same instance of pxlKtxAlbum that you created to retrieve the list of PXLPhotos to send the correct album information to the analytics server.**
```kotlin
#!kotlin
class YourApplication: Application {
    override fun onCreate() {
        super.onCreate()
        // initializing SDK
        PXLClient.initialize(<your api key>, <your secret key>)

        PXLClient.autoAnalyticsEnabled = true <----- This activates this feature
        PXLClient.regionId = your region id <--- set it if you use multi-region.
    }
}

class YourActivityOrFrament: Activity or Fragment {
    // load this when you need
    func setup() {
        pxlPhotoRecyclerView.albumForAutoAnalytics = BaseRecyclerView.AlbumForAutoAnalytics(pxlKtxAlbum, "your own widget type for analytics tracking")
        pxlPhotoRecyclerView.initiate(...)
    }

    func loadPhotos(){
        val pxlPhoto = pxlKtxAlbum.getNextPage()
        ...
    }
}
```

#### Play and stop the video
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
    override fun onStart() {
        super.onStart()
        pxlPhotoRecyclerView.playVideoOnStart()
    }

    override fun onResume() {
        super.onResume()
        pxlPhotoRecyclerView.playVideoOnResume()
    }
    
    override fun onPause() {
        super.onPause()
        pxlPhotoRecyclerView.stopVideoOnPause()
    }

    override fun onStop() {
        super.onStart()
        pxlPhotoRecyclerView.stopVideoOnStop()
    }

}
```

#### Mute the video
```kotlin
#!kotlin
pxlPhotoRecyclerView.mute()
```

#### Unmute the Video
```kotlin
#!kotlin
pxlPhotoRecyclerView.unmute()
```

## PXLPhotoRecyclerViewInGrid
this is a class that extends RecyclerView providing an PXLPhotoAdapter, PXLPhotoView and PXLPhotoViewHolder. Please check KtxGalleryGridFragment.kt for example codes in the demo app.
- you can customize most of ui elements if needed.
- you can add a header text to the list.
- you set a header to the grid list.
- you customize the height of items.
- infinite scroll is not available here.
- auto video playing is not available.

#### Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.list.PXLPhotoRecyclerViewInGrid
    android:id="@+id/pxlPhotoRecyclerViewInGrid"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

#### isLoopingVideo and soundMuted will be ignored in PXLPhotoRecyclerViewInGrid because PXLPhotoRecyclerViewInGrid does not support playing videos in the list 
```kotlin
#!kotlin
PhotoWithImageScaleType(pxlPhoto = pxlPhoto,  //data
    configuration = PXLPhotoView.Configuration().apply{
        // Customize image size, not a video
        pxlPhotoSize = PXLPhotoSize.ORIGINAL
        // Customize image scale type
        imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP // [CENTER_CROP, FIT_CENTER]
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
    heightInPixel = cellSize, // the height cell size in RecyclerView
    isLoopingVideo = true,    // true: loop the video, false; play it once and stop it
    soundMuted = true        // true: muted, false: unmuted
)
```

#### Add this to your Activity or Fragment
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
                                        configuration = PXLPhotoView.Configuration().apply {
                                            // Customize image size, not a video
                                            pxlPhotoSize = PXLPhotoSize.ORIGINAL
                                            // Customize image scale type
                                            imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP // [CENTER_CROP, FIT_CENTER]
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
                        
                                        },
                                        heightInPixel = cellSize,
                                        isLoopingVideo = true,
                                        soundMuted = true))
        list.add(PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                                        configuration = PXLPhotoView.Configuration().apply {
                                            // Customize image size, not a video
                                            pxlPhotoSize = PXLPhotoSize.ORIGINAL
                                            // Customize image scale type
                                            imageScaleType = PXLPhotoView.ImageScaleType.FIT_CENTER // [CENTER_CROP, FIT_CENTER]
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
                        
                                        },
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

fun getTitleGif(): ListHeader{
    return ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.FIT_CENTER)
}
```

## PXLWidgetView (Recommended)
this is a class that extends RecyclerView providing an PXLPhotoAdapter, PXLPhotoView and PXLPhotoViewHolder. Please check DyamicDemoActivity.kt, SimpleGridActivity.kt and SimpleListActivity for example codes in the demo app.
- you can display photos in different layouts, 
- you customize the height of items.
- this view can automatically retrieve the photos of your album or product

### viewType Options
To set the layout of this view, you have to set viewType to your PXLWidgetView like this `PXLWidgetView.initiate(viewType = {your view type})`.

| List                                                                        |Grid| Mosaic                                                                                                                         | Horizontal                                                                                                                     |
|-----------------------------------------------------------------------------|---|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
|<img src="https://i.ibb.co/5rwSKcx/ezgif-com-gif-      maker-1.gif" height="300">| <img src="https://i.ibb.co/80gWSvL/ezgif-com-gif-maker-2.gif" height="300">| <img src="https://user-images.githubusercontent.com/6112156/166413474-ba4f3215-e94b-4ccd-9126-42154d4f47d8.png" height="300"/> | <img src="https://user-images.githubusercontent.com/6112156/166413492-3af85984-3337-4719-aaa8-464fb36babfd.png" height="300"/> |

- List ([example in demo](https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/simpleapp/src/main/java/com/pixlee/pixleeandroidsdk/pxlwidgetview/ListActivity.kt))
  ```kotlin
  PXLWidgetView.ViewType.List(cellHeightInPixel: Int = 200.px.toInt(),  // fixed height of the cell
                        infiniteScroll: Boolean = false,     // true: infinite scroll or false: a normal scroll 
                        autoPlayVideo: Boolean = false,      // true: auto play video or false: do not play video
                        alphaForStoppedVideos: Float = 1f    // alpha value of cells when a video is being auto played. If you don't want this, just use the default value which is 1f.  
  ) : ViewType()
  ```

- Grid ([example in demo](https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/simpleapp/src/main/java/com/pixlee/pixleeandroidsdk/pxlwidgetview/GridActivity.kt))
  ```kotlin
  PXLWidgetView.ViewType.Grid(cellHeightInPixel: Int = 200.px.toInt(),  // fixed height of the cell
                        gridSpan: Int = 2,                              // number of columns
                        lineSpace: Space = Space(),                     // space between lines in pixel
                        listHeader: ListHeader? = null                  // header of the list. not using it or giving it null will hide the header
  ) : ViewType()    
  ```
  ```kotlin
  class Space(lineWidthInPixel: Int = 4.px.toInt(),  // size in pixel 
              includingEdge: Boolean = false)        // true: give padding to the edge of the list, false: do nothing
   ```
  ```kotlin
  sealed class ListHeader {
    class SpannableText(val spannable: Spannable, val padding:TextPadding = TextPadding()) : ListHeader() // text header
    class Gif(url: String, heightInPixel: Int, imageScaleType: ImageScaleType) : ListHeader() // gif header
  }
  ```
  - ListHeader options
    - a ListHeader.SpannableText example https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/app/src/main/java/com/pixlee/pixleeandroidsdk/ui/gallery/KtxGalleryGridFragment.kt#L160-L177
    - a ListHeader.Gif example https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/app/src/main/java/com/pixlee/pixleeandroidsdk/ui/gallery/KtxGalleryGridFragment.kt#L179-L181
      - ImageScaleType https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/pixleesdk/src/main/java/com/pixlee/pixleesdk/ui/widgets/ImageScaleType.kt#L5-L21
- Mosaic ([example in demo](https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/simpleapp/src/main/java/com/pixlee/pixleeandroidsdk/pxlwidgetview/MosaicActivity.kt))
  ```kotlin
  PXLWidgetView.ViewType.MosaicMosaic(gridSpan: Int = 4,        // number of the smaller columns
                                   lineSpace: Space = Space()   // space between lines in pixel
  ) : ViewType()
  ```
- Horizontal ([example in demo](https://github.com/pixlee/android-sdk/blob/005feadfa2c0d6c205cb615b2c7a0e7f5dec2f31/simpleapp/src/main/java/com/pixlee/pixleeandroidsdk/pxlwidgetview/HorizontalActivity.kt))
  ```kotlin
  PXLWidgetView.ViewType.Horizontal(squareSizeInPixel: Int = 100.px.toInt(),   // a pixel size of a square shape cell
                                    lineWidthInPixel: Int = 4.px.toInt()       // a pixel size of a line between cells
  ) : ViewType()
  ```
  
#### Add View to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.list.PXLWidgetView
    android:id="@+id/widget"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

#### Add codes to your Fragment or Activity
```kotlin
#!kotlin
class SimpleListActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_layout)

        widget.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (widget == null)
                        return

                    initiateList((widget.measuredHeight / 2).toInt())

                    widget.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initiateList(cellHeightInPixel: Int) {
        // you can customize color, size if you need
        widget.initiate(
                widgetTypeForAnalytics = "your_widget_type",
                viewType = PXLWidgetView.ViewType.List(),
                cellHeightInPixel = cellHeightInPixel,
                apiParameters = PXLKtxBaseAlbum.Params(
                        // album images
                        searchId = PXLKtxBaseAlbum.SearchId.Album("your album number"), // product images: searchId = PXLKtxBaseAlbum.SearchId.Product("your sku string"),
                        filterOptions = PXLAlbumFilterOptions().apply {
                            // hasProduct and hasPermission are often used together for displaying photos with tagged products and gotten the permission from their creators
                            // if you don't see any photos after the loading is done, go to https://app.pixlee.com/app#albums/{your album id} and make sure your photos have the same filter conditions as your filterOptions.
                            hasProduct = true
                            hasPermission = true

                            // more filter options
                            // - hasPermission = true
                            // - inStockOnly = true
                            // - .. there are more. Please check README or PXLAlbumFilterOptions class for more filter options
                        },
                        sortOptions = PXLAlbumSortOptions().apply {
                            sortType = PXLAlbumSortType.RECENCY
                            descending = false
                        }
                ),
                loadMoreTextViewStyle = TextViewStyle().apply {
                    text = "Load More"
                    textPadding = TextPadding(0, 22.px.toInt(), 0, 22.px.toInt())
                    size = 18.px
                    color = Color.BLACK
                },
                configuration = PXLPhotoView.Configuration().apply {
                    pxlPhotoSize = PXLPhotoSize.MEDIUM
                    imageScaleType = ImageScaleType.CENTER_CROP
                },
                onPhotoClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    ViewerActivity.launch(this, photoWithImageScaleType)
                    Toast.makeText(this, "onItemClickedListener", Toast.LENGTH_SHORT).show()
                }
        )
    }
}
```

#### isLoopingVideo and soundMuted will be ignored in PXLPhotoRecyclerViewInGrid because PXLPhotoRecyclerViewInGrid does not support playing videos in the list


## PXLPhotoView
If you want to display your a PXLPhoto without a list of PXLProduct in your layout, you can use this codes.

#### Add this to your xml
```xml
#!xml
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
    android:id="@+id/pxlPhotoView"
    android:layout_width="match_parent"
    android:layout_height="250dp"  /* You can change the height to what you need. This is just an example. */
/>
```

#### Add this to your Activity or Fragment
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
        pxlPhotoView.setContent(item, PXLPhotoView.ImageScaleType.CENTER_CROP)   
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun playVideoOnStart() {
        pxlPhotoView.playVideo()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideoOnResume() {
        pxlPhotoView.playVideo()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideoOnPause() {
        pxlPhotoView.pauseVideo()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopVideoOnStop() {
        pxlPhotoView.pauseVideo()
    }
}
```

#### [Automatic Analytics of PXLPhotoRecyclerViewInGrid]
- If you want to delegate firing 'VisibleWidget' and 'OpenedWidget' analytics event to PXLPhotoRecyclerViewInGrid, use this code. On the other hand, if you want to manually fire the two events, you don't use this and do need to implement our own analytics codes. Please check out KtxAnalyticsFragment.kt to get the sample codes.
- **[Important] Please be aware of giving the same instance of pxlKtxAlbum that you created to retrieve the list of PXLPhotos to send the correct album information to the analytics server.**
```kotlin
#!kotlin
class YourApplication: Application {
    override fun onCreate() {
        super.onCreate()
        // initializing SDK
        PXLClient.initialize(<your api key>, <your secret key>)

        PXLClient.autoAnalyticsEnabled = true <----- This activates this feature
        PXLClient.regionId = your region id <--- set it if you use multi-region.
    }
}

class YourActivityOrFrament: Activity or Fragment {
    // load this when you need
    func setup() {
        pxlPhotoRecyclerViewInGrid.albumForAutoAnalytics = BaseRecyclerView.AlbumForAutoAnalytics(pxlKtxAlbum, "your own widget type for analytics tracking")
        pxlPhotoRecyclerViewInGrid.initiate(...)
    }

    func loadPhotos(){
        val pxlPhoto = pxlKtxAlbum.getNextPage()
        ...
    }
}
```

#### Mute the video
```kotlin
#!kotlin
pxlPhotoView.mute()
```

#### Unmute the Video
```kotlin
#!kotlin
pxlPhotoView.unmute()
```
