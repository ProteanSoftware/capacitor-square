import { registerPlugin } from '@capacitor/core';
const CapacitorSquare = registerPlugin('CapacitorSquare', {
    web: () => import('./web').then(m => new m.CapacitorSquareWeb()),
});
export * from './definitions';
export { CapacitorSquare };
//# sourceMappingURL=index.js.map