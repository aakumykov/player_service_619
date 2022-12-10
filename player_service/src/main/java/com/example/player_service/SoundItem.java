package com.example.player_service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class SoundItem {

    private final String mId;
    private final File mFile;
    private final String mTitle;

    public SoundItem(@NonNull String id, @NonNull String title, @Nullable File file) {
        mId = id;
        mTitle = title;
        mFile = file;
    }

    public String getId() {
        return mId;
    }

    public File getFile() {
        return mFile;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return "SoundItem{" +
                "mId='" + mId + '\'' +
                ", mFile=" + mFile +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }
}
