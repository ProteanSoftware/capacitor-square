declare module "@capacitor/core" {
  interface PluginRegistry {
    SquarePayments: SquarePaymentsPlugin;
  }
}

export interface SquarePaymentsPlugin {
  init(options: { applicationId: string }): Promise<void>;
  startTransaction(options: { value: string }): Promise<void>;
}
