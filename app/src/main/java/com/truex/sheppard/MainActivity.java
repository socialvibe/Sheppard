package com.truex.sheppard;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private static final String CLASSTAG = "Sheppard";
    private SimpleExoPlayerView mPlayerView;
    private TruexAdManager mTruexAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayerView == null) {
            setupExoPlayer();
            setupFakeStream();
        }

//        if (mTruexAdManager != null) {
//            return;
//        }
//        ViewGroup viewGroup = (ViewGroup) this.findViewById(R.id.activity_main);
//        mTruexAdManager = new TruexAdManager(this, viewGroup);
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

        player.addListener(new PlayerEventListener(mPlayerView, this, this));
    }

    private void setupFakeStream() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Sheppard"), null);

        Uri uri = Uri.parse("https://media.truex.com/video_assets/2018-03-16/cce7a081-f9eb-4c14-aef2-1f773b4005d0_large.mp4");
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mPlayerView.getPlayer().prepare(source);
        mPlayerView.getPlayer().setPlayWhenReady(true);
    }

    public void onPlayerDidStart() {
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        // Pause the video
        mPlayerView.getPlayer().setPlayWhenReady(false);
        mPlayerView.setVisibility(View.GONE);

        // Start the ad
        ViewGroup viewGroup = (ViewGroup) this.findViewById(R.id.activity_main);
        mTruexAdManager = new TruexAdManager(this, viewGroup, this);
    }

    public void onPlayerDidResume() {
        // Do nothing
    }

    public void onPlayerDidPause() {
        // Do nothing
    }

    public void resumeStream() {
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.getPlayer().setPlayWhenReady(true);

        onPlayerDidResume();
    }

    public void cancelStream() {
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        SimpleExoPlayer player = mPlayerView.getPlayer();
        mPlayerView.setPlayer(null);
        player.release();
    }
}