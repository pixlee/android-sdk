/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pixlee.pixleesdk.video;

import android.content.Context;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.File;
import java.util.concurrent.Executors;

/** Utility methods for the demo app. */
public final class ExoPlayerUtil {
  private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
  public static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

  private static @MonotonicNonNull File downloadDirectory;
  private static @MonotonicNonNull Cache downloadCache;

  /** Returns whether extension renderers should be used. */
  public static boolean useExtensionRenderers() {
    return false;
  }

  public static RenderersFactory buildRenderersFactory(
      Context context, boolean preferExtensionRenderer) {
    @DefaultRenderersFactory.ExtensionRendererMode
    int extensionRendererMode =
        useExtensionRenderers()
            ? (preferExtensionRenderer
                ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
    return new DefaultRenderersFactory(context.getApplicationContext(), extensionRendererMode);
  }

  private static synchronized File getDownloadDirectory(Context context) {
    if (downloadDirectory == null) {
      downloadDirectory = context.getExternalFilesDir(/* type= */ null);
      if (downloadDirectory == null) {
        downloadDirectory = context.getFilesDir();
      }
    }
    return downloadDirectory;
  }

  private ExoPlayerUtil() {}

  /**
   * Returns a new DataSource factory.
   *
   * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
   *     DataSource factory.
   * @return A new DataSource factory.
   */
  public static DataSource.Factory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
    return buildDataSourceFactory(context, useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /** Returns a {@link DataSource.Factory}. */
  public static DataSource.Factory buildDataSourceFactory(Context context, TransferListener<? super DataSource> listener) {
    String userAgent = Util.getUserAgent(context, "PXLExoPlayer");
    DefaultDataSourceFactory upstreamFactory =
            new DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(userAgent, listener));
    return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context));
  }

  /** Returns a {@link HttpDataSource.Factory}. */
  public static HttpDataSource.Factory buildHttpDataSourceFactory(
          String userAgent,
          TransferListener<? super DataSource> listener) {
    return new DefaultHttpDataSourceFactory(userAgent, listener);
  }

  private static  synchronized Cache getDownloadCache(Context context) {
    if (downloadCache == null) {
      File downloadContentDirectory = new File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY);
      downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
    }
    return downloadCache;
  }

  private static CacheDataSourceFactory buildReadOnlyCacheDataSource(
          DefaultDataSourceFactory upstreamFactory, Cache cache) {
    return new CacheDataSourceFactory(
            cache,
            upstreamFactory,
            new FileDataSourceFactory(),
            /* cacheWriteDataSinkFactory= */ null,
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            /* eventListener= */ null);
  }
}
