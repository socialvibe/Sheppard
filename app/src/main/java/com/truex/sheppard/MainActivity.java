package com.truex.sheppard;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.truex.sheppard.ads.TruexAdManager;
import com.truex.sheppard.player.PlaybackHandler;
import com.truex.sheppard.player.PlaybackStateListener;
import com.truex.sheppard.player.PlayerEventListener;

public class MainActivity extends AppCompatActivity implements PlaybackStateListener, PlaybackHandler {

    private static final String CLASSTAG = "MainActivity";
    private static final String APP_NAME = "Sheppard";
    private static final String FAKE_STREAM_URL = "http://media.truex.com/video_assets/2018-03-16/cce7a081-f9eb-4c14-aef2-1f773b4005d0_large.mp4";
    private ViewGroup mViewGroup;
    private SimpleExoPlayerView mPlayerView;
    private TruexAdManager mTruexAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setupExoPlayer();
        setupFakeStream();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTruexAdManager != null) {
            mTruexAdManager.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTruexAdManager != null) {
            mTruexAdManager.onPause();
        }
    }

    private void setupExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        mPlayerView = (SimpleExoPlayerView) this.findViewById(R.id.player_view);
        mPlayerView.setPlayer(player);

        player.addListener(new PlayerEventListener(this, this));
    }

    private void setupFakeStream() {
        String userAgent = Util.getUserAgent(this, APP_NAME);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent, null);

        Uri uri = Uri.parse(FAKE_STREAM_URL);
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mPlayerView.getPlayer().prepare(source);
        mPlayerView.getPlayer().setPlayWhenReady(true);
    }

    public void onPlayerDidStart() {
        Log.d(CLASSTAG, "onPlayerDidStart");
        if (mPlayerView.getPlayer() == null) {
            return;
        }

        pauseStream();

        mViewGroup = (ViewGroup) this.findViewById(R.id.activity_main);
        mTruexAdManager = new TruexAdManager(this, this);
        mTruexAdManager.startAd(mViewGroup);
    }

    public void onPlayerDidResume() {
        Log.d(CLASSTAG, "onPlayerDidResume");
    }

    public void onPlayerDidPause() {
        Log.d(CLASSTAG, "onPlayerDidPause");
    }

    public void resumeStream() {
        Log.d(CLASSTAG, "resumeStream");
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.getPlayer().setPlayWhenReady(true);
    }

    public void pauseStream() {
        Log.d(CLASSTAG, "pauseStream");
        mPlayerView.getPlayer().setPlayWhenReady(false);
        mPlayerView.setVisibility(View.GONE);
    }

    public void cancelStream() {
        Log.d(CLASSTAG, "cancelStream");
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        SimpleExoPlayer player = mPlayerView.getPlayer();
        mPlayerView.setPlayer(null);
        player.release();
    }
}