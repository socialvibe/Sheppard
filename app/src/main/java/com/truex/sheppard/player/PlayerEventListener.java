package com.truex.sheppard.player;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;

public class PlayerEventListener extends Player.DefaultEventListener {
    private static final String CLASSTAG = "PlayerEventListener";

    private PlaybackHandler mPlaybackHandler;
    private boolean mPlaybackDidStart;
    private PlaybackStateListener mListener;

    public PlayerEventListener(PlaybackHandler playbackHandler, PlaybackStateListener listener) {
       mPlaybackHandler = playbackHandler;
       mPlaybackDidStart = false;
       mListener = listener;
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(CLASSTAG, "onPlayerError");
        mPlaybackHandler.cancelStream();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY) {
            if (!mPlaybackDidStart) {
                mListener.onPlayerDidStart();
                mPlaybackDidStart = true;
            } else {
                mListener.onPlayerDidResume();
            }
        } else if (!playWhenReady) {
            mListener.onPlayerDidPause();
        }
    }
}
