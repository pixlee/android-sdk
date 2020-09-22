# Kotlin

# Table of Content
- [Initialize SDK](#Initialize-SDK)
    - [Register Pixlee credentials](#Register-Pixlee-credentials)
    - [Get PXLClient](#Get-PXLClient)
- [Album Features](#Album-Features)
    - [Initiate Album or Product](#Initiate-Album-or-Product)
    - Option 1: [Get Album photos](#Get-Album-photos)
    - Option 2: [Get Product photos](#Get-Product-photos)
- [How to get image urls](#How-to-get-image-urls)

# Initialize SDK
### You must do this before using this SDK!!
#### Register Pixlee credentials
- Before accessing any Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize. The simplest way for this is you put this code in your Application class:
    ```
    // If you need only to use @Get APIs
    #!kotlin
    
    PXLClient.initialize(<PIXLEE API KEY>)
    ```
    Or:
    ```
    // If you need to use both @Get and @Post APIs
    #!kotlin
    
    PXLClient.initialize(<PIXLEE API KEY>, <PIXLEE SECRET KEY>)
    ```
#### Get PXLKtxAlbum
- You can then use the singleton instance to make calls against the Pixlee API:
    ```
    #!kotlin
    val pxlAlbum = PXLKtxAlbum(context)
    ```
    Or:
    ```
    #!kotlin
    val client = PXLClient.getInstance(context);
    val pxlAlbum = PXLKtxAlbum(client)
    ```
    Or:
    ```
    #!kotlin
    val client = PXLClient.getInstance(context);
    val ktxBasicDataSource = client.ktxBasicRepo
    val ktxAnalyticsDataSource = client.ktxAnalyticsRepo
    val pxlAlbum = PXLKtxAlbum(ktxBasicDataSource, ktxAnalyticsDataSource)
    ```
## Album Features
To prepare to load the photos in an album, you'll need the codes below
#### Option 1: Album
```
#!kotlin
val pxlAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Album("<your ALBUM ID>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
#### Option 2: Product
```
#!kotlin
val pxlAlbum = PXLKtxAlbum(context)
val searchId = PXLKtxBaseAlbum.SearchId.Product("<your Product's SKU>")
pxlKtxAlbum.params = PXLKtxBaseAlbum.Params(
     searchId = searchId
)
```
### Get Photos
Get the first page
```
pxlAlbum.getFirstPage()
```

Get the next pages
```
pxlAlbum.getNextPage()
```
Advanced Search options
```
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
```
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
    
