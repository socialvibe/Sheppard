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

    private static final String CLASSTAG = MainActivity.class.getSimpleName();
    private static final String FAKE_STREAM_URL = "http://media.truex.com/video_assets/2018-03-16/cce7a081-f9eb-4c14-aef2-1f773b4005d0_large.mp4";

    // This player view is used to display a fake stream that mimics actual video content
    private SimpleExoPlayerView mPlayerView;

    // We need to hold onto the ad manager so that the ad manager can listen for lifecycle events
    private TruexAdManager mTruexAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Set-up the video content player
        setupExoPlayer();

        // Set-up and start a fake video stream
        setupFakeStream();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // We need to inform the true[X] ad manager that the application has resumed
        if (mTruexAdManager != null) {
            mTruexAdManager.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // We need to inform the true[X] ad manager that the application has paused
        if (mTruexAdManager != null) {
            mTruexAdManager.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need to inform the true[X] ad manager that the application has stopped
        if (mTruexAdManager != null) {
            mTruexAdManager.onStop();
        }
    }

    private void setupExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        mPlayerView = (SimpleExoPlayerView) this.findViewById(R.id.player_view);
        mPlayerView.setPlayer(player);

        // Listen for player events so that we can load the true[X] ad manager when the video stream starts
        player.addListener(new PlayerEventListener(this, this));
    }

    private void setupFakeStream() {
        // Set-up the video content data source
        String applicationName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        String userAgent = Util.getUserAgent(this, applicationName);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent, null);

        // Load and start the fake stream
        Uri uri = Uri.parse(FAKE_STREAM_URL);
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mPlayerView.getPlayer().prepare(source);
        mPlayerView.getPlayer().setPlayWhenReady(true);
    }

    /**
     * Called when the player starts displaying the fake content stream
     * Display the true[X] engagement
     */
    public void onPlayerDidStart() {
        Log.d(CLASSTAG, "onPlayerDidStart");
        if (mPlayerView.getPlayer() == null) {
            return;
        }

        // Pause the stream and display a true[X] engagement
        pauseStream();

        // Start the true[X] engagement
        ViewGroup viewGroup = (ViewGroup) this.findViewById(R.id.activity_main);
        mTruexAdManager = new TruexAdManager(this, this);
        mTruexAdManager.startAd(viewGroup);
    }

    /**
     * Called when the fake content stream is resumed
     */
    public void onPlayerDidResume() {
        Log.d(CLASSTAG, "onPlayerDidResume");
    }

    /**
     * Called when the fake content stream is paused
     */
    public void onPlayerDidPause() {
        Log.d(CLASSTAG, "onPlayerDidPause");
    }

    /**
     * This method resumes and displays the fake content stream
     * Note: We call this method whenever a true[X] engagement is completed
     */
    public void resumeStream() {
        Log.d(CLASSTAG, "resumeStream");
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.getPlayer().setPlayWhenReady(true);
    }

    /**
     * This method pauses and hides the fake content stream
     * Note: We call this method whenever a true[X] engagement is completed
     */
    public void pauseStream() {
        Log.d(CLASSTAG, "pauseStream");
        if (mPlayerView.getPlayer() == null) {
            return;
        }
        mPlayerView.getPlayer().setPlayWhenReady(false);
        mPlayerView.setVisibility(View.GONE);
    }

    /**
     * This method cancels the fake content stream and releases the video content player
     * Note: We call this method whenever the user cancels an engagement without receiving credit
     */
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