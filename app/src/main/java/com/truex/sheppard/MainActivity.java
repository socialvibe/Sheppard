package com.truex.sheppard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.truex.adrenderer.TruexAdRenderer;
import com.truex.adrenderer.TruexAdRendererConstants;
import com.truex.adrenderer.IEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String CLASSTAG = "Sheppard";
    private TruexAdRenderer mAdRenderer;
    ViewGroup mViewGroup;

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
        if (mAdRenderer != null) {
            return;
        }
        Log.d(CLASSTAG, "onStart");
        mViewGroup = (ViewGroup) this.findViewById(R.id.activity_main);
        try {

            // NOTE: This creativeURL, adParameters and slotType should come from Uplynk.
            // This is hard coded as an example only.
            String creativeURL = "https://media.truex.com/container/2.0/fw_renderers/choicecard-foxnow.js";
            JSONObject adParams = new JSONObject("{\"user_id\":\"3e47e82244f7aa7ac3fa60364a7ede8453f3f9fe\",\"placement_hash\":\"40b200758ad4c17150face37a16baf1b153f69af\",\"vast_config_url\":\"http://qa-get.truex.com/40b200758ad4c17150face37a16baf1b153f69af/vast/config?asnw=&flag=%2Bamcb%2Bemcr%2Bslcb%2Bvicb%2Baeti-exvt&fw_key_values=&metr=0&prof=g_as3_truex&ptgt=a&pvrn=&resp=vmap1&slid=fw_truex&ssnw=&vdur=&vprn=\"}\n");
            mAdRenderer = new TruexAdRenderer(this);
            mAdRenderer.addEventListener(TruexAdRendererConstants.AD_STARTED, this.adStarted);
            mAdRenderer.addEventListener(TruexAdRendererConstants.AD_COMPLETED, this.adCompleted);
            mAdRenderer.addEventListener(TruexAdRendererConstants.AD_ERROR, this.adError);
            mAdRenderer.addEventListener(TruexAdRendererConstants.NO_ADS_AVAILABLE, this.noAds);
            mAdRenderer.addEventListener(TruexAdRendererConstants.AD_FREE_POD, this.adFree);
            mAdRenderer.addEventListener(TruexAdRendererConstants.POPUP_WEBSITE, this.popup);
            mAdRenderer.init(creativeURL, adParams, TruexAdRendererConstants.MIDROLL);
            mAdRenderer.start(mViewGroup);
        } catch (JSONException e) {
            Log.e(CLASSTAG, "JSON ERROR");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdRenderer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdRenderer.resume();
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
        }
    };
}