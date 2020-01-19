package com.proteansoftware.capacitor.square;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.getcapacitor.JSObject;
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

    private static final int CHARGE_REQUEST_CODE = 1;
    private static PosClient posClient = null;

    @PluginMethod()
    public void initApp(PluginCall call) {
        String applicationId = call.getString("applicationId");
        if(applicationId == null || applicationId.length() == 0) {
            call.reject("applicationId null");
            return;
        }

        Context context = getContext();
        posClient = PosSdk.createClient(context, applicationId);

        JSObject data = new JSObject();
        data.put("message", "set applicationId");
        call.success(data);
    }

    @PluginMethod()
    public void startTransaction(PluginCall call) {
        if(posClient == null) {
            call.reject("client not setup, call initApp first");
            return;
        }

        Integer totalAmount = call.getInt("totalAmount");
        if(totalAmount == null) {
            call.reject("totalAmount is null");
            return;
        }

        if(totalAmount <= 0) {
            call.reject("totalAmount must be greater than 0");
            return;
        }

        String currencyCodeString = call.getString("currencyCode");
        if(currencyCodeString == null || currencyCodeString.length() == 0) {
            call.reject("currencyCode is null");
            return;
        }

        CurrencyCode currencyCode;
        try {
            currencyCode = CurrencyCode.valueOf(currencyCodeString);
        } catch (IllegalArgumentException e) {
            call.reject("currencyCode '" + currencyCodeString + "' is invalid");
            return;
        }

        saveCall(call);
        PosClient posClient = null;
        try {
            ChargeRequest request = new ChargeRequest.Builder(totalAmount, currencyCode).build();
            Intent intent = posClient.createChargeIntent(request);
            startActivityForResult(call, intent, CHARGE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // TODO: Square not installed
            if(posClient != null) {
                posClient.openPointOfSalePlayStoreListing();
            }
            call.reject(e.getMessage());
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    // in order to handle the intents result, you have to @Override handleOnActivityResult
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        // Get the previously saved call
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            savedCall.reject("could not retrive saved call");
            return;
        }

        if (requestCode != CHARGE_REQUEST_CODE) {
            // Do something with the data
            savedCall.reject("response code did not match");
            return;
        }
       
        // Handle expected results
        if (resultCode == Activity.RESULT_OK) {
            // Handle success
            ChargeRequest.Success success = posClient.parseChargeSuccess(data);
            JSObject resultData = new JSObject();
            resultData.put("message", "Success");
            resultData.put("clientTransactionId", success.clientTransactionId);
            savedCall.success(resultData);
        } else {
            // Handle expected errors
            ChargeRequest.Error error = posClient.parseChargeError(data);
            String errorMessage = "Error" + error.code + "\nclientTransactionId" + error.debugDescription;
            savedCall.reject(errorMessage);
        }
    }
}
