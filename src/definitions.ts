declare module "@capacitor/core" {
  interface PluginRegistry {
    SquarePayments: SquarePaymentsPlugin;
  }
}

export interface SquarePaymentsPlugin {
  initApp(options: { applicationId: string }): Promise<void>;
  startTransaction(options: { value: string }): Promise<void>;
}
