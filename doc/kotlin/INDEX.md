# Kotlin

# Table of Content
- [Initialize SDK your Application level](#Initialize-SDK-your-Application-level)
    - [Register Pixlee credentials](#Register-Pixlee-credentials)
- [API: Getting Album/PDP contents](API.md)
- [API: Getting a PXLPhoto with an album photo id](API.md#get-pxlphoto-with-an-albumphotoid)
- [API: Analytics for album/product](API.md#album-analytics)
- [API: Analytics for Ecommerce](API.md#ecommerce-analytics)
- [UI Components](UI.md)

# Initialize SDK in your Application level
You must do this before using this SDK!!
### Register Pixlee credentials
- Before accessing any Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize. The simplest way for this is you put this code in your Application class:
    ```kotlin
    // If you need only to use @Get APIs
    #!kotlin
    
    PXLClient.initialize(<PIXLEE API KEY>)
    ```
    Or:
    ```kotlin
    // If you need to use both @Get and @Post APIs
    #!kotlin
    
    PXLClient.initialize(<PIXLEE API KEY>, <PIXLEE SECRET KEY>)
    ```
### (Optional) Multi-region
- if you use multi-region, you can set your region id here to get photos, a photo, and products available in the region.
```kotlin
#!kotlin
PXLClient.regionId = your region id <--- set it if you use multi-region.
```

### (Optional) Automatic Analytics
```swift
#!swift
PXLClient.autoAnalyticsEnabled = true // (Optional) <----- This activates auto-analytics on PXLPhotoRecyclerView, PXLPhotoRecyclerViewInGrid and PXLPhotoProductView.
```
- This is to delegate this SDK to fire necessary analytics events for you. If you don't want to use this, you can just ignore this part.
- if you use PXLPhotoRecyclerView or PXLPhotoRecyclerViewInGrid, you need an extra setting [Document: Automatic analytics of PXLGridView](#automatic-analytics-of-pxlgridview).
- Which analytics do we fire for you?:
    - `loadmore` event: when you use `PXLClient.sharedClient.loadNextPageOfPhotosForAlbum(album: album)` and load the second or the next pages, we fire `loadmore` events for you.
    - `openedWidget` event: if you implemente [Document: Automatic analytics of PXLGridView](#automatic-analytics-of-pxlgridview) and try to display the PXLGridView with a number of PXLPhotos on the screen we fire `openedWidget`.
    - `widgetVisible` event: if you implemente [Document: Automatic analytics of PXLGridView](#automatic-analytics-of-pxlgridview) and try to display the PXLGridView with a number of PXLPhotos on the screen we fire `widgetVisible`.
    - `openedLightbox` event: when you display [PXLPhotoProductView](#automatic-analytics-of-pxlphotoproductview) with a PXLPhoto on the screen, we fire `openedLightbox`.
- **Notice**: you can see the fired events on Logcat. If there's a problem of your setting, you can see error messages we display in Logat.