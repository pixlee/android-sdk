# UI components


### Show a fullscreen PXLPhoto with its PXLProduct list
- There is an example in ViewerActivity.kt
XML
```xml
<com.pixlee.pixleesdk.ui.widgets.PXLPhotoProductView
    android:id="@+id/pxlPhotoProductView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
Kotlin
```kotlin
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
PXLPhotoProductView: photo with products in a fullscreen view
PXLPhotoRecyclerView: recyclerview with PXLPhotoView in its ViewHolder. infinite scroll is available. play video one at a time
PXLPhotoView

ProductViewHolder
PXLPhotoAdapter
PXLPhotoViewHolder