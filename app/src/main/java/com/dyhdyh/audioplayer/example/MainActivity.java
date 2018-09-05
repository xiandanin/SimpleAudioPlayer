package com.dyhdyh.audioplayer.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dyhdyh.audioplayer.AbstractAudioPlayer;
import com.dyhdyh.audioplayer.OnAudioPlayerStateChangeListener;
import com.dyhdyh.manager.assets.AssetsManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    SimpleAudioPlayerView audio_view;

    TextView tv_state;
    Button btn_play;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audio_view = findViewById(R.id.audio_view);
        tv_state = findViewById(R.id.tv_state);
        btn_play = findViewById(R.id.btn_play);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            final File file = new File(getExternalCacheDir(), "short_test.mp3");
            AssetsManager.copyAssetFile(this, file.getName(), file);
            audio_view.setup(file.getAbsolutePath());
        }

        audio_view.setOnStateChangeListener(new OnAudioPlayerStateChangeListener() {
            @Override
            public void onStateChanged(int oldState, int state) {
                tv_state.setText(audio_view.getStateString());
                if (state == AbstractAudioPlayer.STATE_PLAYING) {
                    btn_play.setText("停止");
                } else if (state == AbstractAudioPlayer.STATE_PAUSE) {
                    btn_play.setText("恢复");
                } else {
                    btn_play.setText("播放");
                }
            }
        });
    }

    public void clickAutoPlay(View view) {
        if (audio_view.getCurrentState() == AbstractAudioPlayer.STATE_PAUSE) {
            //如果是暂停的
            audio_view.resumePlay();
        } else if (audio_view.getCurrentState() == AbstractAudioPlayer.STATE_NORMAL) {
            //如果是停止的
            audio_view.startPlay();
        } else if (audio_view.getCurrentState() == AbstractAudioPlayer.STATE_PLAYING) {
            //如果是在播放的
            audio_view.stopPlay();
        }
    }

    public void clickPause(View view) {
        if (audio_view.getCurrentState() == AbstractAudioPlayer.STATE_PLAYING) {
            //如果是在播放的
            audio_view.pausePlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        audio_view.resumePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        audio_view.pausePlay();
    }

}
