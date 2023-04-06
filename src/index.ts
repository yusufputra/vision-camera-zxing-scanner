import { Platform } from 'react-native';
import type { Frame } from 'react-native-vision-camera';
import type {
  BarcodeFormat,
  DetectionOptions,
  DetectionResult,
} from 'vision-camera-plugin-zxing';
import { detectBarcodes as detectBarcodesIos } from 'vision-camera-plugin-zxing'

export function scanBarcodes(
  frame: Frame,
  formats: BarcodeFormat[],
  options: DetectionOptions = {}
): DetectionResult | null {
  'worklet';
  if (Platform.OS === 'android') {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return __scanBarcodes(frame, formats, options);
  } else {
    return detectBarcodesIos(frame, formats, options)
  }
}

