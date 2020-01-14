[![](https://jitpack.io/v/pixlee/android-sdk.svg)](https://jitpack.io/#pixlee/android-sdk)


# pixlee-android-sdk

This SDK makes it easy for Pixlee customers to find and download Pixlee images and albums.  There's a native wrapper to the Pixlee album API for Android, and there's also a demo app showing how easy it is to drop in and customize a UI.

## Getting Started

This repo includes both the Pixlee Android SDK and an example project to show you how it's used.  The examples included with this SDK are meant to be used in Android Studio to create a typical Android app.  These examples were created and tested in Android Studio 3.5.3 and can be included by following the directions below under "Including the Pixlee SDK."

### SDK

Before accessing the Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize:


```
#!java

PXLClient.initialize(<API KEY>);
```


You can then use the singleton instance to make calls against the Pixlee API:


```
#!java

PXLClient pxlClient = PXLClient.getInstance(context);
```


To load the photos in an album, you'll want to use the `PXLAlbum` class. Instantiate one with your album ID and context:


```
#!java

PXLAlbum pxlAlbum = new PXLAlbum(<ALBUM ID>, context);
```

To load the photos in an Product album, you'll want to use the `PXLPdpAlbum` class. Instantiate one with your desired sku and context:


```
#!java

PXLPdpAlbum pxlPdpAlbum = new PXLPdpAlbum(<SKU>, context);
```


You can then set sort and filter options if desired and use `loadNextPageOfPhotos` to kick off the async request.


```
#!java

PXLAlbumFilterOptions filterOptions = new PXLAlbumFilterOptions();
filterOptions.minTwitterFollowers = 1000;
filterOptions.minInstagramFollowers = 2000;
PXLAlbumSortOptions sortOptions = new PXLAlbumSortOptions();
sortOptions.sortType = PXLAlbumSortType.DYNAMIC;
sortOptions.descending = true;
album.setPerPage(15);
album.setFilterOptions(filterOptions);
album.setSortOptions(sortOptions);
album.loadNextPageOfPhotos(this);
```


Each successive call of `loadNextPageOfPhotos` will load the next page of photos. Be sure to set all of your request options (filters, sort, etc) before calling `loadNextPageOfPhotos`.  See the source for more implementation details.

Once an album has loaded photos from the server, it will instantiate `PXLPhoto` objects that can be consumed by your UI. `PXLPhoto` exposes all of the data for a photo available through the Pixlee API and offers several image url sizes depending on your needs.

#### Uploading Photos

If you wish to build your application with photo uploading capabilities, you must initialize the `PXLClient` with an additional parameter - your Pixlee secret key. You must set the secret key along with the API key when you call the static method initialize:

```
#!java

PXLClient.initialize(<API KEY>, <SECRET KEY>);
```

Now wen you want to upload a photo in your application, simply call the `uploadImage` method of the PXLAlbum object you are using.  This would look something like this:

```
#!java

album.uploadImage("test", "test@test.com", "testuser", "https://timedotcom.files.wordpress.com/2019/05/drake-nba-finals-warning.jpg", true);
```

### Analytics
#### Opened Widget
To fire an opened widget event, simply call the `openedWidget` method of the PXLAlbum or PXLPdpAlbum AFTER data has been returned from the first call of the `loadNextPageOfPhotos` method, and an "Opened Widget" event will be fired containing all of the necessary analytics information.
See the DataLoadedHandler function in SampleActiviy.java for an example.

```
#!java

album.openedWidget();
```

#### Load More
To fire a load more event, simply call the `loadMore` method of the PXLAlbum or PXLPdpAlbum AFTER data has been returned from calls via the 'loadNextPageOfPhotos' method, a "Load More" analytics event will be fired containing all of the necessary analytics information.
See the DataLoadedHandler function in SampleActiviy.java for an example.

```
#!java

album.loadMore();
```

#### Load More
On calls to loadNextPageOfPhotos (except the first), a "Load More" analytics event will be fired automatically

#### Opened Lightbox
To fire an opened ligtbox event, simply call the `openedLightbox` method of the PXLPhoto that is being opened, and an "Opened Lightbox" event will be fired containing all of the necessary analytics information.

```
#!java

photo.openedLightbox(context);
```

#### Action Clicked
To fire an action clicked event, simply call the `actionClicked` method of the PXLPhoto that the action click is being driven from and pass in the URL of the link that the user is being redirected to.  An "Action Clicked" event will be fired containing all of the necessary analytics information.

```
#!java

photo.actionClicked("https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html", context);
```

### Ecommerce Analytics

For triggering all ecommerce analytics events within your app, you'll want to use the `PXLAnalytics` class. Instantiate one with the application context:
```
#!Java

Context c = this.getApplicationContext();
PXLAnalytics pixleeAnalytics = new PXLAnalytics(c);
```

#### Add To Cart
To fire an Add To Cart event, simply call the `addToCart` method of the PXLAnalytics object with the necessary parameters, and an "Add To Cart" event will be fired containing all of the necessary analytics information.
The parameters for this method are:
- [Required] sku  (String)
- [Required] price (String)
- [Required] quantity (Integer)
- [Optional] currency (String)

```
#!java

pixleeAnalytics.addToCart("sku123", "123", 4);
```

#### Conversion
To fire a Conversion event, simply call the `conversion` method of the PXLAnalytics object with the necessary parameters, and a "Conversion" event will be fired containing all of the necessary analytics information.
The parameters for this method are:
- [Required] cartContents  (ArrayList<HashMap<String, Object>>)
- [Required] cartTotal (String)
- [Required] cartTotalQuantity (Integer)
- [Optional] orderId (String)
- [Optional] currency (String)

```
#!java

ArrayList<HashMap<String, Object>> cartContents = new ArrayList();
HashMap<String, Object> cart1 = new HashMap();
cart1.put("price", "123");
cart1.put("product_sku", "test123");
cart1.put("quantity", "4");

cartContents.add(cart1);
pixleeAnalytics.conversion(cartContents, "123", 4);
```

To help you get up and running quickly, we've also built an sample application featuring a grid view, list view, and detail view.  The adapters simply maintain an ArrayList of PXLPhoto, which is updated via calls to `loadNextPageOfPhotos`.  Since the data source contains the full PXLPhoto object, you can easily customize your own widgets to display the desired images and text.  The sample also implements a scroll listener which times calls to `loadNextPageOfPhotos` to provide the endless scroll effect. 
An example of the proper usage of an opened lightbox event is also included in the sample app!

### Including the Pixlee Android SDK
#### Using in a preexisting project
1. Open your existing project in Android Studio
2. Go to Import Module (File -> New -> Import Module)
3. Enter the path of the pixlee-android-sdk directory
4. Select the modules you would like to import and click Finish.

#### Loading the project as is
1. Start Android Studio and select **Import Project**
2. Select the path of the pixlee-android-sdk repo

### Running the Sample Application
1. After loading the project as described above, navigate to the app module.
2. If you would like to display your own album, navigate to the `createAlbum` method in SampleActivity.java. Replace the album id and api key with your own values (available from the Pixlee dashboard).
3. Run the module.

## License
pixlee-android-sdk is available under the MIT license.