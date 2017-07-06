package com.broadcast.chatdemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.broadcast.entities.ChatDo;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2/15/2017.
 */
public class MediaManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static MediaManager sInstance;

    private Context mContext = null;
    private ChatDo mChatDo = null;
    private View mConvertView = null;

    private ImageView playRecording = null;
    private SeekBar mseekBar = null;
    private TextView mtotalRecordingTime = null;
    private TextView mStartRecordingTime = null;

    private MediaPlayer mediaPlayer;
    private int resumePosition;


    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;

    private MediaManager() {
    }

    public static MediaManager getInstance() {
        if (sInstance == null) {
            sInstance = new MediaManager();
        }
        return sInstance;
    }


    public void prepareMediaPlayer(Context context, ChatDo chatDo, View convertView) {
        if (mChatDo != null) {
            pauseMedia();
            resetAudioTimeMembers();
        }
        mContext = context;
        mChatDo = chatDo;
        mConvertView = convertView;

        initViews();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setDataSource(chatDo.getmVoiceMessage());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepare();
            updateSongProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetAudioTimeMembers() {
        startTime = 0;
        finalTime = 0;
        oneTimeOnly = 0;
    }

    private void updateSongProgress() {
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            mseekBar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        mtotalRecordingTime.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        mStartRecordingTime.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );

        mseekBar.setProgress((int) startTime);
    }


    Runnable updateTimeProgressRunnable = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            mStartRecordingTime.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime))));
            mseekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    private void initViews() {
        playRecording = (ImageView) mConvertView.findViewById(R.id.iv_play_audio);
        mseekBar = (SeekBar) mConvertView.findViewById(R.id.seekBar);
        mtotalRecordingTime = (TextView) mConvertView.findViewById(R.id.tv_message_total_time);
        mStartRecordingTime = (TextView) mConvertView.findViewById(R.id.tv_message_playing_time);
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            playRecording.setImageResource(R.drawable.pause);
            mtotalRecordingTime.setVisibility(View.VISIBLE);
            mStartRecordingTime.setVisibility(View.VISIBLE);
            mediaPlayer.start();
            myHandler.postDelayed(updateTimeProgressRunnable, 100);
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            playRecording.setImageResource(R.drawable.play_button);
            myHandler.removeCallbacks(updateTimeProgressRunnable);
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            playRecording.setImageResource(R.drawable.play_button);
            myHandler.removeCallbacks(updateTimeProgressRunnable);
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }


    public ChatDo getmChatDo() {
        return mChatDo;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playRecording.setImageResource(R.drawable.play_button);
        mtotalRecordingTime.setVisibility(View.GONE);
        mStartRecordingTime.setVisibility(View.GONE);
        mseekBar.setProgress((0));
        myHandler.removeCallbacks(updateTimeProgressRunnable);
    }
}
