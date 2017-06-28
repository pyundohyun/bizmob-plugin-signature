package com.mcnc.bizmoblite.plugin.signature;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Signature extends CordovaPlugin {

    private static final int OPEN_SIGNATURE_REQUEST_CODE = 0x1001;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x1002;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("openSignature")) {
            this.callbackContext = callbackContext;

            if (cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                openSignatureActivity();
            } else {
                cordova.requestPermission(this, WRITE_EXTERNAL_STORAGE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSignatureActivity();
                return;
            }
        }
        callbackContext.error("You can't make a signature unless you grant us the permission.");
    }

    private void openSignatureActivity() {
        Intent intent = new Intent(cordova.getActivity(), SignatureActivity.class);
        intent.putExtra("orientation", "land");
        cordova.startActivityForResult(this, intent, OPEN_SIGNATURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == OPEN_SIGNATURE_REQUEST_CODE) {
                if (intent.hasExtra("message")) {
                    JSONObject message = new JSONObject(intent.getStringExtra("message"));

                    boolean result = message.has("result") && message.getBoolean("result");
                    if (result) {
                        callbackContext.success(message.has("sign_data") ? message.getString("sign_data") : "");
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.error("User has been canceled the signature.");
    }
}
