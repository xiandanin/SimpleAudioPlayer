package com.dyhdyh.audioplayer.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dyhdyh.audioplayer.AudioPlayerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author dengyuhan
 *         created 2018/9/6 15:27
 */
public class AudioListActivity extends AppCompatActivity {
    RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        rv = findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AudioAdapter(testData()));
    }

    private List<String> testData() {
        final File longFile = new File(getExternalCacheDir(), "long_test.mp3");
        final File shortFile = new File(getExternalCacheDir(), "short_test.mp3");
        List<String> data = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            data.add(random.nextBoolean() ? shortFile.getAbsolutePath() : longFile.getAbsolutePath());
        }
        return data;
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
}
