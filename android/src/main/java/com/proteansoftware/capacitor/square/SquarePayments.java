package com.proteansoftware.capacitor.square;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;
import com.squareup.sdk.pos.PosClient;
import com.squareup.sdk.pos.PosSdk;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "SquarePayments")
public class SquarePayments extends Plugin {
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

        JSArray defaultPaymentMethods = new JSArray();
        defaultPaymentMethods.put(-1);

        ArrayList<ChargeRequest.TenderType> restrictPaymentMethods = new ArrayList<>();
        try {
            List allowedPaymentMethods = call.getArray("allowedPaymentMethods", defaultPaymentMethods).toList();
            if(!allowedPaymentMethods.contains("ALL")) {
                for (Object i: allowedPaymentMethods) {
                    try {
                        ChargeRequest.TenderType tenderType = ChargeRequest.TenderType.valueOf(i.toString());
                        restrictPaymentMethods.add(tenderType);
                    } catch (IllegalArgumentException ie) {
                        call.reject("paymentMethod type '" + i.toString() + "' is invalid");
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            restrictPaymentMethods = new ArrayList<>();
        }

        saveCall(call);
        try {
            ChargeRequest.Builder request = new ChargeRequest.Builder(totalAmount, currencyCode);

            if(!restrictPaymentMethods.isEmpty()) {
                request.restrictTendersTo(restrictPaymentMethods);
            }

            Intent intent = posClient.createChargeIntent(request.build());
            startActivityForResult(call, intent, "chargeRequest");
        } catch (ActivityNotFoundException e) {
            // TODO: Square not installed
            if(posClient != null) {
                posClient.openPointOfSalePlayStoreListing();
            }
            freeSavedCall();
            call.reject(e.getMessage());
        } catch (Exception e) {
            freeSavedCall();
            call.reject(e.getMessage());
        }
    }

    @ActivityCallback
    protected void chargeRequest(int resultCode, Intent data) {
        // Get the previously saved call
        PluginCall savedCall = getSavedCall();
        JSObject errorObject = new JSObject();
        try {
            if (savedCall == null) {
                errorObject.put("error", "could not retrieve saved call");
                notifyListeners("transactionFailed", errorObject);
                savedCall.success();
                return;
            }

            // Handle expected results
            if (resultCode == Activity.RESULT_OK) {
                // Handle success
                ChargeRequest.Success success = posClient.parseChargeSuccess(data);
                JSObject resultData = new JSObject();
                resultData.put("message", "Success");
                resultData.put("clientTransactionId", success.clientTransactionId);
                notifyListeners("transactionComplete", resultData);
                savedCall.success();
            } else {
                // Handle expected errors
                ChargeRequest.Error error = posClient.parseChargeError(data);
                String errorMessage = "Error" + error.code + "\nclientTransactionId" + error.debugDescription;
                errorObject.put("error", errorMessage);
                notifyListeners("transactionFailed", errorObject);
                savedCall.success();
            }
        } catch (Exception e) {
            errorObject.put("error", e.getMessage());
            notifyListeners("transactionFailed", errorObject);
            savedCall.success();
        }
    }
}
