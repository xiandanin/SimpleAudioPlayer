package com.dyhdyh.audioplayer;

import java.io.IOException;

/**
 * 音频播放器控制器接口
 *
 * @author dengyuhan
 *         created 2018/9/5 15:17
 */
public interface AudioPlayerController {

    void start();

    void setDataSource(String path) throws IOException;

    void pause();

    void stop();

    boolean isPlaying();

    void seekTo(long time);

    void release();

    long getCurrentPosition();

    long getDuration();

    void setVolume(float leftVolume, float rightVolume);
}
