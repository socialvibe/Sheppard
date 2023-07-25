package com.truex.sheppard.player;

public interface PlaybackHandler {
    void resumeStream();
    void closeStream();
    void cancelStream();
    void displayLinearAds();
}
