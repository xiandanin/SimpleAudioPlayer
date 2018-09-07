package com.dyhdyh.audioplayer.example;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button btn_play;
    private Button btn_pause;
    private TextView tv_state;

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
        tv_state = findViewById(R.id.tv_state);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.pb_progress);
        btn_play=findViewById(R.id.btn_play);
        btn_pause=findViewById(R.id.btn_pause);
        btn_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAutoPlay();
            }
        });
        btn_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPause();
            }
        });

    }

    public void setup(String path) {
        try {
            super.setup(path, true);
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

    @Override
    protected void onStateChanged(int oldState, int state) {
        super.onStateChanged(oldState, state);
        tv_state.setText(hashCode()+"---"+getStateString());
        if (state == AbstractAudioPlayer.STATE_PLAYING) {
            btn_play.setText("停止");
        } else if (state == AbstractAudioPlayer.STATE_PAUSE) {
            btn_play.setText("恢复");
        } else {
            btn_play.setText("播放");
        }
    }


    public void clickAutoPlay() {
        if (getCurrentState() == AbstractAudioPlayer.STATE_PAUSE) {
            //如果是暂停的
            resumePlay();
        } else if (getCurrentState() == AbstractAudioPlayer.STATE_NORMAL||getCurrentState() == AbstractAudioPlayer.STATE_ERROR) {
            //如果是停止的
            startPlay();
        } else if (getCurrentState() == AbstractAudioPlayer.STATE_PLAYING) {
            //如果是在播放的
            stopPlay();
        }
    }

    public void clickPause() {
        if (getCurrentState() == AbstractAudioPlayer.STATE_PLAYING) {
            //如果是在播放的
            pausePlay();
        }
    }
}
