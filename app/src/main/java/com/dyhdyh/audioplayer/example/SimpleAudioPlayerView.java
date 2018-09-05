package com.dyhdyh.audioplayer.example;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dyhdyh.audioplayer.AbstractAudioPlayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author dengyuhan
 *         created 2018/9/5 15:51
 */
public class SimpleAudioPlayerView extends AbstractAudioPlayer {
    private TextView tvProgress;
    private ProgressBar progressBar;

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public SimpleAudioPlayerView(Context context) {
        this(context, null);
    }

    public SimpleAudioPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleAudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        View.inflate(context, R.layout.view_audio, this);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.pb_progress);

    }

    public void setup(String path) {
        try {
            super.setup(path, false);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPlayerProgress(long progressTime, float percent) {
        Log.d("------->", "当前正在播放-->" + progressTime + ", " + percent + ", " + getDuration());
        tvProgress.setText(format.format(progressTime) + " : " + format.format(getDuration()));
        progressBar.setProgress((int) (percent * 100));
    }

}
