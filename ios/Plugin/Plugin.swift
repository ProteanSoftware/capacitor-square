import Foundation
import Capacitor
import SquarePointOfSaleSDK

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SquarePayments)
public class SquarePayments: CAPPlugin {
    static var ApplicationId = "";

    @objc func initApp(_ call: CAPPluginCall) {
        if let applicationId = call.getString("applicationId") {
            SquarePayments.ApplicationId = applicationId;
            SCCAPIRequest.setClientID(applicationId);
          call.resolve([
            "message": "set applicationId"
          ]);
        } else {
          call.reject("applicationId null")
        }
    }

    @objc func startTransaction(_ call: CAPPluginCall) {
        if SquarePayments.ApplicationId.count == 0 {
        call.reject("client not setup, call initApp first")
      }

      let callbackUrl = call.getString("callbackUrl") ?? "";
      if callbackUrl.count == 0 {
        call.reject("callbackUrl null")
      }

      let totalAmount = call.getInt("totalAmount") ?? 0;
      if(totalAmount <= 0) {
        call.reject("totalAmount must be greater than 0");
        return;
      }

      let currencyCode = call.getString("currencyCode") ?? "";
      if(currencyCode.count == 0) {
        call.reject("currencyCode is null");
        return;
      }

      let yourCallbackURL = URL(string: callbackUrl)!

      // Specify the amount of money to charge.
      var amount: SCCMoney
      do {
          amount = try SCCMoney(amountCents: totalAmount, currencyCode: currencyCode);
      } catch {
        call.reject("unable to create amount, maybe invalid currenyCode");
        return;
      }

      var request: SCCAPIRequest
      do {
          request = try SCCAPIRequest(callbackURL: yourCallbackURL,
                                      amount: amount,
                                      userInfoString: nil,
                                      merchantID: nil,
                                      notes: "Coffee",
                                      customerID: nil,
                                      supportedTenderTypes: SCCAPIRequestTenderTypes.all,
                                      clearsDefaultFees: false,
                                      returnAutomaticallyAfterPayment: false)
      } catch {
        call.reject("unable to create request");
        return;
      }

      var success = false
      do {
        try SCCAPIConnection.perform(request)
          success = true
      } catch {}
    }
}
