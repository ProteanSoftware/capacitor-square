declare module "@capacitor/core" {
  interface PluginRegistry {
    SquarePayments: SquarePaymentsPlugin;
  }
}

export interface SquarePaymentsPlugin {
  initApp(options: { applicationId: string }): Promise<{message: string}>;
  startTransaction(options: { value: string }): Promise<{message: string, clientTransactionId: string}>;
}
