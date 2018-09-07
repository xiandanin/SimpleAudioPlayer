package com.dyhdyh.audioplayer.example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dyhdyh.audioplayer.AudioPlayerManager;
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
        }

        clickShortFile(null);
    }


    public void clickLongFile(View view) {
        final File longFile = new File(getExternalCacheDir(), "long_test.mp3");
        AssetsManager.copyAssetFile(this, longFile.getName(), longFile);
        audio_view.setup(longFile.getAbsolutePath());
    }

    public void clickShortFile(View view) {
        final File shortFile = new File(getExternalCacheDir(), "short_test.mp3");
        AssetsManager.copyAssetFile(this, shortFile.getName(), shortFile);
        audio_view.setup(shortFile.getAbsolutePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioPlayerManager.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioPlayerManager.stop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayerManager.releaseAll();
    }

    public void clickAudioList(View view) {
        startActivity(new Intent(this, AudioListActivity.class));
    }
}
