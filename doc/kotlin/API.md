# API

- [Album and Product Features](#Album-and-Product-Features)
    - [Initialization](#Initialization)
    - [Prepare parameters](#Prepare-parameters)
        - Option 1: [to get Album content](#to-get-Album-content)
        - Option 2: [to get Product content](#to-get-Product-content)
        - [Advanced parameter options](#Advanced-parameter-options)
    - [Get content](#Get-content)
    - [Get a PXLPhoto with an album photo id](API.md#get-pxlphoto-with-an-albumphotoid)
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
To prepare to load the content, you'll need the codes below
### Initialization
```kotlin
#!kotlin

val pxlKtxAlbum = PXLKtxAlbum(context)
```
Or:
```kotlin
#!kotlin

val client = PXLClient.getInstance(context);
val pxlKtxAlbum = PXLKtxAlbum(client)
```
Or:
```kotlin
#!kotlin

val client = PXLClient.getInstance(context);
val ktxBasicDataSource = client.ktxBasicDataSource
val ktxAnalyticsDataSource = client.ktxAnalyticsDataSource
val pxlKtxAlbum = PXLKtxAlbum(ktxBasicDataSource, ktxAnalyticsDataSource)
```
### Prepare parameters
- #### Option 1: to get Album content
```kotlin
#!kotlin

val pxlKtxAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Album("<your ALBUM ID>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
- #### Option 2: to get Product content
```kotlin
#!kotlin

val pxlKtxAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Product("<your Product's SKU>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
- #### Advanced parameter options
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
        // ... there's more.
    },
    regionId = <Optional: your region id(Int)>       //<-------------- HERE is where you need to add your region id (Optional). If you don't know your region ids, please ask your account manager to give you it. 
)
```
### Get content (a list of PXLPhoto)
Get the first page
```kotlin
#!Kotlin.coroutines

val result = pxlKtxAlbum.getFirstPage()
```

Get the next pages
```kotlin
#!Kotlin.coroutines

val result = pxlKtxAlbum.getNextPage()
```

### Get PXLPhoto with an albumPhotoId
```kotlin
#!Kotlin.coroutines

val albumPhotoId:String = <one of your album photo ids>
val result:PXLPhoto = pxlKtxAlbum.getPhotoWithId(albumPhotoId)
```

### How to get image urls
Some imageURL fields can be empty or null depending on its data's status. In order to get appropriate images, you can use this method.
```kotlin
#!kotlin

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
1. **Opened Widget**: You should fire this when firing the api is done and loading a list of PXLPhoto into your own view for the widget is complete.
2. **Widget Visible**: **Opened Widget** should be fired first. Then, you can fire this when your own view for the widget started to be visible on the screen.

- #### Opened Widget
    - To fire this event, simply call the `openedWidget` method of the PXLKtxAlbum AFTER data has been returned from the first call of `pxlKtxAlbum.getFirstPage()` or `pxlKtxAlbum.getNextPage()` method, and an "Opened Widget" event will be fired containing all of the necessary analytics information.

        ```kotlin
        #!Kotlin.coroutines

        pxlKtxAlbum.openedWidget(PXLWidgetType.photowall);
        pxlKtxAlbum.openedWidget(PXLWidgetType.horizontal);
        pxlKtxAlbum.openedWidget("<Customized name>");
        ```

- #### Widget Visible
    - To fire this event, simply call the `widgetVisible` method of the PXLKtxAlbum AFTER data has been returned from the first call of `pxlKtxAlbum.getFirstPage()` or `pxlKtxAlbum.getNextPage()` method, and an "Widget Visible" event will be fired containing all of the necessary analytics information.

        ```kotlin
        #!Kotlin.coroutines

        pxlKtxAlbum.widgetVisible(PXLWidgetType.photowall);
        pxlKtxAlbum.widgetVisible(PXLWidgetType.horizontal);
        pxlKtxAlbum.widgetVisible("<Customized name>");
        ```

### Load More
- To fire a load more event, simply call the `loadMore` method of the PXLKtxAlbum AFTER `pxlKtxAlbum.getNextPage()` method with [pxlKtxAlbum.lastPageLoaded >= 2] is successfully called, a "Load More" analytics event will be fired containing all of the necessary analytics information.
See the onComplete function in GalleryFragment.java for an example.
- On calls to pxlKtxAlbum.getNextPage() (except the first), a "Load More" analytics event will be fired automatically
    ```kotlin
    #!Kotlin.coroutines

    pxlKtxAlbum.loadMore();
    ```
- if you want to manually fire pxlKtxAlbum.loadMore(), first you must successfully call this pxlKtxAlbum.getNextPage(callLoadMoreAnalytics = true) to get content which does not fire loadMore(), then you manually fire pxlKtxAlbum.loadMore() in your app.

### Opened Lightbox
- To fire an opened ligtbox event, simply call the `openedLightbox` method of PXLKtxAlbum, and an "Opened Lightbox" event will be fired containing all of the necessary analytics information.

    ```kotlin
    #!Kotlin.coroutines

    pxlKtxAlbum.openedLightbox(pxlPhoto.albumPhotoId)
    pxlKtxAlbum.openedLightbox(pxlPhoto)
    ```

### Action Clicked
- To fire an action clicked event, simply call the `actionClicked` method of PXLKtxAlbum that the action click is being driven from and pass in the URL of the link that the user is being redirected to.  An "Action Clicked" event will be fired containing all of the necessary analytics information.

    ```kotlin
    #!Kotlin.coroutines

    pxlKtxAlbum.actionClicked(pxlPhoto.albumPhotoId, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");
    pxlKtxAlbum.actionClicked(pxlPhoto, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");

    ```
## Ecommerce Analytics
- Option 1: Use PXLKtxAlbum
    ```kotlin
    #!kotlin

    val pxlKtxAlbum = PXLKtxAlbum(context)
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
    #!Kotlin.coroutines

    pxlKtxAlbum.addToCart("sku123", "123", 4);
    ```
    Or:
    ```kotlin
    #!Kotlin.coroutines
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
    #!Kotlin.coroutines

    val cartContents: ArrayList<HashMap<String, Any>> = ArrayList()
    val cart1: HashMap<String, Any> = HashMap()
    cart1["price"] = "123"
    cart1["product_sku"] = BuildConfig.PIXLEE_SKU
    cart1["quantity"] = "4"
    cartContents.add(cart1)
    // you can add more to cartContents if needed


    pxlKtxAlbum.conversion(cartContents = cartContents, cartTotal = "123", cartTotalQuantity = 4)
    ```
    OR:
    ```kotlin
    #!Kotlin.coroutines

    ktxAnalyticsDataSource.conversion(cartContents = cartContents, cartTotal = "123", cartTotalQuantity = 4)
    ```