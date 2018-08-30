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

public class TruexAdManager {
    private static final String CLASSTAG = "TruexAdManager";
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

    public void startAd(ViewGroup viewGroup) {
        try {
            // NOTE: The creativeURL, adParameters, and slotType should come from Uplynk.
            // This is hard coded as an example only.
            String creativeURL = "https://media.truex.com/container/2.0/fw_renderers/choicecard.js";
            JSONObject adParams = new JSONObject("{\"user_id\":\"3e47e82244f7aa7ac3fa60364a7ede8453f3f9fe\",\"placement_hash\":\"40b200758ad4c17150face37a16baf1b153f69af\",\"vast_config_url\":\"http://qa-get.truex.com/40b200758ad4c17150face37a16baf1b153f69af/vast/config?asnw=&flag=%2Bamcb%2Bemcr%2Bslcb%2Bvicb%2Baeti-exvt&fw_key_values=&metr=0&prof=g_as3_truex&ptgt=a&pvrn=&resp=vmap1&slid=fw_truex&ssnw=&vdur=&vprn=\"}\n");

            mTruexAdRenderer.init(creativeURL, adParams, TruexAdRendererConstants.PREROLL);
            mTruexAdRenderer.start(viewGroup);
        } catch (JSONException e) {
            Log.e(CLASSTAG, "JSON ERROR");
        }
    }

    public void onResume() {
        mTruexAdRenderer.resume();
    }

    public void onPause() {
        mTruexAdRenderer.pause();
    }

    private IEventHandler adStarted = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adStarted");
        }
    };

    private IEventHandler adCompleted = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adCompleted");

            if (mDidReceiveCredit) {
                mPlaybackHandler.resumeStream();
            } else {
                mPlaybackHandler.cancelStream();
            }
        }
    };

    private IEventHandler adError = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adError");
        }
    };

    private IEventHandler noAds = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "noAds");
        }
    };

    private IEventHandler popup = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            String url = (String) data.get("url");
            Log.d(CLASSTAG, "popup");
            Log.d(CLASSTAG, "url: " + url);
        }
    };

    private IEventHandler adFree = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "adFree");
            mDidReceiveCredit = true;
        }
    };

    private IEventHandler userCancel = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "userCancel");
        }
    };

    private IEventHandler optIn = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "optIn");
        }
    };

    private IEventHandler optOut = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "optOut");
        }
    };

    private IEventHandler skipCardShown = new IEventHandler() {
        @Override
        public void handleEvent(Map<String, ?> data) {
            Log.d(CLASSTAG, "skipCardShown");
        }
    };
}
