package com.example.player_service;

import static com.github.aakumykov.argument_utils.ArgumentUtils.checkNotNull;

import androidx.annotation.NonNull;

import java.io.File;

public class SoundItem {

    @NonNull private final String mId;
    @NonNull private final File mFile;
    @NonNull private final String mTitle;

    public SoundItem(@NonNull String id, @NonNull String title, @NonNull File file) throws NullPointerException {
        mId = checkNotNull(id);
        mTitle = checkNotNull(title);
        mFile = checkNotNull(file);
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public File getFile() {
        return mFile;
    }

    @NonNull
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
