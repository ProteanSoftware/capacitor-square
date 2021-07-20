import { WebPlugin, registerPlugin } from '@capacitor/core';
import { SquarePaymentsPlugin } from './definitions';

export class SquarePaymentsWeb extends WebPlugin implements SquarePaymentsPlugin {
  constructor() {
    super({
      name: 'SquarePayments',
      platforms: ['web']
    });
  }

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

// const SquarePayments = new SquarePaymentsWeb();

// export { SquarePayments };

// import { registerWebPlugin } from '@capacitor/core';
// registerWebPlugin(SquarePayments);


const SquarePayments = registerPlugin<SquarePaymentsWeb>('SquarePaymentsPlugin', {
  web: () => import('./web').then(m => new m.SquarePaymentsWeb())
});

export * from './definitions';
export { SquarePayments };
