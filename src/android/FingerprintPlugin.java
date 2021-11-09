package com.unikrew.faceoff;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import com.unikrew.faceoff.fingerprint.FingerprintConfig;
import com.unikrew.faceoff.fingerprint.FingerprintScannerActivity;
import com.unikrew.faceoff.fingerprint.LivenessNotSupportedException;
import com.unikrew.faceoff.fingerprint.NadraConfig;


import android.content.Context;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

import com.unikrew.faceoff.fingerprint.FingerprintResponse;
import com.unikrew.faceoff.fingerprint.ResultIPC;
import com.unikrew.faceoff.fingerprint.models.IdentificationResponse;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
/////////////////////////


import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * This class echoes a string called from JavaScript.
 */
public class FingerprintPlugin extends CordovaPlugin {

    private static final String TAG = "HomeActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final int LIVENESS_CHECK_REQUEST = 2;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("exportMethod")) {
            String message = args.getString(0);
            this.requestExternalStoragePermission();
            this.exportMethod(callbackContext);
            return true;
        }
        
        return false;
    }

    private void exportMethod(CallbackContext callbackContext) {
        Context context = cordova.getActivity().getApplicationContext();
        this.launchScanning(null, null, null, null, null, FingerprintConfig.Mode.EXPORT_WSQ);
        callbackContext.success("launched Scanning");
        
    }

    private void launchScanning(String name, String username, String password,
    String cnicNumber, NadraConfig nadraConfig, FingerprintConfig.Mode mode) {
        try {

            Context context = cordova.getActivity().getApplicationContext();

            // Build FingerprintConfig, required by Fingerprint SDK
            // Fingerprint Config is used to customize the UI and fingerprint scanning options
            // See its usage in 'SettingsActivity' for details
            FingerprintConfig.Builder builder = new FingerprintConfig.Builder()
                .setFingers(FingerprintConfig.Fingers.EIGHT_FINGERS)
                .setMode(mode)
                .setPackPng(true);

            if (nadraConfig != null) {
                builder.setNadraConfig(nadraConfig);
            }

            FingerprintConfig fingerprintConfig = builder.build(R.drawable.placeholder_logo, R.color.colorPrimary);

            // Setting intent data and launching scanner activity
            Intent intent = new Intent(context, FingerprintScannerActivity.class);
            if (mode == FingerprintConfig.Mode.ENROLL) {
                intent.putExtra(FingerprintScannerActivity.NAME_FOR_FINGERPRINT, name);
                intent.putExtra(FingerprintScannerActivity.CNIC_FOR_FINGERPRINT, cnicNumber);
            }
            if (mode == FingerprintConfig.Mode.NADRA) {
                intent.putExtra(FingerprintScannerActivity.USERNAME, username);
                intent.putExtra(FingerprintScannerActivity.PASSWORD, password);
                intent.putExtra(FingerprintScannerActivity.CNIC_FOR_FINGERPRINT, cnicNumber);
            }
            intent.putExtra(FingerprintScannerActivity.FACEOFF_FINGERPRINT_CONFIG, fingerprintConfig);
            cordova.setActivityResultCallback (this);
            this.cordova.getActivity().startActivityForResult(intent, 22);

        } catch (LivenessNotSupportedException e) {
            System.out.println( e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final int responseCode = data.getIntExtra("fingerprint_response_code", -1);
        if (responseCode > 0) {
            FingerprintResponse fingerprintResponse = ResultIPC.getInstance().getFingerprintResponse(responseCode);
            if (fingerprintResponse != null && fingerprintResponse.getIdentificationResponse() != null) {
                // Initialize views and show person's data

                System.out.println(fingerprintResponse.getIdentificationResponse());
            } else {
                // If not empty, show results
                //fingerprintResponse.getPngList();
                fingerprintResponse.getWsqList();
                System.out.println("Response : " + fingerprintResponse.getWsqList());
            }
        }
    }

    private void requestExternalStoragePermission() {
        Context context = cordova.getActivity().getApplicationContext();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.cordova.getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        11);
            }
        }
    }




}