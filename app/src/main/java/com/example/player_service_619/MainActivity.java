package com.example.player_service_619;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.player_service.PlayerService;
import com.example.player_service.SoundItem;
import com.example.player_service.SoundPlayer;
import com.example.player_service_619.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final int PICK_FILE = R.id.pick_file_request_code;
    @Nullable private SoundPlayer mSoundPlayer;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.pickFileButton.setOnClickListener(v ->
                MainActivityPermissionsDispatcher.pickFileWithPermissionCheck(MainActivity.this));

        mBinding.playDownloadsButton.setOnClickListener(v ->
                MainActivityPermissionsDispatcher.playFilesFromDownloadsWithPermissionCheck(MainActivity.this));

        mBinding.playMusicButton.setOnClickListener(v ->
            MainActivityPermissionsDispatcher.playFilesFromMusicWithPermissionCheck(MainActivity.this));

        startService(PlayerService.getIntent(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPlayerService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPlayerService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (PICK_FILE == requestCode)
            processPickedFile(resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mSoundPlayer = (SoundPlayer) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mSoundPlayer = null;
    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void playFilesFromDownloads() {
        playFileFromDir("mp3",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void playFilesFromMusic() {
        playFileFromDir("mp3",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
    }


    private void processPickedFile(int resultCode, @Nullable Intent intent) {
        if (RESULT_OK != resultCode)
            return;

        if (null == intent) {
            showError("Intent == null");
            return;
        }

        Uri data = intent.getData();
        if (null == data) {
            showError("Не найдены данные");
            return;
        }

        if (null != mSoundPlayer) {
            String path = data.getPath();
            mSoundPlayer.play(new SoundItem(path, path, new File(path)));
        }
    }


    private void playFileFromDir(@NonNull final String fileExtension, @NonNull final File directory) {
        if (null != mSoundPlayer)
            mSoundPlayer.play(
                    listFilesFromDir(fileExtension, directory).stream()
                    .map(file -> new SoundItem(file.getName(), file.getName(), file))
                    .collect(Collectors.toList()));
        else
            showError("Не найден SoundPlayer");
    }

    private List<File> listFilesFromDir(final String fileExtension, final File dir) {

        Pattern filePatten = Pattern.compile("^.+\\.\\s*" + fileExtension + "\\s*", Pattern.CASE_INSENSITIVE);

        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(filePatten.pattern());
            }
        });

        if (null == files)
            return new ArrayList<>();

        return new ArrayList<>(Arrays.asList(files));
    }


    private void showError(final String errorMsg) {
        mBinding.errorView.setText(errorMsg);
        mBinding.errorView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        mBinding.errorView.setText("");
        mBinding.errorView.setVisibility(View.GONE);
    }


    private void bindPlayerService() {
        bindService(PlayerService.getIntent(this), this, 0);
    }

    private void unbindPlayerService() {
        unbindService(this);
    }
}