package vn.nhan.phiendich;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import vn.nhan.phiendich.model.Detail;
import vn.nhan.phiendich.utils.Utils;

public class DetailActivity extends BaseActive {

    private LinearLayout audioPanel;
    private WebView content;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler handler = new Handler();
    private ImageButton barPlay, scroll, play, replay, stop;
    private TextView playText, timer;
    private SeekBar seekBar;

    private boolean playing, scrolling = true;

    private static Detail model;

    public static void setModel(Detail model) {
        DetailActivity.model = model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        audioPanel = (LinearLayout) findViewById(R.id.audio_panel);
        content = (WebView) findViewById(R.id.content);
        content.setBackgroundColor(Color.TRANSPARENT);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        barPlay = (ImageButton) findViewById(R.id.bar_play);
        scroll = (ImageButton) findViewById(R.id.scroll);
        play = (ImageButton) findViewById(R.id.play);
        replay = (ImageButton) findViewById(R.id.replay);
        stop = (ImageButton) findViewById(R.id.stop);
        playText = (TextView) findViewById(R.id.play_txt);
        timer = (TextView) findViewById(R.id.timer);
        stop.setEnabled(false);
        replay.setEnabled(false);

        if (model != null) {
            // set title
            setTitle(model.typename + " - " + Utils.formatTitleDate(AppManager.selectedDate));

            // set audio bar
            if (model.audioFile != null) {
                audioPanel.setVisibility(View.VISIBLE);
                initPlayMedia();
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            startTime = progress;
                            seekBar.setProgress(progress);
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            } else {
                audioPanel.setVisibility(View.INVISIBLE);
                audioPanel.setVisibility(View.GONE);
            }

            // set content
            String c = String.format("<div align=center>%s</div>", model.content);
            content.loadData(c, "text/html; charset=utf-8", "UTF-8");
        }
    }

    private void initPlayMedia() {
        try {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(model.audioFile));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Utils.makeText(e.getMessage());
            Log.d(getClass().getName(), "initPlayMedia: " + e.getMessage(), e);
        }
    }

    public void scroll(View v) {
        scrolling = !scrolling;
        if (scrolling) {
            scroll.setBackgroundResource(R.drawable.audio_scroll);

        } else {
            scroll.setBackgroundResource(R.drawable.audio_unscroll);

        }
    }

    public void play(View v) {
        playing = !playing;
        if (playing) {
            barPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
            play.setBackgroundResource(R.drawable.audio_pause);
            playText.setText("Tạm dừng");

            try {
                mediaPlayer.start();
                if (finalTime == 0) {
                    finalTime = mediaPlayer.getDuration();
                    if (finalTime == -1) {
                        finalTime = Integer.parseInt(model.audioLength) * 1000;
                    }
                    seekBar.setMax((int) finalTime);
                    handler.postDelayed(updateTime, 100);
                }
            } catch (Exception e) {
                Utils.makeText(e.getMessage());
                Log.d(getClass().getName(), "play: " + e.getMessage(), e);
            }
        } else {
            barPlay.setBackgroundResource(android.R.drawable.ic_media_play);
            play.setBackgroundResource(R.drawable.audio_play);
            playText.setText("Đọc kinh");

            try {
                mediaPlayer.pause();
            } catch (Exception e) {
                Utils.makeText(e.getMessage());
                Log.d(getClass().getName(), "play pause: " + e.getMessage(), e);
            }
        }
        stop.setEnabled(playing);
        replay.setEnabled(playing);
    }

    private Runnable updateTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            timer.setText(String.format("%s:%s/%s:%s",
                    Utils.twoDigit(TimeUnit.MILLISECONDS.toMinutes((long) startTime)),
                    Utils.twoDigit(TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))),
                    Utils.twoDigit(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)),
                    Utils.twoDigit(TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            ));
            seekBar.setProgress((int)startTime);
            if (startTime < finalTime) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private Runnable webScroll = new Runnable() {
        public void run() {
            content.scrollBy(0, 20);
            handler.postDelayed(this, 500);
        }
    };

    public void replay(View v) {
        stop(v);
        play(v);
    }

    public void stop(View v) {
        barPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        play.setBackgroundResource(R.drawable.audio_play);
        playText.setText("Đọc kinh");
        playing = false;
        stop.setEnabled(playing);
        replay.setEnabled(playing);

        try {
            mediaPlayer.stop();
            handler.removeCallbacks(updateTime);
            startTime = finalTime = 0;
            seekBar.setProgress((int)startTime);
            initPlayMedia();
        } catch (Exception e) {
            Utils.makeText(e.getMessage());
            Log.d(getClass().getName(), "stop: " + e.getMessage(), e);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
