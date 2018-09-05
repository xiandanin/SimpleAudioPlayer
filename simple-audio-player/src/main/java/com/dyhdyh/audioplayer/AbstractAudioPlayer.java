package com.dyhdyh.audioplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author dengyuhan
 *         created 2018/9/5 15:26
 */
public abstract class AbstractAudioPlayer extends RelativeLayout {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARING_CHANGING_URL = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSE = 5;
    public static final int STATE_AUTO_COMPLETE = 6;
    public static final int STATE_ERROR = 7;

    private int mState;
    private boolean mLooping;
    private long mDuration;

    private OnAudioPlayerStateChangeListener mStateChangeListener;

    public AbstractAudioPlayer(Context context) {
        super(context);
    }

    public AbstractAudioPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractAudioPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(String path, boolean looping) throws IOException {
        this.mLooping = looping;
        final AudioPlayerController engine = AudioPlayerManager.getInstance().getPlayerEngine();
        engine.setDataSource(path);
    }

    /**
     * 开始
     */
    public void startPlay() {
        final AudioPlayerController engine = AudioPlayerManager.getInstance().getPlayerEngine();
        if (engine.isPlaying()) {
            //如果有播放的 先停掉
            stopPlay();
        }
        AudioPlayerManager.getInstance().setCurrentPlayer(this);
        engine.start();
        setState(STATE_PLAYING);
    }

    /**
     * 暂停
     */
    public void pausePlay() {
        final AudioPlayerController engine = AudioPlayerManager.getInstance().getPlayerEngine();
        if (engine.isPlaying()) {
            engine.pause();
            setState(STATE_PAUSE);
        }
    }

    /**
     * 恢复
     */
    public void resumePlay() {
        if (STATE_PAUSE == mState) {
            startPlay();
        }
    }

    /**
     * 停止
     */
    public void stopPlay() {
        final AudioPlayerController engine = AudioPlayerManager.getInstance().getPlayerEngine();
        if (engine.isPlaying()) {
            AudioPlayerManager.getInstance().getPlayerEngine().stop();
            setState(STATE_NORMAL);
        }
    }

    public boolean isPlaying() {
        return AudioPlayerManager.getInstance().getPlayerEngine().isPlaying();
    }

    public void seekTo(long time) {
        AudioPlayerManager.getInstance().getPlayerEngine().seekTo(time);
    }

    public void release() {
        AudioPlayerManager.getInstance().getPlayerEngine().release();
        AudioPlayerManager.getInstance().setCurrentPlayer(null);
    }

    public long getCurrentPosition() {
        return AudioPlayerManager.getInstance().getPlayerEngine().getCurrentPosition();
    }

    public long getDuration() {
        if (mDuration > 0) {
            return mDuration;
        }
        mDuration = AudioPlayerManager.getInstance().getPlayerEngine().getDuration();
        return mDuration;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        AudioPlayerManager.getInstance().getPlayerEngine().setVolume(leftVolume, rightVolume);
    }

    public void onPrepared() {

    }

    /**
     * 自己播放完成了
     */
    public void onAutoCompletion() {
        setState(STATE_AUTO_COMPLETE);
        setState(STATE_NORMAL);
        if (mLooping) {
            startPlay();
        }
    }

    /**
     * 缓冲进度
     *
     * @param percent
     */
    public void onBufferingUpdate(float percent) {

    }

    /**
     * 播放进度
     */
    public void onPlayerProgress(long progressTime) {
        final long duration = getDuration();
        long newProgressTime = Math.min(progressTime, duration);
        this.onPlayerProgress(newProgressTime, (float) newProgressTime / duration);
    }

    public void onPlayerProgress(long progressTime, float percent) {

    }

    /**
     * Seek完成
     */
    public void onSeekComplete() {

    }

    /**
     * 异常回调
     *
     * @param what
     * @param extra
     * @return
     */
    public void onError(final int what, final int extra) {
        mState = STATE_ERROR;
    }

    public void onInfo(final int what, final int extra) {
    }

    protected void setState(int state) {
        int oldState = mState;
        this.mState = state;
        onStateChanged(oldState, mState);
    }

    public int getCurrentState() {
        return mState;
    }

    public String getStateString() {
        final Field[] fields = AbstractAudioPlayer.class.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType() == int.class && fields[i].getName().startsWith("STATE_")) {
                    int state = fields[i].getInt(this);
                    if (mState == state) {
                        return fields[i].getName();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void onStateChanged(int oldState, int state) {
        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChanged(oldState, state);
        }
    }

    public void setOnStateChangeListener(OnAudioPlayerStateChangeListener listener) {
        this.mStateChangeListener = listener;
    }
}
