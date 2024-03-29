#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorSquarePlugin, "CapacitorSquare",
           CAP_PLUGIN_METHOD(startTransaction, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(initApp, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(handleIosResponse, CAPPluginReturnPromise);
)
