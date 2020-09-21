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
- Before accessing any Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize:
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
#### Get PXLClient
- You can then use the singleton instance to make calls against the Pixlee API:
    ```
    #!kotlin
    
    PXLClient client = PXLClient.getInstance(context);
    ```
## Album Features
### Initiate Album or Product
#### Option 1: Get Album photos
To load the photos in an album, you'll need the codes below
        
```
#!kotlin
val albumId = <your album id>
val ktxBasicDataSource: KtxBasicDataSource = client.ktxBasicRepo
var searchSetting: SearchSetting? = null
var perPage = 30
var filterOptions: PXLAlbumFilterOptions? = null
var sortOptions: PXLAlbumSortOptions? = null
var lastPageLoaded: Int = 0

// this is a suspend method. please run it within
//      - viewModelScope.launch(handler) { /* here */ }
//      - viewLifecycleOwner.lifecycleScope.launch { /* here */  }
lastPageLoaded = 1
val result = ktxBasicDataSource.getPhotosWithID(albumId, filterOptions, sortOptions, perPage, lastPageLoaded)
```
- Get More photos: Be sure that you have the same (roductSKU, filterOptions, sortOptions, perPage) to get the right responses.
    ```
    #!kotlin
    ++lastPageLoaded
    val result = ktxBasicDataSource.getPhotosWithID(albumId, filterOptions, sortOptions, perPage, lastPageLoaded)
    ```
#### Option 2: Get Product photos
To load the photos in an Product album, you'll need the codes below

```
#!kotlin
val productSKU = <your product's sku>
val ktxBasicDataSource: KtxBasicDataSource = client.ktxBasicRepo
var searchSetting: SearchSetting? = null
var perPage = 30
var filterOptions: PXLAlbumFilterOptions? = null
var sortOptions: PXLAlbumSortOptions? = null
var lastPageLoaded: Int = 0

// this is a suspend method. please run it within
//      - viewModelScope.launch(handler) { /* here */ }
//      - viewLifecycleOwner.lifecycleScope.launch { /* here */  }
lastPageLoaded = 1
val result = ktxBasicDataSource.getPhotosWithSKU(productSKU, filterOptions, sortOptions, perPage, lastPageLoaded)
```
- Get More photos: Be sure that you have the same (roductSKU, filterOptions, sortOptions, perPage) to get the right responses.
    ```
    #!kotlin
    ++lastPageLoaded
    val result = ktxBasicDataSource.getPhotosWithSKU(productSKU, filterOptions, sortOptions, perPage, lastPageLoaded)
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
    
