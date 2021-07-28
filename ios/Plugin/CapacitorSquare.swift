import Foundation

@objc public class CapacitorSquare: NSObject {
    static var ApplicationId = "";

    @objc public func setApplicationID(_ applicationId: String) {
        SquarePayments.ApplicationId = applicationId;
    }

    @objc public func isInitalised() -> boolean {
        return SquarePayments.ApplicationId.count > 0;
    }

    
}
