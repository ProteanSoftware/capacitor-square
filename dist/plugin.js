var capacitorCapacitorSquare = (function (exports, core) {
    'use strict';

    /**
     * AutoReturn timeout values
     */
    exports.AutoReturn = void 0;
    (function (AutoReturn) {
        /**
         * No timeout
         */
        AutoReturn[AutoReturn["NoTimeout"] = 0] = "NoTimeout";
        /**
         * Minimum timeout value (3200 milliseconds)
         */
        AutoReturn[AutoReturn["Min"] = 3200] = "Min";
        /**
         * Maximum timeout value (10000 milliseconds)
         */
        AutoReturn[AutoReturn["Max"] = 10000] = "Max";
    })(exports.AutoReturn || (exports.AutoReturn = {}));

    const CapacitorSquare = core.registerPlugin('CapacitorSquare', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.CapacitorSquareWeb()),
    });

    class CapacitorSquareWeb extends core.WebPlugin {
        async initApp(_options) {
            throw new Error("Method not implemented.");
        }
        async startTransaction(_options) {
            throw new Error("Method not implemented.");
        }
        async handleIosResponse(_options) {
            throw new Error("Method not implemented.");
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        CapacitorSquareWeb: CapacitorSquareWeb
    });

    exports.CapacitorSquare = CapacitorSquare;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}, capacitorExports));
//# sourceMappingURL=plugin.js.map
