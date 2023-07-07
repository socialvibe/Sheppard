package com.truex.sheppard.player;

import android.util.Log;

import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;

/**
 * This class simply listens for playback events and informs the listeners when any playback events
 * occur. Additionally, this class cancels the video stream when and if any playback errors occur.
 */
public class PlayerEventListener implements Player.Listener {
    private static final String CLASSTAG = PlayerEventListener.class.getSimpleName();

    private PlaybackHandler playbackHandler;
    private PlaybackStateListener listener;
    private boolean playbackDidStart;
    private boolean playWhenReady;

    public PlayerEventListener(PlaybackHandler playbackHandler, PlaybackStateListener listener) {
        this.playbackHandler = playbackHandler;
        this.listener = listener;
        playbackDidStart = false;
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        // Just report errors, don't cancel the stream, as there can be spurious MediaCodecVideoRenderer exceptions
        // for simple .mp4 streams, even from videos playing in a web view, not technically even related to
        // the current player instance.
        Log.e(CLASSTAG, "onPlayerError: " + error);
        //playbackHandler.cancelStream();
    }

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
        this.playWhenReady = playWhenReady;
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_ENDED) {
            listener.onPlayerDidComplete();
        } else if (playWhenReady && playbackState == Player.STATE_READY) {
            if (!playbackDidStart) {
                playbackDidStart = true;
                listener.onPlayerDidStart();
            } else {
                listener.onPlayerDidResume();
            }
        } else if (playbackDidStart && playbackState == Player.STATE_IDLE) {
            listener.onPlayerDidPause();
        }
    }
}
