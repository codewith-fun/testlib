package investwell.common.calculator.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.iw.acceleratordemo.R;


public class AudioPlayer {

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, R.raw.click_sound2);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }

}