import { WebPlugin } from '@capacitor/core';
import type { AutoReturn, CapacitorSquarePlugin } from './definitions';
export declare class CapacitorSquareWeb extends WebPlugin implements CapacitorSquarePlugin {
    initApp(_options: {
        applicationId: string;
    }): Promise<{
        message: string;
    }>;
    startTransaction(_options: {
        totalAmount: number;
        currencyCode: string;
        allowedPaymentMethods?: string[] | null;
        autoReturnTimeout?: number | AutoReturn.NoTimeout | null;
        callbackUrl?: string | null;
    }): Promise<void>;
    handleIosResponse(_options: {
        url: string;
    }): Promise<void>;
}
