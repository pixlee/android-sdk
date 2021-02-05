package android.webkit;

/**
 * Created by sungjun on 2/5/21.
 */
public class URLUtil {
    public static boolean isValidUrl(String url){
        if (url == null || url.length() == 0) {
            return false;
        }
        return url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://");
    }
}
