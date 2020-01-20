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
    
    var supportedTenderTypes: SCCAPIRequestTenderTypes = SCCAPIRequestTenderTypes.init();
    if let allowedPaymentMethods = call.getArray("allowedPaymentMethods", String.self) {
        for paymentMethod in allowedPaymentMethods {
            switch paymentMethod {
            case "CARD":
                supportedTenderTypes.insert(SCCAPIRequestTenderTypes.card);
                break;
            case "CARD_ON_FILE":
                supportedTenderTypes.insert(SCCAPIRequestTenderTypes.cardOnFile);
                break;
            case "CASH":
                supportedTenderTypes.insert(SCCAPIRequestTenderTypes.cash);
                break;
            case "OTHER":
                supportedTenderTypes.insert(SCCAPIRequestTenderTypes.other);
                supportedTenderTypes.insert(SCCAPIRequestTenderTypes.squareGiftCard);
                break;
            default:
                call.reject("paymentMethod type '" + paymentMethod + "' is invalid");
                return;
            }
        }
    } else {
        supportedTenderTypes = SCCAPIRequestTenderTypes.all;
    }

    var request: SCCAPIRequest
    do {
        request = try SCCAPIRequest(callbackURL: yourCallbackURL,
                                    amount: amount,
                                    userInfoString: nil,
                                    merchantID: nil,
                                    notes: nil,
                                    customerID: nil,
                                    supportedTenderTypes: supportedTenderTypes,
                                    clearsDefaultFees: false,
                                    returnAutomaticallyAfterPayment: true)
    } catch {
      call.reject("unable to create request");
      return;
    }

    do {
      try SCCAPIConnection.perform(request)
    } catch {
      call.reject("unable to action request")
    }

    call.resolve();
  }

  @objc func handleIosResponse(_ call: CAPPluginCall) {
    if let url = call.getString("url") {
        var decodeError: Error?
      
        if let nsUrl = URL(string: url) {
            do {
                let response = try SCCAPIResponse(responseURL: nsUrl);
                
                if response.isSuccessResponse {
                  // Print checkout object
                  self.notifyListeners("transactionComplete", data: [
                      "message": "Transaction successful: \(response)"
                  ]);
                } else if decodeError != nil {
                    // Print decode error
                    if let decodeError = decodeError {
                        self.notifyListeners("transactionFailed", data: [
                            "message": "Decode Error: \(decodeError)"
                        ]);
                    }
                } else {
                    // Print the error code
                    self.notifyListeners("transactionFailed", data: [
                        "message": "Request failed: \(response.error)"
                    ]);
                }

                call.resolve();
            } catch {
                self.notifyListeners("transactionFailed", data: [
                    "message": "Error getting response"
                ]);
            }
        } else {
          call.reject("Url null");
        }
    } else {
      call.reject("Url null");
    }
  }
}
