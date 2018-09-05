package com.dyhdyh.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 基于系统播放器的音频播放控制器
 *
 * @author dengyuhan
 *         created 2018/9/5 15:18
 */
public class SystemAudioPlayerController implements AudioPlayerController, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {
    private MediaPlayer mediaPlayer;
    private Handler mMainHandler;

    /**
     * 进度回调的间隔
     */
    private long mProgressCallbackDelay = 1000;

    /**
     * 播放的计时器
     */
    private Timer mPlayerTimer;

    private boolean mPrepareCompleted;

    public SystemAudioPlayerController() {
        mMainHandler = new Handler();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
    }

    @Override
    public void start() {
        try {
            if (mPrepareCompleted) {
                startPlayerAndTimer();
            } else {
                mediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDataSource(String path) throws IOException {
        mPrepareCompleted = false;
        mediaPlayer.setDataSource(path);

    }

    @Override
    public void pause() {
        pausePlayerAndTimer();
    }

    @Override
    public void stop() {
        stopPlayerAndTimer();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        try {
            mediaPlayer.seekTo((int) time);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPrepareCompleted = true;
        startPlayerAndTimer();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("--------->", "onCompletion");
        stopTimer();
        AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onAutoCompletion();
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                float percentFloat = (float) percent / 100;
                player.onBufferingUpdate(percentFloat);
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onSeekComplete();
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        Log.d("--------->", "onError--->" + what + "-->" + extra);
        AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onError(what, extra);
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        Log.d("--------->", "onInfo--->" + what + "-->" + extra);
        AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    if (player.getCurrentState() == AbstractAudioPlayer.STATE_PREPARING
                            || player.getCurrentState() == AbstractAudioPlayer.STATE_PREPARING_CHANGING_URL) {
                        player.onPrepared();
                    }
                } else {
                    player.onInfo(what, extra);
                }
            }
        });
        return false;
    }

    /**
     * 启动播放器和计时器
     */
    private void startPlayerAndTimer() {
        mediaPlayer.start();

        if (mPlayerTimer == null) {
            mPlayerTimer = new Timer();
        }
        mPlayerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final long currentPosition = getCurrentPosition();
                if (currentPosition >= getDuration()) {
                    stopTimer();
                }
                AudioPlayerManager.runCurrentPlayer(mMainHandler, new CurrentPlayerRunnable() {
                    @Override
                    public void run(AbstractAudioPlayer player) {
                        player.onPlayerProgress(currentPosition);
                    }
                });
            }
        }, 0, mProgressCallbackDelay);
    }

    /**
     * 暂停播放器和计时器
     */
    private void pausePlayerAndTimer() {
        mediaPlayer.pause();

        stopTimer();
    }

    /**
     * 停止播放器和计时器
     * <p>MediaPlayer调用stop()后再调用start(),需要重新prepare()</p>
     */
    private void stopPlayerAndTimer() {
        mediaPlayer.stop();

        mPrepareCompleted = false;
        stopTimer();
    }

    private void stopTimer() {
        if (mPlayerTimer != null) {
            mPlayerTimer.cancel();
            mPlayerTimer = null;
        }
    }

    public void setProgressCallbackDelay(long progressCallbackDelay) {
        this.mProgressCallbackDelay = progressCallbackDelay;
    }
}
