package com.tominc.mirror.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerService;
import com.example.jean.jcplayer.JcPlayerView;
import com.tominc.mirror.Config;
import com.tominc.mirror.R;
import com.tominc.mirror.Utility;

import java.io.File;
import java.util.ArrayList;

import de.mateware.snacky.Snacky;

/**
 * Created by I334104 on 7/17/2017.
 */

public class MusicFragment extends Fragment implements JcPlayerService.JcPlayerServiceListener {
    Utility utility;
    JcPlayerView player;

    public MusicFragment(){
        utility = Utility.getInstance(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.music_layout, container, false);

        player = (JcPlayerView) root.findViewById(R.id.jcplayer);

        File music_folder = getMusicFolder();

        ArrayList<JcAudio> jcAudios = new ArrayList<>();

        if(music_folder.isDirectory()) {
            File[] files = music_folder.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.getAbsolutePath().endsWith(".mp3")) {
                        jcAudios.add(JcAudio.createFromFilePath(file.getAbsolutePath()));
                    }
                }
            } else {
                Snacky.builder()
                        .setActivty(getActivity())
                        .setText("No Music Found in SmartMirror/music Folder")
                        .warning().show();
            }
        }

        player.initPlaylist(jcAudios);

        player.createNotification();

        player.registerServiceListener(this);


        return root;
    }

    private File getMusicFolder(){
        File temp = new File(Config.MUSIC_FOLDER);
        if(!temp.exists()) temp.mkdirs();

        return temp;

    }

    @Override
    public void onPreparedAudio(String audioName, int duration) {

    }

    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onContinueAudio() {

    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onTimeChanged(long currentTime) {

    }

    @Override
    public void updateTitle(String title) {

    }
}
