package com.proteansoftware.capacitor.square;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;
import com.squareup.sdk.pos.PosClient;
import com.squareup.sdk.pos.PosSdk;

@NativePlugin()
public class SquarePayments extends Plugin {

    private static String APPLICATION_ID = null;
    private static final int CHARGE_REQUEST_CODE = 1;

    @PluginMethod()
    public void initApp(PluginCall call) {
      APPLICATION_ID = call.getString("ApplicationId");
    }

    @PluginMethod()
    public void startTransaction(PluginCall call) {
        Context context = getContext();
        PosClient posClient = PosSdk.createClient(context, APPLICATION_ID);
        
        ChargeRequest request = new ChargeRequest.Builder(100, CurrencyCode.GBP).build();
        try {
            Intent intent = posClient.createChargeIntent(request);
            saveCall(call);
            startActivityForResult(call, intent, CHARGE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // TODO: Square not installed
            posClient.openPointOfSalePlayStoreListing();
        }
    }

    // in order to handle the intents result, you have to @Override handleOnActivityResult
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        // Get the previously saved call
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }

        if (requestCode == CHARGE_REQUEST_CODE) {
            // Do something with the data
            savedCall.success();
        } else {
            savedCall.reject();
        }
    }
}
