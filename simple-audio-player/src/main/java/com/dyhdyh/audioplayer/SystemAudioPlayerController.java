package com.dyhdyh.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
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
    }

    private void reset() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
        } else {
            mediaPlayer.reset();
        }
    }

    @Override
    public void start() {
        try {
            if (mPrepareCompleted) {
                startPlayerAndTimer();
            } else {
                mediaPlayer.prepareAsync();
            }
        } catch (final Exception e) {
            e.printStackTrace();

            callError(AbstractAudioPlayer.WHAT_START, AbstractAudioPlayer.EXTRA_EMPTY, e);

        }
    }

    @Override
    public void setDataSource(String path) {
        mPrepareCompleted = false;
        try {
            reset();
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();

            callError(AbstractAudioPlayer.WHAT_SET_DATA, AbstractAudioPlayer.EXTRA_EMPTY, e);
        }

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
        } catch (final IllegalStateException e) {
            e.printStackTrace();

            callError(AbstractAudioPlayer.WHAT_SEEK, AbstractAudioPlayer.EXTRA_EMPTY, e);
        }
    }

    @Override
    public void release() {
        //回收也要停止计时器 防止未stop就回收的情况
        stopTimer();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
        AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onAutoCompletion();
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                float percentFloat = (float) percent / 100;
                player.onBufferingUpdate(percentFloat);
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onSeekComplete();
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        Log.d("--------->", "onError--->" + what + "-->" + extra);
        callError(what, extra, new Exception(String.format("MediaPlayer error what=%d, extra=%d", what, extra)));
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        //Log.d("--------->", "onInfo--->" + what + "-->" + extra);
        AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onInfo(what, extra);
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
                AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
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

    /**
     * 回调错误
     *
     * @param what
     * @param extra
     * @param e
     */
    public void callError(final int what, final int extra, final Throwable e) {
        AudioPlayerManager.runMainThreadCurrentPlayer(new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                player.onError(what, extra, e);
            }
        });
    }

    public void setProgressCallbackDelay(long progressCallbackDelay) {
        this.mProgressCallbackDelay = progressCallbackDelay;
    }
}
