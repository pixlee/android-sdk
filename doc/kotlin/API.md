# API

- [Album and Product Features](#Album-and-Product-Features)
    - [Initialization](#Initialization)
    - [Prepare parameters](#Prepare-parameters)
        - Option 1: [to get Album photos](#to-get-Album-photos)
        - Option 2: [to get Product photos](#to-get-Product-photos)
    - [Get Photos](#Get-Photos)
    - [Advanced Search options](#Advanced-Search-options)
    - [How to get image urls](#How-to-get-image-urls)
- Analytics
    - [Album Analytics](#Album-Analytics)
        - [Opened Widget](#Opened-Widget)
        - [Widget Visible](#Widget-Visible)
        - [Load More](#Load-More)
        - [Opened Lightbox](#Opened-Lightbox)
        - [Action Clicked](#Action-Clicked)
    - [Ecommerce Analytics](#Ecommerce-Analytics)
        - [Add To Cart](#Add-To-Cart)
        - [Conversion](#Conversion)

## Album and Product Features
To prepare to load the photos, you'll need the codes below
### Initialization
```kotlin
#!kotlin

val pxlAlbum = PXLKtxAlbum(context)
```
Or:
```kotlin
#!kotlin

val client = PXLClient.getInstance(context);
val pxlAlbum = PXLKtxAlbum(client)
```
Or:
```kotlin
#!kotlin

val client = PXLClient.getInstance(context);
val ktxBasicDataSource = client.ktxBasicDataSource
val ktxAnalyticsDataSource = client.ktxAnalyticsDataSource
val pxlAlbum = PXLKtxAlbum(ktxBasicDataSource, ktxAnalyticsDataSource)
```
### Prepare parameters
- #### Option 1: to get Album photos
```kotlin
#!kotlin

val pxlAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Album("<your ALBUM ID>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
- #### Option 2: to get Product photos
```kotlin
#!kotlin

val pxlAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Product("<your Product's SKU>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
### Get Photos
Get the first page
```kotlin
#!kotlin

pxlAlbum.getFirstPage()
```

Get the next pages
```kotlin
#!kotlin

pxlAlbum.getNextPage()
```
### Advanced Search options
```kotlin
#!kotlin

pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
    searchId = searchId,
    perPage = 30,
    filterOptions = PXLAlbumFilterOptions().apply {
        hasPermission = true
        hasProduct = true
        // ... there's more
    },
    sortOptions = PXLAlbumSortOptions().apply {
        sortType = PXLAlbumSortType.RECENCY
        descending = true
    }
)
```
## How to get image urls
Some imageURL fields can be empty or null depending on its data's status. In order to get appropriate images, you can use this method.
```kotlin
#!kotlin

val result = ktxBasicDataSource.getPhotosWithID(albumId, filterOptions, sortOptions, perPage, lastPageLoaded)
result.forEach { pxlPhoto ->
    // here: your business logic
    pxlPhoto.getUrlForSize(PXLPhotoSize.ORIGINAL)
    pxlPhoto.getUrlForSize(PXLPhotoSize.BIG)
    pxlPhoto.getUrlForSize(PXLPhotoSize.MEDIUM)
    pxlPhoto.getUrlForSize(PXLPhotoSize.THUMBNAIL)
}
```

## Album Analytics
You can see the example codes for analytics in the demo app.

### Opened Widget & Widget Visible
Be aware of the difference between **Opened Widget** and **Widget Visible**. (Need a sample code. Check the demo app in the project)

There is an order of firing these two APIs.
1. **Opened Widget**: You should fire this when firing the api is done and loading the photo data into your own view for the widget is complete.
2. **Widget Visible**: **Opened Widget** should be fired first. Then, you can fire this when your own view for the widget started to be visible on the screen.

- #### Opened Widget
    - To fire this event, simply call the `openedWidget` method of the PXLKtxAlbum AFTER data has been returned from the first call of `pxlKtxAlbum.getFirstPage()` or `pxlKtxAlbum.getNextPage()` method, and an "Opened Widget" event will be fired containing all of the necessary analytics information.

        ```kotlin
        #!kotlin

        pxlKtxAlbum.openedWidget(PXLWidgetType.photowall);
        pxlKtxAlbum.openedWidget(PXLWidgetType.horizontal);
        pxlKtxAlbum.openedWidget("<Customized name>");
        ```

- #### Widget Visible
    - To fire this event, simply call the `widgetVisible` method of the PXLKtxAlbum AFTER data has been returned from the first call of `pxlKtxAlbum.getFirstPage()` or `pxlKtxAlbum.getNextPage()` method, and an "Widget Visible" event will be fired containing all of the necessary analytics information.

        ```kotlin
        #!kotlin

        pxlKtxAlbum.widgetVisible(PXLWidgetType.photowall);
        pxlKtxAlbum.widgetVisible(PXLWidgetType.horizontal);
        pxlKtxAlbum.widgetVisible("<Customized name>");
        ```

### Load More
- To fire a load more event, simply call the `loadMore` method of the PXLKtxAlbum AFTER `pxlKtxAlbum.getNextPage()` method with [pxlKtxAlbum.lastPageLoaded >= 2] is successfully called, a "Load More" analytics event will be fired containing all of the necessary analytics information.
See the onComplete function in GalleryFragment.java for an example.
- On calls to pxlKtxAlbum.getNextPage() (except the first), a "Load More" analytics event will be fired automatically
    ```kotlin
    #!kotlin

    pxlKtxAlbum.loadMore();
    ```
- if you want to manually fire pxlKtxAlbum.loadMore(), first you must successfully call this pxlKtxAlbum.getNextPage(callLoadMoreAnalytics = true) to get photos which does not fire loadMore(), then you manually fire pxlKtxAlbum.loadMore() in your app.

### Opened Lightbox
- To fire an opened ligtbox event, simply call the `openedLightbox` method of PXLKtxAlbum, and an "Opened Lightbox" event will be fired containing all of the necessary analytics information.

    ```kotlin
    #!kotlin

    pxlKtxAlbum.openedLightbox(photo.albumPhotoId)
    pxlKtxAlbum.openedLightbox(photo)
    ```

### Action Clicked
- To fire an action clicked event, simply call the `actionClicked` method of PXLKtxAlbum that the action click is being driven from and pass in the URL of the link that the user is being redirected to.  An "Action Clicked" event will be fired containing all of the necessary analytics information.

    ```kotlin
    #!kotlin

    pxlKtxAlbum.actionClicked(photo.albumPhotoId, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");
    pxlKtxAlbum.actionClicked(photo, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");

    ```
## Ecommerce Analytics
- Option 1: Use PXLKtxAlbum
    ```kotlin
    #!kotlin

    val pxlAlbum = PXLKtxAlbum(context)
    ```
- Option 2: Use KtxAnalyticsDataSource
    ```kotlin
    #!kotlin

    val client = PXLClient.getInstance(context);
    val ktxAnalyticsDataSource = client.ktxAnalyticsDataSource
    ```

### Add To Cart
- To fire an Add To Cart event, simply call the `addToCart` method in PXLKtxAlbum or KtxAnalyticsDataSource with the necessary parameters, and an "Add To Cart" event will be fired containing all of the necessary analytics information.
The parameters for this method are:
    - [Required] sku  (String)
    - [Required] price (String)
    - [Required] quantity (Integer)
    - [Optional] currency (String)

    ```kotlin
    #!kotlin

    pxlAlbum.addToCart("sku123", "123", 4);
    ```
    Or:
    ```
    #!kotlin
    ktxAnalyticsDataSource.addToCart("sku123", "123", 4);
    ```

### Conversion
- To fire a Conversion event, simply call the `conversion` method in PXLKtxAlbum or KtxAnalyticsDataSource with the necessary parameters, and a "Conversion" event will be fired containing all of the necessary analytics information.
The parameters for this method are:
    - [Required] cartContents  (ArrayList<HashMap<String, Object>>)
    - [Required] cartTotal (String)
    - [Required] cartTotalQuantity (Integer)
    - [Optional] orderId (String)
    - [Optional] currency (String)

    ```kotlin
    #!kotlin

    val cartContents: ArrayList<HashMap<String, Any>> = ArrayList()
    val cart1: HashMap<String, Any> = HashMap()
    cart1["price"] = "123"
    cart1["product_sku"] = BuildConfig.PIXLEE_SKU
    cart1["quantity"] = "4"
    cartContents.add(cart1)
    // you can add more to cartContents if needed


    pxlAlbum.conversion(cartContents = cartContents, cartTotal = "123", cartTotalQuantity = 4)
    ```
    OR:
    ```kotlin
    #!kotlin

    ktxAnalyticsDataSource.conversion(cartContents = cartContents, cartTotal = "123", cartTotalQuantity = 4)
    ```
## UI components
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

PXLPhotoProductView: photo with products in a fullscreen view
PXLPhotoRecyclerView: recyclerview with PXLPhotoView in its ViewHolder. infinite scroll is available. play video one at a time
PXLPhotoView

ProductViewHolder
PXLPhotoAdapter
PXLPhotoViewHolder