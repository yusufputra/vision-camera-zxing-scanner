# vision-camera-zxing-scanner
a plugin for react-native-vision-camera to scan a barcode using zxing. this library also using `vision-camera-plugin-zxing` for scanning on IOS
## Installation

```sh
yarn add vision-camera-plugin-zxing
yarn add vision-camera-zxing-scanner
```
make sure you correctly setup `react-native-reanimated` and insert as a first line of your `index.tsx`

Add this to your `babel.config.js`

```js
[
    'react-native-reanimated/plugin',
    {
        globals: ['__detectBarcodes', '__scanBarcodes'],
    },
],
```

## Usage

```js
const frameProcessor = useFrameProcessor((frame) => {
    'worklet';
    const value = scanBarcodes(frame, ['QRCode', 'DataMatrix'], {
        readByQuadrant: true,
        readMultiple: true,
    });
    runOnJS(setBarcodes)(value);
}, []);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
