package zakariahossain.com.musicplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private SeekBar seekBar;
    private TextView tvLeftTime, tvRightTime;
    private Button btnPrevious, btnPlayPause, btnNext;

    private MediaPlayer mediaPlayer;
    private Thread musicThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();
        setUpSeekBar();
    }

    private void setUpUI() {
        seekBar = findViewById(R.id.seekBar);
        tvLeftTime = findViewById(R.id.tvLeftTime);
        tvRightTime = findViewById(R.id.tvRightTime);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);

        btnPrevious.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlayPause.setBackgroundResource(R.drawable.ic_play);
            }
        });
    }

    private void setUpSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());

                tvLeftTime.setText(dateFormat.format(new Date(mediaPlayer.getCurrentPosition())));
                tvRightTime.setText(dateFormat.format(new Date(mediaPlayer.getDuration())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrevious:
                previousButtonMethod();
                break;

            case R.id.btnPlayPause:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
                break;

            case R.id.btnNext:
                nextButtonMethod();
                break;
        }
    }

    private void previousButtonMethod() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
    }

    private void nextButtonMethod() {
        if (mediaPlayer.isPlaying()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());

            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
            seekBar.setProgress(mediaPlayer.getDuration());

            tvLeftTime.setText(dateFormat.format(new Date(mediaPlayer.getDuration())));
        }
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            updateMusicThread();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            btnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void updateMusicThread() {
        musicThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer.isPlaying() && mediaPlayer != null) {
                        Thread.sleep(100);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                tvLeftTime.setText(dateFormat.format(new Date(mediaPlayer.getCurrentPosition())));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        musicThread.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        musicThread.interrupt();
        musicThread = null;

        super.onDestroy();
    }
}
