package com.github.aakumykov.player_service_619;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.aakumykov.player_service.PlayerService;
import com.github.aakumykov.player_service.PlayerState;
import com.github.aakumykov.player_service.SoundItem;
import com.github.aakumykov.player_service.SoundPlayer;
import com.github.aakumykov.player_service_619.databinding.ActivityMainBinding;
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final int PICK_FILE_REQUEST_CODE = R.id.pick_file_request_code;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Nullable private SoundPlayer mSoundPlayer;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.pickFileButton.setOnClickListener(v -> pickFile());
        mBinding.playDownloadsButton.setOnClickListener(v -> playFilesFromDownloads());
        mBinding.playMusicButton.setOnClickListener(v -> playFilesFromMusic());

        mBinding.playPauseButton.setOnClickListener(v -> {

            if (null == mSoundPlayer)
                return;

            if (mSoundPlayer.isStopped()) {
                mSoundPlayer.playAgain();
                return;
            }

            if (mSoundPlayer.isPlaying())
                mSoundPlayer.pause();
            else if (mSoundPlayer.isPaused())
                mSoundPlayer.resume();
            else
                Log.e(TAG, "Плеер ни играет, ни на паузе О_о");
        });

        mBinding.stopButton.setOnClickListener(v -> {
            if (null != mSoundPlayer)
                mSoundPlayer.stop();
        });

        mBinding.skipPrevButton.setOnClickListener(v -> {
            if (null != mSoundPlayer)
                mSoundPlayer.skipToPrev();
        });

        mBinding.skipNextButton.setOnClickListener(v -> {
            if (null != mSoundPlayer)
                mSoundPlayer.skipToNext();
        });

        startService(PlayerService.getIntent(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPlayerService();
        askForNotificationsPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPlayerService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (PICK_FILE_REQUEST_CODE == requestCode)
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
    public void onServiceConnected(ComponentName name, IBinder binder) {

        PlayerService.setContentIntent(binder, createContentIntent());

        mSoundPlayer = PlayerService.getSoundPlayer(binder);

        if (null != mSoundPlayer)
            mSoundPlayer.getPlayerStateLiveData().observe(this, this::onPlayerStateChanged);
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (null != mSoundPlayer) {
            mSoundPlayer.getPlayerStateLiveData().removeObserver(this::onPlayerStateChanged);
            mSoundPlayer = null;
        }
    }


    void pickFile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            MainActivityPermissionsDispatcher.pickFileOldWithPermissionCheck(this);
        else
            MainActivityPermissionsDispatcher.pickFileNewWithPermissionCheck(this);
    }

    @NeedsPermission({android.Manifest.permission.READ_EXTERNAL_STORAGE})
    void pickFileOld() {
        openFileSelectionDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NeedsPermission({Manifest.permission.READ_MEDIA_AUDIO})
    void pickFileNew() {
        openFileSelectionDialog();
    }

    private void openFileSelectionDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }


    private void playFilesFromDownloads() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            MainActivityPermissionsDispatcher.playFilesFromDownloadsOldWithPermissionCheck(this);
        else
            MainActivityPermissionsDispatcher.playFilesFromDownloadsNewWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void playFilesFromDownloadsOld() {
        playFilesFromDownloadsReal();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NeedsPermission({Manifest.permission.READ_MEDIA_AUDIO})
    void playFilesFromDownloadsNew() {
        playFilesFromDownloadsReal();
    }

    private void playFilesFromDownloadsReal() {
        playFileFromDir("mp3",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }


    private void playFilesFromMusic() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            MainActivityPermissionsDispatcher.playFilesFromMusicOldWithPermissionCheck(this);
        else
            MainActivityPermissionsDispatcher.playFilesFromMusicNewWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void playFilesFromMusicOld() {
        playFilesFromMusicReal();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NeedsPermission({Manifest.permission.READ_MEDIA_AUDIO})
    void playFilesFromMusicNew() {
        playFilesFromMusicReal();
    }

    private void playFilesFromMusicReal() {
        playFileFromDir("mp3",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
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

        if (null != mSoundPlayer)
            mSoundPlayer.play(new SoundItem(data));
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

        return Stream.of(files).sorted().collect(Collectors.toList());
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


    private void onPlayerStateChanged(PlayerState playerState) {

        final PlayerState.Mode mode = playerState.getMode();

        switch (mode) {
            case IDLE:
                onPlayerIdle();
                break;
            case WAITING:
                onPlayerWaiting();
                break;
            case PLAYING:
            case RESUMED:
                onPlayerPlaying(playerState);
                break;
            case PAUSED:
                onPlayerPaused((PlayerState.Paused) playerState);
                break;
            case STOPPED:
                onPlayerStopped();
                break;
            case ERROR:
                onPlayerError((PlayerState.Error) playerState);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное значение: "+mode);
        }
    }

    private void onPlayerIdle() {

    }

    private void onPlayerWaiting() {
        hideError();
        showTrackTitle(getString(R.string.player_waiting));
        showWaitingButton();
    }

    private void onPlayerPlaying(PlayerState playerState) {
        hideError();
        showTrackTitle(playerState.getTrackTitle());
        showPauseButton();
    }

    private void onPlayerPaused(PlayerState.Paused playerState) {
        showTrackTitle(playerState.getTrackTitle());
        showPlayPauseButton();
    }

    private void onPlayerStopped() {
        hideTrackTitle();
        showPlayButton();
    }

    private void onPlayerError(PlayerState.Error errorPlayerState) {
        showError(ExceptionUtils.getErrorMessage(errorPlayerState.getError()));
        showPlayButton();
    }

    private void showWaitingButton() {
        mBinding.playPauseButton.setImageResource(R.drawable.ic_waiting);
    }

    private void showPlayButton() {
        mBinding.playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    private void showPauseButton() {
        mBinding.playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    private void showPlayPauseButton() {
        mBinding.playPauseButton.setImageResource(R.drawable.ic_play_pause);
    }

    private void showTrackTitle(String trackTitle) {
        mBinding.infoView.setText(trackTitle);
    }

    private void hideTrackTitle() {
        mBinding.infoView.setText("");
    }


    private Intent createContentIntent() {
        return new Intent(this, MainActivity.class);

    }


    private void askForNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            MainActivityPermissionsDispatcher.requestAllowingNotificationsWithPermissionCheck(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NeedsPermission(Manifest.permission.POST_NOTIFICATIONS)
    void requestAllowingNotifications() {}
}