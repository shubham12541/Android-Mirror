package com.tominc.mirror.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private static final String TAG = "Music Fragment";
    TextView song_name, song_duration;
    ArrayList<JcAudio> jcAudios;


    public static MusicFragment newInstance() {

        Bundle args = new Bundle();

        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        utility = Utility.getInstance(getActivity().getApplicationContext());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.music_layout, container, false);

        player = (JcPlayerView) root.findViewById(R.id.jcplayer);

        song_name = (TextView) root.findViewById(R.id.song_name);
        song_duration = (TextView) root.findViewById(R.id.song_duration);

        File music_folder = getMusicFolder();

        jcAudios = new ArrayList<>();

        if(music_folder.isDirectory()) {
            File[] files = music_folder.listFiles();

            Log.d(TAG, "onCreateView: " + files.length);

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


        player.registerServiceListener(this);


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        player.initPlaylist(jcAudios);

        player.createNotification();

//        if(jcAudios.size() > 0){
//            for(JcAudio audio: jcAudios){
//                player.playAudio(audio);
//            }
//        } else{
//            Snacky.builder()
//                    .setActivty(getActivity())
//                    .setText("No Music File found in SmartMirror/music Folder")
//                    .error().show();
//        }


    }

    private File getMusicFolder(){
        Log.d(TAG, "getMusicFolder: " + Config.MUSIC_FOLDER);
        Log.d(TAG, "getMusicFolder: " + Environment.getExternalStorageDirectory().getPath());
        File temp = new File(Config.MUSIC_FOLDER);
        if(!temp.exists()) temp.mkdirs();

        return temp;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.kill();
    }

    @Override
    public void onPreparedAudio(String audioName, int duration) {
        song_duration.setText(String.valueOf(duration));
        song_name.setText(audioName);

        Snacky.builder()
                .setActivty(getActivity())
                .setText("Audio Prepared: " + audioName)
                .success().show();
    }

    @Override
    public void onCompletedAudio() {
        player.next();
    }

    @Override
    public void onPaused() {
        player.pause();
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
