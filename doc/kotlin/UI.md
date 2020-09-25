# UI components
You can use these UI components after you retrive PXLPhoto data via our API [API doc](API.md)

##### Notice: Due to the limitations of hardware specs on some android devices, this SDK only for now don't play 2 movies concurrently to make the SDK stable. But, playing multiple videos simultaneously will be availble soon.

## Index
- [PXLPhotoProductView](#PXLPhotoProductView) : A fullscreen view displaying PXLPhoto with a list of PXLPhoto
- [PXLPhotoRecyclerView](#PXLPhotoRecyclerView) : A RecyclerView displaying a list of PXLPhoto
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
    val pxlPhoto: PXLPhoto? = i.getParcelableExtra("pxlPhoto")
    // if the photo is null, close this image view
    if (pxlPhoto == null) {
        finish()
        return
    }
    pxlPhotoProductView.setPhoto(pxlPhoto = pxlPhoto,
        configuration = ProductViewHolder.Configuration().apply {
            circleIcon = ProductViewHolder.CircleIcon().apply {
                icon = R.drawable.<your drawable>
                iconColor = <set color:int, if you want to change icon's color>
                backgroundColor = <circle background color>
            }
            mainTextStyle = TextStyle().apply {
                size = 14.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
            }
            subTextStyle = TextStyle().apply {
                size = 12.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
            }
            priceTextStyle = TextStyle().apply {
                size = 24.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
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
            showingDebugView = false, //false: for production, true: development only when you want to see the debug info 
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
        val photos: List<PXLPhoto> = ....

        // turn the list into List<PhotoWithImageScaleType> to set ImageScaleType[CENTER_CROP, FIT_CENTER], and the cells' height size
        val list = ArrayList<PhotoWithImageScaleType>()
        photos.forEach { pxlPhoto ->
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

    // play video
    override fun onResume() {
        super.onResume()
        pxlPhotoRecyclerView.onResume()
    }

    // stop video
    override fun onStop() {
        super.onStop()
        pxlPhotoRecyclerView.onStop()
    }
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
private val mVideoPlayerManager: VideoPlayerManager<MetaData> = SingleVideoPlayerManager { }
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(...)
    ...
    ...
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
    pxlPhotoView.setPhoto(it, PXLPhotoView.ImageScaleType.CENTER_CROP)
    pxlPhotoView.playVideo(videoPlayerManger = mVideoPlayerManager, isLooping = true, muted = true)

    // alternative: pxlPhotoView.playVideo(videoPlayerManger = mVideoPlayerManager, isLooping = false, muted = false)
    // alternative: pxlPhotoView.setPhoto(it, PXLPhotoView.ImageScaleType.FIT_CENTER)
}