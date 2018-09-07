package com.dyhdyh.audioplayer.example;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * @author dengyuhan
 *         created 2018/9/6 15:29
 */
public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.Holder> {
    private List<String> data;

    public AudioAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audio, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final String path = data.get(i);
        holder.tv_name.setText(new File(path).getName());
        holder.audio_item.setup(path);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Holder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.audio_item.stopPlay();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        SimpleAudioPlayerView audio_item;
        TextView tv_name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            audio_item = itemView.findViewById(R.id.audio_item);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
