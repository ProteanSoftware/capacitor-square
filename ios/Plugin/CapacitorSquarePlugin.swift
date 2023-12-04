import Foundation
import Capacitor
import SquarePointOfSaleSDK

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorSquarePlugin)
public class CapacitorSquarePlugin: CAPPlugin {
    private let implementation = CapacitorSquare()

    @objc func initApp(_ call: CAPPluginCall) {
        if let applicationId = call.getString("applicationId") {
        
        implementation.setApplicationID(applicationId);
        call.resolve([
          "message": "set applicationId"
        ]);
      } else {
        call.reject("applicationId null")
      }
    }

    @objc func startTransaction(_ call: CAPPluginCall) {
        if !implementation.isInitalised() {
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

        //add optional location id
        let locationId = call.getString("locationId") ?? nil;

        // handle a note 
        let note = call.getString("note") ?? "";
        if(note.count >= 500) {
            call.reject("Note is too long");
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
                                        locationID: locationId,
                                        notes: note,
                                        customerID: nil,
                                        supportedTenderTypes: supportedTenderTypes,
                                        clearsDefaultFees: false,
                                        returnsAutomaticallyAfterPayment: true,
                                        disablesKeyedInCardEntry: false,
                                        skipsReceipt: false)
        } catch (let exception) {
            call.reject("unable to create request: " + exception.localizedDescription);
            return;
        }

        do {
            try SCCAPIConnection.perform(request)
            } catch (let exception) {
                call.reject("unable to action request: " + exception.localizedDescription)
            }

        call.resolve();
    }
    
    @objc func handleIosResponse(_ call: CAPPluginCall) {
         if let url = call.getString("url") {
            let decodeError: Error? = nil;

             if let nsUrl = URL(string: url) {
                 do {
                     let response = try SCCAPIResponse(responseURL: nsUrl);

                     if response.isSuccessResponse {
                       // Print checkout object
                       self.notifyListeners("transactionComplete", data: [
                           "message": "Transaction successful: \(response)",
                           "clientTransactionId": response.clientTransactionID ?? "missing id",
                           "serverTransactionId": response.transactionID ?? "missing id"
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
                             "message": "Request failed: \(String(describing: response.error))"
                         ]);
                     }

                     call.resolve();
                 } catch {
                     self.notifyListeners("transactionFailed", data: [
                         "message": "Error getting response"
                     ]);
                     call.resolve();
                 }
             } else {
               call.reject("Url null");
             }
         } else {
           call.reject("Url null");
         }
       }
}
