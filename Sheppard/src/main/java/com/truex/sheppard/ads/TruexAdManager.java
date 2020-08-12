package com.truex.sheppard.ads;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

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

    private PlaybackHandler playbackHandler;
    private boolean didReceiveCredit = false;
    private TruexAdRenderer truexAdRenderer;

    public TruexAdManager(Context context, PlaybackHandler playbackHandler) {
        this.playbackHandler = playbackHandler;

        // Set-up the true[X] ad renderer
        truexAdRenderer = new TruexAdRenderer(context);
        truexAdRenderer.addEventListener("*", this::onTruexAdRendererEvent);
    }

    public void onTruexAdRendererEvent(@NonNull final String eventName, Map<String, ?> data) {
        Log.i(CLASSTAG, "onTruexAdRendererEvent(eventName=" + eventName + ")");
        switch (eventName) {
            case TruexAdRendererConstants.AD_STARTED:
            case TruexAdRendererConstants.OPT_IN:
            case TruexAdRendererConstants.OPT_OUT:
            case TruexAdRendererConstants.USER_CANCEL:
            case TruexAdRendererConstants.SKIP_CARD_SHOWN:
            case TruexAdRendererConstants.AD_FETCH_COMPLETED:
                break;
            case TruexAdRendererConstants.AD_FREE_POD:
                didReceiveCredit = true;
                break;
            case TruexAdRendererConstants.AD_COMPLETED:
            case TruexAdRendererConstants.AD_ERROR:
            case TruexAdRendererConstants.NO_ADS_AVAILABLE:
                onCompletion();
                break;
            case TruexAdRendererConstants.POPUP_WEBSITE:
                String url = (String) data.get("url");
                Log.d(CLASSTAG, "popup(url=" + url + ")");
                break;
            default:
                Log.w(CLASSTAG,"received unrecognized event, ignoring...");
                break;
        }
    }

    /**
     * Start displaying the true[X] engagement
     * @param viewGroup - the view group in which you would like to display the true[X] engagement
     */
    public void startAd(ViewGroup viewGroup) {
        try {
            String json = String.format("{\"user_id\":\"3e47e82244f7aa7ac3fa60364a7ede8453f3f9fe\",\"placement_hash\":\"%s\",\"vast_config_url\":\"%s\"}\n", "81551ffa2b851abc5372ab9ed9f1f58adabe5203", "https://qa-get.truex.com/81551ffa2b851abc5372ab9ed9f1f58adabe5203/vast/config?asnw=&flag=%2Bamcb%2Bemcr%2Bslcb%2Bvicb%2Baeti-exvt&fw_key_values=&metr=0&prof=g_as3_truex&ptgt=a&pvrn=&resp=vmap1&slid=fw_truex&ssnw=&vdur=&vprn=");
            JSONObject adParams = new JSONObject(json);

            truexAdRenderer.init(adParams, TruexAdRendererConstants.PREROLL);
            truexAdRenderer.start(viewGroup);
        } catch (JSONException e) {
            Log.e(CLASSTAG, "JSON ERROR");
        }
    }

    /**
     * Inform the true[X] ad renderer that the application has resumed
     */
    public void onResume() {
        truexAdRenderer.resume();
    }

    /**
     * Inform the true[X] ad renderer that the application has paused
     */
    public void onPause() {
        truexAdRenderer.pause();
    }

    /**
     * Inform that the true[X] ad renderer that the application has stopped
     */
    public void onStop() {
        truexAdRenderer.stop();
    }

    /**
     * This method should be called once the true[X] ad manager is done
     */
    private void onCompletion() {
        if (didReceiveCredit) {
            // The user received true[ATTENTION] credit
            // Resume the content stream (and skip any linear ads)
            playbackHandler.resumeStream();
        } else {
            // The user did not receive credit
            // Continue the content stream and display linear ads
            playbackHandler.displayLinearAds();
        }
    }
}
