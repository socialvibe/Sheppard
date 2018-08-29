package com.truex.sheppard.player;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class PlayerEventListener extends Player.DefaultEventListener {
    private SimpleExoPlayerView mPlayerView;
    private PlaybackHandler mPlaybackHandler;
    private boolean mPlaybackDidStart;
    private PlaybackStateListener mListener;

    public PlayerEventListener(SimpleExoPlayerView playerView, PlaybackHandler playbackHandler, PlaybackStateListener listener) {
       mPlayerView = playerView;
       mPlaybackHandler = playbackHandler;
       mPlaybackDidStart = false;
       mListener = listener;
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
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
