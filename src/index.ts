import { registerPlugin } from '@capacitor/core';

import type { CapacitorSquarePlugin } from './definitions';

const CapacitorSquare = registerPlugin<CapacitorSquarePlugin>('CapacitorSquare', {
  web: () => import('./web').then(m => new m.CapacitorSquareWeb()),
});

export * from './definitions';
export { CapacitorSquare };
