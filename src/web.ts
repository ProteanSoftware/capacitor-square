import { WebPlugin } from '@capacitor/core';

import type { AutoReturn, CapacitorSquarePlugin } from './definitions';

export class CapacitorSquareWeb extends WebPlugin implements CapacitorSquarePlugin {
  async initApp(_options: { applicationId: string; }): Promise<{ message: string }> {
    throw new Error("Method not implemented.");
  }

  async startTransaction(_options: {
    totalAmount: number,
    currencyCode: string,
    allowedPaymentMethods?: string[] | null,
    autoReturnTimeout?: number | AutoReturn.NoTimeout | null,
    callbackUrl?: string | null
  }): Promise<void> {
    throw new Error("Method not implemented.");
  }

  async handleIosResponse(_options: { url: string; }): Promise<void> {
    throw new Error("Method not implemented.");
  }
}
