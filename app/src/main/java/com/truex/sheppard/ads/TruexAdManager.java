package com.truex.sheppard.ads;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.truex.adrenderer.IEventHandler;
import com.truex.adrenderer.TruexAdRenderer;
import com.truex.adrenderer.TruexAdRendererConstants;
import com.truex.sheppard.player.PlaybackHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * This class holds a reference to the true[X] ad renderer and handles all of the event handling
 * for the example integration application. This class interacts with the video player by resuming
 * the content when the engagement is complete.
 */
public class TruexAdManager {
    private static final String CLASSTAG = TruexAdManager.class.getSimpleName();
    private boolean mDidReceiveCredit;
    private PlaybackHandler mPlaybackHandler;
    private TruexAdRenderer mTruexAdRenderer;

    public TruexAdManager(Context context, PlaybackHandler playbackHandler) {
        mDidReceiveCredit = false;
        mPlaybackHandler = playbackHandler;

        // Set-up the true[X] ad renderer
        mTruexAdRenderer = new TruexAdRenderer(context);

        // Set-up the event listeners
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.AD_STARTED, this.adStarted);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.AD_COMPLETED, this.adCompleted);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.AD_ERROR, this.adError);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.NO_ADS_AVAILABLE, this.noAds);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.AD_FREE_POD, this.adFree);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.POPUP_WEBSITE, this.popup);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.USER_CANCEL, this.userCancel);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.OPT_IN, this.optIn);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.OPT_OUT, this.optOut);
        mTruexAdRenderer.addEventListener(TruexAdRendererConstants.SKIP_CARD_SHOWN, this.skipCardShown);
    }

    /**
     * Start displaying the true[X] engagement
     * @param viewGroup - the view group in which you would like to display the true[X] engagement
     */
    public void startAd(ViewGroup viewGroup) {
        try {
            // NOTE: The creativeURL, adParameters, and slotType should come from the SSAI provider
            // This is hard coded as an example only
            String creativeURL = "https://media.truex.com/container/2.0/fw_renderers/choicecard.js";
            JSONObject adParams = new JSONObject("{\"user_id\":\"3e47e82244f7aa7ac3fa60364a7ede8453f3f9fe\",\"placement_hash\":\"81551ffa2b851abc5372ab9ed9f1f58adabe5203\",\"vast_config_url\":\"http://qa-get.truex.com/81551ffa2b851abc5372ab9ed9f1f58adabe5203/vast/config?asnw=&flag=%2Bamcb%2Bemcr%2Bslcb%2Bvicb%2Baeti-exvt&fw_key_values=&metr=0&prof=g_as3_truex&ptgt=a&pvrn=&resp=vmap1&slid=fw_truex&ssnw=&vdur=&vprn=\"}\n");

            mTruexAdRenderer.init(creativeURL, adParams, TruexAdRendererConstants.PREROLL);
            mTruexAdRenderer.start(viewGroup);
        } catch (JSONException e) {
            Log.e(CLASSTAG, "JSON ERROR");
        }
    }

    /**
     * Inform the true[X] ad renderer that the application has resumed
     */
    public void onResume() {
        mTruexAdRenderer.resume();
    }


    /**
     * Inform the true[X] ad renderer that the application has paused
     */
    public void onPause() {
        mTruexAdRenderer.pause();
    }

    /**
     * Inform that the true[X] ad renderer that the application has stopped
     */
    public void onStop() {
        mTruexAdRenderer.stop();
    }

    /**
     * This method should be called once the true[X] ad manager is done
     */
    private void onCompletion() {
        if (mDidReceiveCredit) {
            // The user received true[ATTENTION] credit
            // Resume the content stream (and skip any linear ads)
            mPlaybackHandler.resumeStream();
        } else {
            // The user did not receive credit
            // In an actual integration, we would typically display linear ads
            // For this example integration, however, we will instead cancel the stream
            mPlaybackHandler.cancelStream();
        }
    }

    /*
       Note: This event is triggered when the ad starts
     */
    private IEventHandler adStarted = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adStarted");
        }
    };

    /*
       Note: This event is triggered when the engagement is completed,
       either by the completion of the engagement or the user exiting the engagement
     */
    private IEventHandler adCompleted = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adCompleted");

            // We are now done with the engagement
            onCompletion();
        }
    };

    /*
       Note: This event is triggered when an error is encountered by the true[X] ad renderer
     */
    private IEventHandler adError = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adError");

            // There was an error trying to load the enagement
            onCompletion();
        }
    };

    /*
       Note: This event is triggered if the engagement fails to load,
       as a result of there being no engagements available
     */
    private IEventHandler noAds = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "noAds");

            // There are no engagements available
            onCompletion();
        }
    };

    /*
       Note: This event is not currently being used
     */
    private IEventHandler popup = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            String url = (String) data.get("url");
            Log.d(CLASSTAG, "popup");
            Log.d(CLASSTAG, "url: " + url);
        }
    };

    /*
       Note: This event is triggered when the viewer has earned their true[ATTENTION] credit. We
       could skip over the linear ads here, so that when the ad is complete, all we would need
       to do is resume the stream.
     */
    private IEventHandler adFree = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adFree");
            mDidReceiveCredit = true;
        }
    };

    /*
       Note: This event is triggered when a user cancels an interactive engagement
     */
    private IEventHandler userCancel = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "userCancel");
        }
    };

    /*
       Note: This event is triggered when a user opts-in to an interactive engagement
     */
    private IEventHandler optIn = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "optIn");
        }
    };

    /*
       Note: This event is triggered when a user opts-out of an interactive engagement,
       either by time-out, or by choice
     */
    private IEventHandler optOut = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "optOut");
        }
    };

    /*
       Note: This event is triggered when a skip card is being displayed to the user
       This occurs when a user is able to skip ads
     */
    private IEventHandler skipCardShown = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "skipCardShown");
        }
    };
}
