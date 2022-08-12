import { PluginListenerHandle } from "@capacitor/core";

export type TransactionCompletedListener = (callback: { 
  clientTransactionId: string,
  serverTransactionId: string
}) => void;

export type TransactionFailedListener = (callback: { error: any }) => void;

/**
 * AutoReturn timeout values
 */
export enum AutoReturn {
  /**
   * No timeout
   */
  NoTimeout = 0,
  /**
   * Minimum timeout value (3200 milliseconds)
   */
  Min = 3200,
  /**
   * Maximum timeout value (10000 milliseconds)
   */
  Max = 10000
};

export interface CapacitorSquarePlugin {
  initApp(options: { applicationId: string }): Promise<{ message: string }>;

  startTransaction(options: {
    /**
     * amount in pennies/cents
     */
    totalAmount: number,
    /**
     * // ISO currency code, must be support by square
     */
    currencyCode: string,
    /**
     * Sqaure TendType: https://developer.squareup.com/docs/api/point-of-sale/android/com/squareup/sdk/pos/ChargeRequest.TenderType.html
     */
    allowedPaymentMethods?: string[] | null,
    /**
     * Android only - The timeout to set in milliseconds, or AutoReturn.NoTimeout. If you specify a timeout, it must be between 3200 milliseconds and 10000 milliseconds.
     */
    autoReturnTimeout?: number | AutoReturn.NoTimeout | null,
    /**
     * iOS only - No documentation available from Square
     */
    skipsReceipt?: boolean | null,
    /**
     * see iOS setup
     */
    callbackUrl?: string | null
  }): Promise<void>;

  handleIosResponse(options: { url: string }): Promise<void>;

  addListener(
    eventName: 'transactionComplete',
    listenerFunc: TransactionCompletedListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  addListener(
    eventName: 'transactionFailed',
    listenerFunc: TransactionFailedListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}
