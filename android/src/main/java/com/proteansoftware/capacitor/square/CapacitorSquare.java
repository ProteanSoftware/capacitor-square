package com.proteansoftware.capacitor.square;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;
import com.squareup.sdk.pos.PosApi;
import com.squareup.sdk.pos.PosClient;
import com.squareup.sdk.pos.PosSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CapacitorSquare {
    private static PosClient posClient = null;

    public void initApp(String applicationId, Context context) {
        posClient = PosSdk.createClient(context, applicationId);
    }

    public boolean isInitalised() {
        return posClient != null;
    }

    public CurrencyCode parseCurrencyCode(String currencyCodeString) {
        CurrencyCode currencyCode;
        try {
            currencyCode = CurrencyCode.valueOf(currencyCodeString);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return currencyCode;
    }

    public ArrayList<ChargeRequest.TenderType> parsePaymentMethods(List allowedPaymentMethods) throws Exception {
        ArrayList<ChargeRequest.TenderType> restrictPaymentMethods = new ArrayList<>();
        if (!allowedPaymentMethods.contains("ALL")) {
            for (Object i: allowedPaymentMethods) {
                try {
                    ChargeRequest.TenderType tenderType = ChargeRequest.TenderType.valueOf(i.toString());
                    restrictPaymentMethods.add(tenderType);
                } catch (IllegalArgumentException ie) {
                    throw new Exception("paymentMethod type '" + i.toString() + "' is invalid");
                }
            }
        }

        return restrictPaymentMethods;
    }

    public Intent createChargeIntent(
            Integer totalAmount,
            CurrencyCode currencyCode,
            ArrayList<ChargeRequest.TenderType> restrictPaymentMethods,
            @Nullable Integer autoReturnTimeout,
            @Nullable String locationId,
            @Nullable String note) {
        ChargeRequest.Builder request = new ChargeRequest.Builder(totalAmount, currencyCode);

        if (!restrictPaymentMethods.isEmpty()) {
            request.restrictTendersTo(restrictPaymentMethods);
        }

        if(locationId != null) {
            request.enforceBusinessLocation(locationId);
        }

        if(note != null) {
            request.note(note);
        }

        if (autoReturnTimeout != null) {
            request.autoReturn(autoReturnTimeout, autoReturnTimeout.equals(PosApi.AUTO_RETURN_NO_TIMEOUT) ? null : TimeUnit.MILLISECONDS);
        }

        Intent intent = posClient.createChargeIntent(request.build());

        return intent;
    }

    public ChargeRequest.Success parseChargeSuccess(Intent data) {
        ChargeRequest.Success success = posClient.parseChargeSuccess(data);
        return success;
    }

    public ChargeRequest.Error parseChargeError(Intent data) {
        ChargeRequest.Error error = posClient.parseChargeError(data);
        return error;
    }

    public void openPointOfSalePlayStoreListing() {
        posClient.openPointOfSalePlayStoreListing();
    }
}
