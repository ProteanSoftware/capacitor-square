import { WebPlugin } from '@capacitor/core';
import { SquarePaymentsPlugin } from './definitions';

export class SquarePaymentsWeb extends WebPlugin implements SquarePaymentsPlugin {
  constructor() {
    super({
      name: 'SquarePayments',
      platforms: ['web']
    });
  }

  async initApp(_options: { applicationId: string; }): Promise<{message: string}> {
    throw new Error("Method not implemented.");
  }

  async startTransaction(_options: { value: string }): Promise<{message: string, clientTransactionId: string}> {
    throw new Error("Method not implemented.");
  }
}

const SquarePayments = new SquarePaymentsWeb();

export { SquarePayments };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(SquarePayments);
