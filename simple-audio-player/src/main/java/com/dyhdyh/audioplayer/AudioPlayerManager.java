package com.dyhdyh.audioplayer;

import android.os.Handler;

/**
 * @author dengyuhan
 *         created 2018/9/5 15:20
 */
public class AudioPlayerManager {
    public static final int PLAYER_TYPE_SYSTEM = 0;

    private int mPlayerType = -1;
    private AudioPlayerController mPlayerController;
    private AbstractAudioPlayer mCurrentPlayer;

    private static AudioPlayerManager mInstance;

    private AudioPlayerManager() {
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

    public static void runCurrentPlayer(Handler handler, final CurrentPlayerRunnable runnable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final AbstractAudioPlayer player = getInstance().getCurrentPlayer();
                if (player != null) {
                    runnable.run(player);
                }
            }
        });
    }


}
