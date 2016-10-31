package it.alfonsovinti.cordova.plugins.bixolonprint.features;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import it.alfonsovinti.cordova.plugins.bixolonprint.BixolonPrint;

/**
 * Created by xyzxyz442 on 10/19/2016.
 */

public class ConnectionListener {

    private static final String TAG = "BixolonConnListener";

    public static final String ACTION_START_CONNECTION_LISTENER = "startConnectionListener";
    public static final String ACTION_RECONNECT = "reconnect";
    public static final String ACTION_DISCONNECT = "disconnect";
    public static final String ACTION_STOP_CONNECTION_LISTENER = "stopConnectionListener";

    private CallbackContext callbackContext;

    private BixolonPrint plugin;

    private boolean isListenerStarted = false;

    public ConnectionListener(BixolonPrint plugin) {
        this.plugin = plugin;
    }

    public boolean startConnectionListener() {
        if(this.callbackContext != null) {
            this.plugin.getCallbackContext().error(createConnectionData("Connection listener already started."));
            return false;
        }
        this.callbackContext = this.plugin.getCallbackContext();

        this.isListenerStarted = true;

        sendConnectionData("Connection listener started.");

        return true;
    }

    private JSONObject createConnectionData(String message) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("isConnected", this.plugin.mIsConnected);
            obj.put("message", message);
        } catch(JSONException e) {
            Log.e(TAG, "ConnectionListener.createConnectionData: " + e.getMessage(), e);
        }

        return obj;
    }

    public void sendConnectionData(String message) {
        String msg = "";

        if(message != null)
            msg = message;

        if(this.callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, createConnectionData(msg));
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }

    public void stopConnectionListener() {
        this.isListenerStarted = false;
        this.sendConnectionData("Connection listener stopped.");
        this.callbackContext = null;
    }

    public boolean isListenerStarted() {
        return this.isListenerStarted;
    }
}
