/**
 * AutoReturn timeout values
 */
export var AutoReturn;
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
})(AutoReturn || (AutoReturn = {}));
;
//# sourceMappingURL=definitions.js.map