# pixlee-android-sdk

This SDK makes it easy for Pixlee customers to find and download Pixlee images and albums.  There's a native wrapper to the Pixlee album API for Android, and there's also a demo app showing how easy it is to drop in and customize a UI.

## Getting Started

This repo includes both the Pixlee Android SDK and an example project to show you how it's used.  The examples included with this SDK are meant to be used in Android Studio to create a typical Android app.  These examples were created and tested in Android Studio 2.3 and can be included by following the directions below under "Including the Pixlee SDK."

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


You can then set sort and filter options if desired and use `loadNextPageOfPhotos` to kick off the async request.


```
#!java

PXLAlbumFilterOptions filterOptions = new PXLAlbumFilterOptions();
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

To help you get up and running quickly, we've also built an sample application featuring a grid view, list view, and detail view.  The adapters simply maintain an ArrayList of PXLPhoto, which is updated via calls to `loadNextPageOfPhotos`.  Since the data source contains the full PXLPhoto object, you can easily customize your own widgets to display the desired images and text.  The sample also implements a scroll listener which times calls to `loadNextPageOfPhotos` to provide the endless scroll effect.

### Including the Pixlee Android SDK
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
pixlee-android-sdk is available under the MIT license.