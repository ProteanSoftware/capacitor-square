import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SquarePayments)
public class SquarePayments: CAPPlugin {
    
    @objc func initApp(_ call: CAPPluginCall) {
        let value = call.getString("applicationId") ?? ""
        call.reject("NOT IMPLEMENTED")
    }

    @objc func startTransaction(_ call: CAPPluginCall) {
        call.reject("NOT IMPLEMENTED")
    }
}
