package com.etna.gunzbu_a.freshdj;

import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;

/**
 * Created by Kevin_Tan on 06/07/2016.
 */
public class BasicCallback extends Callback {

    public BasicCallback() {
    }

    public void successCallback(String channel, Object response) {
        Log.d("PUBNUB", "Success: " + response.toString());
    }

    public void connectCallback(String channel, Object message) {
        Log.d("PUBNUB", "Connect: " + message.toString());
    }

    public void errorCallback(String channel, PubnubError error) {
        Log.d("PUBNUB", "Error: " + error.toString());

    }
}
