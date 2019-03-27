package com.truex.sheppard.player;

public interface PlaybackStateListener {
    void onPlayerDidStart();
    void onPlayerDidResume();
    void onPlayerDidPause();
    void onPlayerDidComplete();
}
