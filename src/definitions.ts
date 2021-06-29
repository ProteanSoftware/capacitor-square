declare global {
  interface PluginRegistry {
    SquarePayments: SquarePaymentsPlugin;
  }
}

export interface SquarePaymentsPlugin {
  initApp(options: { applicationId: string }): Promise<{ message: string }>;
  startTransaction(options: {
    totalAmount: number,
    currencyCode: string,
    allowedPaymentMethods?: string[],
    callbackUrl?: string
  }): Promise<void>;
  handleIosResponse(options: { url: string }): Promise<void>;
}
