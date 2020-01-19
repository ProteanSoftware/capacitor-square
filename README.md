# capacitor-square [![npm version](https://badge.fury.io/js/%40proteansoftware%2Fcapacitor-square.svg)](https://badge.fury.io/js/%40proteansoftware%2Fcapacitor-square)

Capacitor plugin that allows your app to integrate with square payments app


## API

| method             | info                                          | platform    |
| ------------------ | --------------------------------------------- | ----------- |
| `initApp`          | setup the plugin by providing your app id     | ios/android |
| `startTransaction` | start the transaction in the square app       | ios/android |

## Usage

```ts
import { Plugins } from "@capacitor/core";
const { SquarePayments } = Plugins;

//
// Initalise the square plugin
SquarePayments.initApp({
  applicationId: "Some square app id"
});

SquarePayments.startTransaction({
  totalAmount: 100, // amount in pennies/cents
  currencyCode: "GBP", // ISO currency code, must be support by square
  callbackUrl: "MyAppUrl://callback" // see iOS setup
})

```

## iOS setup

- `sudo gem install cocoapods` _(once a time)_
- `ionic start my-cap-app --capacitor`
- `cd my-cap-app`
- `mkdir www && touch www/index.html`
- `npx cap add ios`
- `npm install --save @proteansoftware/capacitor-square`
- `npx cap sync ios` _(always do sync after a plugin install)_
- `npx cap open ios`

Follow these setup steps from square to enable call back to your app: [Square Documentation](https://developer.squareup.com/docs/pos-api/build-on-ios#step-4-add-your-url-schemes).

## Android setup

- `ionic start my-cap-app --capacitor`
- `cd my-cap-app`
- `mkdir www && touch www/index.html`
- `npx cap add android`
- `npm install --save @proteansoftware/capacitor-square`
- `npx cap sync android` _(always do sync after a plugin install)_
- `npx cap open android`
- `[extra step]` in android case we need to tell Capacitor to initialise the plugin:

> on your `MainActivity.java` file add `import com.proteansoftware.capacitor.square.SquarePayments;
` and then inside the init callback `add(SquarePayments.class);`

Now you should be set to go. Try to run your client using `ionic cap run android --livereload`.

## License

MIT