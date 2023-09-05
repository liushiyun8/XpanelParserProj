package com.zff.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    private static final int IMAGE_DISK_CACHE_MAX_SIZE = 250 * 1024 * 1024;

    @Override
    public void applyOptions(@NonNull final Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
//        builder.setDiskCache();
        builder.setMemoryCache(new LruResourceCache(5 * 1024 * 1024));
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //        super.registerComponents(context, glide, registry);
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
//        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
//        registry.replace(GlideUrl.class, InputStream.class, factory);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}