package com.dyhdyh.audioplayer;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

/**
 * @author dengyuhan
 *         created 2018/9/5 15:20
 */
public class AudioPlayerManager {
    public static final int PLAYER_TYPE_SYSTEM = 0;

    private Handler mMainHandler;

    private int mPlayerType = -1;
    private AudioPlayerController mPlayerController;
    //最后一个播放的播放器
    private AbstractAudioPlayer mCurrentPlayer;

    //需要维护生命周期的播放器
    private SparseArray<AbstractAudioPlayer> mLifecyclePlayer;

    private static AudioPlayerManager mInstance;

    private AudioPlayerManager() {
        mLifecyclePlayer = new SparseArray<>();
        mMainHandler = new Handler();
        setPlayerEngine(PLAYER_TYPE_SYSTEM);
    }

    public static AudioPlayerManager getInstance() {
        synchronized (AudioPlayerManager.class) {
            if (mInstance == null) {
                mInstance = new AudioPlayerManager();
            }
        }
        return mInstance;
    }

    public void setCurrentPlayer(AbstractAudioPlayer currentPlayer) {
        this.mCurrentPlayer = currentPlayer;
        //如果设置最后播放的播放器 就添加到池里以维护它的生命周期
        if (mCurrentPlayer != null) {
            mLifecyclePlayer.put(mCurrentPlayer.getContext().hashCode(), mCurrentPlayer);
        }
    }

    public AbstractAudioPlayer getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public AudioPlayerController getPlayerEngine() {
        return mPlayerController;
    }

    /**
     * 设置播放器引擎
     *
     * @param type
     */
    public void setPlayerEngine(int type) {
        if (mPlayerType != type) {
            if (PLAYER_TYPE_SYSTEM == type) {
                mPlayerController = new SystemAudioPlayerController();
                mPlayerType = type;
            }
        }
    }


    /**
     * 在主线程操作当前播放器
     *
     * @param runnable
     */
    public static void runMainThreadCurrentPlayer(final CurrentPlayerRunnable runnable) {
        getInstance().mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                runCurrentPlayer(runnable);
            }
        });
    }

    /**
     * 在当前线程操作当前播放器
     *
     * @param runnable
     */
    public static void runCurrentPlayer(final CurrentPlayerRunnable runnable) {
        final AbstractAudioPlayer player = getInstance().getCurrentPlayer();
        if (player != null) {
            runnable.run(player);
        }
    }

    /**
     * 操作key对应的播放器
     *
     * @param key
     * @param runnable
     */
    public static void runLifecyclePlayer(Object key, final CurrentPlayerRunnable runnable) {
        final AbstractAudioPlayer player = getInstance().mLifecyclePlayer.get(key.hashCode());
        if (player != null) {
            runnable.run(player);
        }
    }


    public static void start(Object key) {
        runLifecyclePlayer(key, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                //Log.d("resume-------->", player.getStateString() + "-->" + player.getContext());
                player.startPlay();
            }
        });
    }

    public static void stop(Object key) {
        runLifecyclePlayer(key, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                Log.d("pause-------->", player.getStateString() + "-->" + player.getContext());
                player.stopPlay();
            }
        });
    }

    public static void release(final Object key) {
        runLifecyclePlayer(key, new CurrentPlayerRunnable() {
            @Override
            public void run(AbstractAudioPlayer player) {
                //移除需要回收的播放器
                //Log.d("release-------->", player.getStateString() + "-->" + player.getContext());
                getInstance().mLifecyclePlayer.remove(key.hashCode());
            }
        });
    }

    public static void releaseAll() {
        getInstance().getPlayerEngine().release();
        getInstance().setCurrentPlayer(null);
        getInstance().mLifecyclePlayer.clear();
    }

}
