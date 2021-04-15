package com.example.codecompanion.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Functionality for the QRCode scanner
 */
public class QrScanner {

    public void start(Activity current){
        IntentIntegrator integrator = new IntentIntegrator(current);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }
}
