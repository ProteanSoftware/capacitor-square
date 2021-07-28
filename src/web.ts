import { WebPlugin } from '@capacitor/core';

import type { CapacitorSquarePlugin } from './definitions';

export class CapacitorSquareWeb extends WebPlugin implements CapacitorSquarePlugin {
  async initApp(_options: { applicationId: string; }): Promise<{ message: string }> {
    throw new Error("Method not implemented.");
  }

  async startTransaction(_options: {
    totalAmount: number,
    currencyCode: string,
    allowedPaymentMethods?: string[],
    callbackUrl?: string
  }): Promise<void> {
    throw new Error("Method not implemented.");
  }

  async handleIosResponse(_options: { url: string; }): Promise<void> {
    throw new Error("Method not implemented.");
  }
}
