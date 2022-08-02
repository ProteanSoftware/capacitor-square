# @proteansoftware/capacitor-square

Integrate with Square Payments SDK

## Install

```bash
npm install @proteansoftware/capacitor-square
npx cap sync
```

## Usage

App Initalisation - app.component.ts (Angular example)

```ts
import { App } from "@capacitor/app";
import { Platform } from "@ionic/angular";
import { CapacitorSquare } from "@proteansoftware/capacitor-square";

export class AppComponent {
  constructor(private platform: Platform) {
    this.initializeApp();
  }
  
  private void initializeApp() {
    this.platform.ready().then(() => {
      App.addListener("appUrlOpen", (data: URLOpenListenerEvent) => {
        console.log("appUrlOpen: " + data.url);

        if (data.url.toLowerCase().startsWith("app-url-scheme://callback-url")) {
          CapacitorSquare.handleIosResponse({
            url: data.url
          }).then(() => {
            console.log("handle ios callback - successful");
          }).catch(e => {
            console.error("handle ios callback - error - " + e);
          });
        }
      });
    });
  }
}
```

Payment flow
```ts
import { CapacitorSquare } from "@proteansoftware/capacitor-square";

//
// Initalise the square plugin
CapacitorSquare.initApp({
  applicationId: "Some square app id"
}).then(() => {
  console.log("Square payment ready");
}).catch(error => {
  console.error(error);
});

// Listen for sucessful payments
CapacitorSquare.addListener("transactionComplete", callback => {
  console.log("clientTransactionId:" + callback.clientTransactionId);
  console.log("serverTransactionId:" + callback.serverTransactionId);
});

// Listen for failed payments
CapacitorSquare.addListener("transactionFailed", callback => {
  console.error(callback.error);
});

// Initiate a transaction
CapacitorSquare.startTransaction({
  totalAmount: 100, // amount in pennies/cents
  currencyCode: "GBP", // ISO currency code, must be support by square
  allowedPaymentMethods: ["CARD"], // Sqaure TendType: https://developer.squareup.com/docs/api/point-of-sale/android/com/squareup/sdk/pos/ChargeRequest.TenderType.html
  callbackUrl: "app-url-scheme://callback-url" // see iOS setup
}).then(() => {
  console.log("transaction started");
}).catch(error => {
  console.error(error);
});

```

Follow these setup steps from square to enable call back to your app: [Square Documentation](https://developer.squareup.com/docs/pos-api/build-on-ios#step-4-add-your-url-schemes).


## API

<docgen-index>

* [`initApp(...)`](#initapp)
* [`startTransaction(...)`](#starttransaction)
* [`handleIosResponse(...)`](#handleiosresponse)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initApp(...)

```typescript
initApp(options: { applicationId: string; }) => any
```

| Param         | Type                                    |
| ------------- | --------------------------------------- |
| **`options`** | <code>{ applicationId: string; }</code> |

**Returns:** <code>any</code>

--------------------


### startTransaction(...)

```typescript
startTransaction(options: { totalAmount: number; currencyCode: string; allowedPaymentMethods?: string[]; callbackUrl?: string; }) => any
```

| Param         | Type                                                                                                          |
| ------------- | ------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ totalAmount: number; currencyCode: string; allowedPaymentMethods?: {}; callbackUrl?: string; }</code> |

**Returns:** <code>any</code>

--------------------


### handleIosResponse(...)

```typescript
handleIosResponse(options: { url: string; }) => any
```

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(eventName: 'transactionComplete', listenerFunc: TransactionCompletedListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>"transactionComplete"</code>                                                                |
| **`listenerFunc`** | <code>(callback: { clientTransactionId: string; serverTransactionId: string; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(eventName: 'transactionFailed', listenerFunc: TransactionFailedListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                |
| ------------------ | --------------------------------------------------- |
| **`eventName`**    | <code>"transactionFailed"</code>                    |
| **`listenerFunc`** | <code>(callback: { error: any; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |

</docgen-api>
