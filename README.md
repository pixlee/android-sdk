# pixlee-android-sdk

This SDK makes it easy for Pixlee customers to easily include Pixlee albums in their native Android apps. It includes a native wrapper to the Pixlee album API as well as some drop-in and customizable UI elements to quickly get you started.

## Getting Started

This repo includes both the Pixlee Android SDK and an example project to show you how it's used.

### SDK

Before accessing the Pixlee API, you must initialize the `PXLClient`. To set the API key, call the static method initialize:

  ```PXLClient.initialize("abcdefghi123456789");```

You can then use the singleton instance to make calls against the Pixlee API:

  ```PXLClient pxlClient = PXLClient.getInstance(context);```

To load the photos in an album, you'll want to use the `PXLAlbum` class. Instantiate one with your album ID and context:

  ```PXLAlbum pxlAlbum = new PXLAlbum(<ALBUM ID>, context);```

You can then set sort and filter options as necessary and use `loadNextPageOfPhotos` to kick off the async request.

  ```PXLAlbumFilterOptions filterOptions = new PXLAlbumFilterOptions();
     filterOptions.minTwitterFollowers = 1000;
     filterOptions.minInstagramFollowers = 2000;
     PXLAlbumSortOptions sortOptions = new PXLAlbumSortOptions();
     sortOptions.sortType = PXLAlbumSortType.PHOTORANK;
     sortOptions.descending = true;
     album.setPerPage(15);
     album.setFilterOptions(filterOptions);
     album.setSortOptions(sortOptions);
     album.loadNextPageOfPhotos(this);
  ```

Each successive call of `loadNextPageOfPhotos` will load the next page of photos. Be sure to set all of your request options (filters, sort, etc) before calling `loadNextPageOfPhotos`.  See the source for more implementation details.

Once an album has loaded photos from the server, it will instantiate `PXLPhoto` objects that can be consumed by your UI. `PXLPhoto` exposes all of the data for a photo available through the Pixlee API and offers several image url sizes depending on your needs.


------------todo: update

To help you quickly get started, we've also built an album view controller and photo detail view controller that can be used and customized in your app. `PXLAlbumViewController` uses a `UICollectionView` to display the photos in an album and includes a toggle to switch between a grid and list view. Use `albumViewControllerWithAlbumId:` to create an instance or set the `album` property if you need to create an instance through other means. Once the album has been set, you can call `loadNextPageOfPhotos` to start the loading process. The album view controller is set up to automatically load more pages of photos as the user scrolls, giving it an infinite scroll effect.

If a user taps on a photo in the `PXLAlbumViewController`, we present a detail view with `PXLPhotoDetailViewController`. You may present a detail view yourself by instantiating an instance of `PXLPhotoDetailViewController` and setting its `photo` property. The photo detail view is configured to display:
* the large photo
* the username of the poster
* a timestamp showing when the photo was posted
* the platform source of the photo (e.g. Instagram)
* the photo's caption (if one is available)
* any products associated with that photo (displayed as a horizontal list of products)

### Including Pixlee SDK
##### If you're building for iOS, tvOS, or watchOS
1. Create a Cartfile that lists the frameworks you’d like to use in your project.
1. Run `carthage update`. This will fetch dependencies into a Carthage/Checkouts folder, then build each one or download a pre-compiled framework.
1. On your application targets’ “General” settings tab, in the “Linked Frameworks and Libraries” section, drag and drop each framework you want to use from the Carthage/Build folder on disk.
1. On your application targets’ “Build Phases” settings tab, click the “+” icon and choose “New Run Script Phase”. Create a Run Script in which you specify your shell (ex: `/bin/sh`), add the following contents to the script area below the shell:

  ```sh
    /usr/local/bin/carthage copy-frameworks
      ```

        and add the paths to the frameworks you want to use under “Input Files”, e.g.:

	  ```
	    $(SRCROOT)/Carthage/Build/iOS/Box.framework
	      $(SRCROOT)/Carthage/Build/iOS/Result.framework
	        $(SRCROOT)/Carthage/Build/iOS/ReactiveCocoa.framework
		  ```
		    This script works around an [App Store submission bug](http://www.openradar.me/radar?id=6409498411401216) triggered by universal binaries and ensures that necessary bitcode-related files and dSYMs are copied when archiving.

		    With the debug information copied into the built products directory, Xcode will be able to symbolicate the stack trace whenever you stop at a breakpoint. This will also enable you to step through third-party code in the debugger.

		    When archiving your application for submission to the App Store or TestFlight, Xcode will also copy these files into the dSYMs subdirectory of your application’s `.xcarchive` bundle.

		    ### Example

		    To run the example project, clone the repo, and run `carthage update` from the Example directory first. Then in `PXLAppDelegate.m` set `PXLClientAPIKey` to your API key (available from the Pixlee dashboard). Then in `PXLExampleAlbumViewController.m` set the album id that you wish to display as `PXLAlbumIdentifier`.

		    To run the project, open example.xcodeproj in Xcode.

		    Run the project and you should see a grid of photos from that album.

		    ## License

		    pixlee-ios-sdk is available under the MIT license.
