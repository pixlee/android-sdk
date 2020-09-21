# Java

# Table of Content
- [Initialize SDK](#Initialize-SDK)
    - [Register Pixlee credentials](#Register-Pixlee-credentials)
    - [Get PXLClient](#Get-PXLClient)
- [Album Features](#Album-Features)
    - [Initiate Album or Product](#Initiate-Album-or-Product)
    - [Get Photos of a Product](#Get-Photos-of-a-Product)
    - [Get more Photos](#Get-more-Photos)
    - [Uploading Photos](#Uploading-Photos)
- [How to get image urls](#How-to-get-image-urls)
- [Album Analytics](#Album-Analytics)
    - [Opened Widget](#Opened-Widget)
    - [Load More](#Load-More)
    - [Opened Lightbox](#Opened-Lightbox)
    - [Action Clicked](#Action-Clicked)
- [Ecommerce Analytics](#Ecommerce-Analytics)
    - [Add To Cart](#Add-To-Cart)
    - [Conversion](#Conversion)
- [UI components](#UI-components)
    - [Image & Video Viewer with PXLPhoto](#Image-and-Video-Viewer-with-PXLPhoto)
- Migration
    - [from 1.0.6 or older](doc/migration/1.0.6.md)

# Initialize SDK
### You must do this before using this SDK!!
#### Register Pixlee credentials
- Before accessing any Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize:
    ```
    // If you need only to use @Get APIs
    #!java
    
    PXLClient.initialize(<PIXLEE API KEY>);
    ```
    Or:
    ```
    // If you need to use both @Get and @Post APIs
    #!java
    
    PXLClient.initialize(<PIXLEE API KEY>, <PIXLEE SECRET KEY>);
    ```
#### Get PXLClient
- You can then use the singleton instance to make calls against the Pixlee API:
    ```
    #!java
    
    PXLClient client = PXLClient.getInstance(context);
    ```
## Album Features
### Initiate Album or Product
- #### Option 1: Album
    To load the photos in an album, you'll want to use the `PXLAlbum` class. Instantiate one with your album ID and client:
        
    ```
    #!java

    PXLBaseAlbum album = new PXLAlbum(<ALBUM ID>, client);
    ```
    Or:
    ```
    #!java
    
    PXLBaseAlbum album = new PXLAlbum(<ALBUM ID>, client.getBasicRepo(), client.getAnalyticsRepo());
    ```
- #### Option 2: Product    
    To load the photos in an Product album, you'll want to use the `PXLPdpAlbum` class. Instantiate one with your desired sku and client:
        
    ```
    #!java
    
    PXLBaseAlbum album = new PXLPdpAlbum(<SKU>, client);
    ```
    Or:
    ```
    #!java
    
    PXLBaseAlbum album = new PXLPdpAlbum(<SKU>, client.getBasicRepo(), client.getAnalyticsRepo());
    ```
#### Get more Photos
- You can then set sort and filter options if desired and use `loadNextPageOfPhotos` to kick off the async request.   
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
- Each successive call of `loadNextPageOfPhotos` will load the next page of photos. Be sure to set all of your request options (filters, sort, etc) before calling `loadNextPageOfPhotos`.  See the source for more implementation details.
  Once an album has loaded photos from the server, it will instantiate `PXLBaseAlbum` objects that can be consumed by your UI. `PXLBaseAlbum` exposes all of the data for a photo available through the Pixlee API and offers several image url sizes depending on your needs.
    
#### Uploading Photos
- Prerequisite:
    - [Initiate Album or Product](#Initiate-Album-or-Product)
    - prepare PXLBaseAlbum and declare a variable as 'album' 
- Option 1: Upload an image url
    
    ```
    #!java
    album.uploadImage(
                "<title>",
                "<email>",
                "<name>",
                "<image URL>",
                <true/false>,
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        // process the result 
                    }

                    @Override
                    public void onError(String error) {
                        // error
                    }
                });
    ```
- Option 2: Upload an image file
    ```
    album.uploadLocalImage(
                "<title>",
                "<email>",
                "<name>",
                <true/false>,
                "<local image path>",
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        // process the result
                    }

                    @Override
                    public void onError(String error) {
                        // error
                    }
                });
    ```    

## How to get image urls
Some imageURL fields can be empty or null depending on its data's status. In order to get appropriate images, you can use this method.
```
#!java
photo.getUrlForSize(PXLPhotoSize.ORIGINAL)
photo.getUrlForSize(PXLPhotoSize.BIG)
photo.getUrlForSize(PXLPhotoSize.MEDIUM)
photo.getUrlForSize(PXLPhotoSize.THUMBNAIL)
```
    
## Album Analytics


#### Opened Widget & Widget Visible
Be aware of the difference between **Opened Widget** and **Widget Visible**. (Need a sample code. Check the demo app in the project)

There is an order of firing these two APIs.
1. **Opened Widget**: You should fire this when firing the api is done and loading the photo data into your own view for the widget is complete.
2. **Widget Visible**: **Opened Widget** should be fired first. Then, you can fire this when your own view for the widget started to be visible on the screen.

- ##### Opened Widget
    - To fire this event, simply call the `openedWidget` method of the PXLAlbum or PXLPdpAlbum AFTER data has been returned from the first call of the `loadNextPageOfPhotos` method, and an "Opened Widget" event will be fired containing all of the necessary analytics information.
    See the onComplete function in GalleryFragment.java for an example.
    
        ```
        #!java
        
        album.openedWidget(PXLWidgetType.photowall);
        album.openedWidget(PXLWidgetType.horizontal);
        album.openedWidget("<Customized name>"); 
        ```
    
- #### Widget Visible
    - To fire this event, simply call the `widgetVisible` method of the PXLAlbum or PXLPdpAlbum AFTER data has been returned from the first call of the `loadNextPageOfPhotos` method, and an "Widget Visible" event will be fired containing all of the necessary analytics information.

        ```
        #!java
        
        album.widgetVisible(PXLWidgetType.photowall);
        album.widgetVisible(PXLWidgetType.horizontal);
        album.widgetVisible("<Customized name>"); 
        ```

#### Load More
- To fire a load more event, simply call the `loadMore` method of the PXLAlbum or PXLPdpAlbum AFTER data has been returned from calls via the 'loadNextPageOfPhotos' method, a "Load More" analytics event will be fired containing all of the necessary analytics information.
See the onComplete function in GalleryFragment.java for an example.
- On calls to loadNextPageOfPhotos (except the first), a "Load More" analytics event will be fired automatically
    ```
    #!java
    
    album.loadMore();
    ```

#### Opened Lightbox
- To fire an opened ligtbox event, simply call the `openedLightbox` method of the PXLBaseAlbum that is being opened, and an "Opened Lightbox" event will be fired containing all of the necessary analytics information.

    ```
    #!java
    
    album.openedLightbox(photo.albumPhotoId)
    album.openedLightbox(photo)
    ```

#### Action Clicked
- To fire an action clicked event, simply call the `actionClicked` method of the PXLBaseAlbum that the action click is being driven from and pass in the URL of the link that the user is being redirected to.  An "Action Clicked" event will be fired containing all of the necessary analytics information.

    ```
    #!java
    
    album.actionClicked(photo.albumPhotoId, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");
    album.actionClicked(photo, "https://ca.puma.com/en/ca/pd/clyde-court-core-basketball-shoes/191712.html");

    ```
## Ecommerce Analytics
- initialize:
    ```
    #!java
    
    PXLAnalytics analytics = new PXLAnalytics(client);
    ```
    Or:
    ```
    #!java
    
    PXLAnalytics analytics = new PXLAnalytics(client.getAnalyticsRepo());
    ```
    
#### Add To Cart
- To fire an Add To Cart event, simply call the `addToCart` method of the PXLAnalytics object with the necessary parameters, and an "Add To Cart" event will be fired containing all of the necessary analytics information.
The parameters for this method are:
    - [Required] sku  (String)
    - [Required] price (String)
    - [Required] quantity (Integer)
    - [Optional] currency (String)

    ```
    #!java
    analytics.addToCart("sku123", "123", 4);
    ```

#### Conversion
- To fire a Conversion event, simply call the `conversion` method of the PXLAnalytics object with the necessary parameters, and a "Conversion" event will be fired containing all of the necessary analytics information.
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
    analytics.conversion(cartContents, "123", 4);
    ```

## UI components
#### Image and Video Viewer with PXLPhoto
- after receiving PXLPhoto list via PXLBaseAlbum.loadNextPageOfPhotos(...), you can launch watch the content using PXLPhotoViewerActivity. Depending on its content_type, PXLPhotoViewerActivity will play a video or display a photo.
- you can use the activity using the code here
    ```
    PXLPhotoViewerActivity.launch(getContext(), pxlPhoto, "photo name");
    PXLPhotoViewerActivity.launch(getContext(), pxlPhoto);
    ```

# License
pixlee-android-sdk is available under the MIT license.
