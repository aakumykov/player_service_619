package com.github.aakumykov.player_service;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.UUID;

public class SoundItem {

    @NonNull private final String mId;
    @NonNull private final String mTitle;
    @NonNull private final Uri mFileUri;

    public SoundItem(@NonNull String id,
                     @NonNull String title,
                     @NonNull File file) throws NullPointerException
    {
        this(Uri.fromFile(file));
    }

    public SoundItem(@NonNull Uri fileUri) throws NullPointerException
    {
        this(UUID.randomUUID().toString(), uri2fileName(fileUri), fileUri);
    }

    private SoundItem(@NonNull String id,
                      @NonNull String title,
                      @NonNull Uri fileUri) throws NullPointerException
    {
        mId = id;
        mTitle = title;
        mFileUri = fileUri;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Uri getFileUri() {
        return mFileUri;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull @Override
    public String toString() {
        return "SoundItem{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mFileUri=" + mFileUri +
                '}';
    }


    private static String uri2fileName(Uri fileUri) {
        final String path = fileUri.getPath().trim();
        if ("".equals(path))
            return "";
        final String[] parts = path.split("/");
        return parts[parts.length-1];
    }
}
