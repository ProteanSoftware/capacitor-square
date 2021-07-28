package com.proteansoftware.capacitor.square;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "CapacitorSquare")
public class CapacitorSquarePlugin extends Plugin {

    private CapacitorSquare implementation = new CapacitorSquare();
    private String savedCallId = null;

    @PluginMethod
    public void initApp(PluginCall call) {
        String applicationId = call.getString("applicationId");
        if(applicationId == null || applicationId.length() == 0) {
            call.reject("applicationId null");
            return;
        }

        Context context = getContext();
        implementation.initApp(applicationId, context);

        JSObject data = new JSObject();
        data.put("message", "set applicationId");
        call.success(data);
    }

    @PluginMethod()
    public void startTransaction(PluginCall call) {
        if (!implementation.isInitalised()) {
            call.reject("client not setup, call initApp first");
            return;
        }

        Integer totalAmount = call.getInt("totalAmount");
        if (totalAmount == null) {
            call.reject("totalAmount is null");
            return;
        }

        if (totalAmount <= 0) {
            call.reject("totalAmount must be greater than 0");
            return;
        }

        String currencyCodeString = call.getString("currencyCode");
        if (currencyCodeString == null || currencyCodeString.length() == 0) {
            call.reject("currencyCode is null");
            return;
        }

        CurrencyCode currencyCode = implementation.parseCurrencyCode(currencyCodeString);
        if (currencyCode == null) {
            call.reject("currencyCode '" + currencyCodeString + "' is invalid");
        }

        JSArray defaultPaymentMethods = new JSArray();
        defaultPaymentMethods.put(-1);

        List allowedPaymentMethods = null;
        try {
            allowedPaymentMethods = call.getArray("allowedPaymentMethods", defaultPaymentMethods).toList();
        } catch (JSONException e) {
            allowedPaymentMethods = new ArrayList<>();
        }

        ArrayList<ChargeRequest.TenderType> restrictPaymentMethods = null;
        try {
            restrictPaymentMethods = implementation.parsePaymentMethods(allowedPaymentMethods);
        } catch (Exception e) {
            call.reject(e.getMessage());
            return;
        }

        savedCallId = call.getCallbackId();
        bridge.saveCall(call);

        try {
            Intent intent = implementation.createChargeIntent(totalAmount, currencyCode, restrictPaymentMethods);
            startActivityForResult(call, intent, "chargeRequest");
        } catch (ActivityNotFoundException e) {
            implementation.openPointOfSalePlayStoreListing();
            call.release(bridge);
            call.reject(e.getMessage());
        } catch (Exception e) {
            call.release(bridge);
            call.reject(e.getMessage());
        }
    }

    @ActivityCallback
    protected void chargeRequest(int resultCode, Intent data) {
        // Get the previously saved call
        PluginCall savedCall = bridge.getSavedCall(savedCallId);
        JSObject errorObject = new JSObject();
        try {
            if (savedCall == null) {
                errorObject.put("error", "could not retrieve saved call");
                notifyListeners("transactionFailed", errorObject);
                savedCall.resolve();
                return;
            }

            // Handle expected results
            if (resultCode == Activity.RESULT_OK) {
                // Handle success
                ChargeRequest.Success success = implementation.parseChargeSuccess(data);
                JSObject resultData = new JSObject();
                resultData.put("message", "Success");
                resultData.put("clientTransactionId", success.clientTransactionId);
                notifyListeners("transactionComplete", resultData);
                savedCall.success();
            } else {
                // Handle expected errors
                ChargeRequest.Error error = implementation.parseChargeError(data);
                String errorMessage = "Error" + error.code + "\nclientTransactionId" + error.debugDescription;
                errorObject.put("error", errorMessage);
                notifyListeners("transactionFailed", errorObject);
                savedCall.resolve();
            }
        } catch (Exception e) {
            errorObject.put("error", e.getMessage());
            notifyListeners("transactionFailed", errorObject);
            savedCall.resolve();
        }
    }
}
