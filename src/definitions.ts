import { PluginListenerHandle } from "@capacitor/core";

export type TransactionCompletedListener = (callback: { clientTransactionId: string }) => void;

export type TransactionFailedListener = (callback: { error: any }) => void;

export interface CapacitorSquarePlugin {
  initApp(options: { applicationId: string }): Promise<{ message: string }>;

  startTransaction(options: {
    totalAmount: number,
    currencyCode: string,
    allowedPaymentMethods?: string[],
    callbackUrl?: string
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
