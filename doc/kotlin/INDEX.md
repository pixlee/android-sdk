# Kotlin

# Table of Content
- [Initialize SDK](#Initialize-SDK)
    - [Register Pixlee credentials](#Register-Pixlee-credentials)
- [API: Getting Album/PDP contents](API.md)
- [API: Getting a PXLPhoto with an album photo id](API.md#get-pxlphoto-with-an-albumphotoid)
- [API: Analytics for album/product](API.md#album-analytics)
- [API: Analytics for Ecommerce](API.md#ecommerce-analytics)
- [UI Components](UI.md)

# Initialize SDK
### You must do this before using this SDK!!
#### Register Pixlee credentials
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