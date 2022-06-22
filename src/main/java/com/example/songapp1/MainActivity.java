package com.example.songapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SeekBar volumeSeekBar = null;
    private Button playButton,stopButton;
    private TextView currentSongName;
    private AudioManager audioManager;
    private String currentUrlFromStream;
    private MediaPlayer mediaPlayer;
    private int pauseAtLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentSongName = findViewById(R.id.songName);
        playButton = findViewById(R.id.play);
        stopButton = findViewById(R.id.stop);
        volumeSeekBar = findViewById(R.id.seekBar);

        initAudioManager();

        //method to click URL to play music
        initListView();
        setUpPlayButton();
        setUpStopButton();

    }

    private void setUpStopButton() {

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                playButton.setText("PLAY");
            }
        });
    }

    private void setUpPlayButton() {

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying() && playButton.getText().equals("PAUSE")){
                    mediaPlayer.pause();
                    playButton.setText("Play");
                    pauseAtLength = mediaPlayer.getCurrentPosition();


                } else if(playButton.getText().equals("PLAY")){
                   playButton.setText("PAUSE");
                   try {
                       if(pauseAtLength==0){
                           mediaPlayer.prepareAsync();
                           mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                               @Override
                               public void onPrepared(MediaPlayer mediaPlayer) {
                                   mediaPlayer.start();

                               }
                           });

                       }else {
                           mediaPlayer.seekTo(pauseAtLength);
                           mediaPlayer.start();
                       }
                   }catch (IllegalStateException e){
                       e.printStackTrace();
                   }
                }

            }
        });
    }

    private void initListView() {
        final List<String> songList= new ArrayList<>();

        songList.add("https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.mp3");//add more

        ArrayAdapter arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                songList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView songTV=(TextView) super.getView(position, convertView, parent);
                songTV.setTag(songList.get(position));
                songTV.setTextColor(Color.parseColor("#c094d4"));
                songTV.setTypeface(songTV.getTypeface(), Typeface.BOLD);
                songTV.setText(songList.get(position).substring(songList.get(position).lastIndexOf("/")+1));
                songTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                return songTV;
            }
        };

        ListView listView = (ListView) findViewById(R.id.songListView);
        //click on items to play music
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                currentUrlFromStream = view.getTag().toString();
                currentSongName.setText(currentUrlFromStream.substring(currentUrlFromStream
                        .lastIndexOf("/")+1));
                try{
                     if(mediaPlayer!=null){
                         mediaPlayer.stop();

                     }
                     pauseAtLength = 0;
                     mediaPlayer = new MediaPlayer();
                     mediaPlayer.setDataSource(currentUrlFromStream);
                     mediaPlayer.prepareAsync();
                     mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//to specify audioStram

                    //specify path f audio file to play
                     mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                         @Override
                         public void onPrepared(MediaPlayer mediaPlayer) {
                             mediaPlayer.start();
                             playButton.setText("PAUSE");

                         }
                     });


                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        listView.setAdapter(arrayAdapter);

    }

    private void initAudioManager() {

        try{
        setVolumeControlStream(AudioManager.STREAM_MUSIC);//method volume of audio stream

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }



    }


}
