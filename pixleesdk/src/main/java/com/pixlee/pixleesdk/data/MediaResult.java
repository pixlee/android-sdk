package com.pixlee.pixleesdk.data;

import com.squareup.moshi.Json;

/**
 * This is the response data of @Post https://distillery.pixlee.com/api/v2/media
 * Created by sungjun on 2020-02-05.
 */
public class MediaResult {
    @Json(name = "album_photo_id")
    public String albumPhotoId;

    @Json(name = "connected_user_id")
    public String connectedUserId;
}
