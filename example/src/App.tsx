/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import 'react-native-reanimated';

import React, { useEffect, useState } from 'react';

import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';
import { runOnJS } from 'react-native-reanimated';
import {
  Camera,
  useCameraDevices,
  useFrameProcessor,
} from 'react-native-vision-camera';

import { Colors } from 'react-native/Libraries/NewAppScreen';
import calculateRotation from './utils/calculateRotation';
import getAnswer from './utils/answer';
import type { DetectionResult } from 'vision-camera-plugin-zxing';
import { scanBarcodes } from 'vision-camera-zxing-scanner';

const getPermission = async () => {
  const cameraPermission = await Camera.getCameraPermissionStatus();
  const microphonePermission = await Camera.getMicrophonePermissionStatus();

  return {
    cameraPermission,
    microphonePermission,
  };
};
const requestPermission = async () => {
  const cameraPermission = await Camera.requestCameraPermission();
  const microphonePermission = await Camera.requestCameraPermission();

  return {
    cameraPermission,
    microphonePermission,
  };
};
const CameraComponent = () => {
  const [hasPermission, setHasPermission] = useState(false);
  const [barcodes, setBarcodes] = useState<DetectionResult | null>();
  useEffect(() => {
    getPermission().then((res) => {
      if (
        res.cameraPermission === 'denied' ||
        res.cameraPermission === 'not-determined' ||
        res.microphonePermission === 'denied' ||
        res.microphonePermission === 'not-determined'
      ) {
        requestPermission().then((result) => {
          setHasPermission(result.cameraPermission === 'authorized');
        });
      }
    });
  }, []);
  const frameProcessor = useFrameProcessor((frame) => {
    'worklet';
    const value = scanBarcodes(frame, ['QRCode', 'DataMatrix'], {
      readByQuadrant: true,
      readMultiple: true,
    });
    console.log(value)
    runOnJS(setBarcodes)(value);
  }, []);
  const devices = useCameraDevices();
  const device = devices.back;
  if (!device || !hasPermission) {
    return <Text style={styles.highlight}>loading</Text>;
  }
  return (
    <>
      {device && hasPermission && (
        <Camera
          style={StyleSheet.absoluteFill}
          device={device}
          isActive={true}
          frameProcessor={frameProcessor}
          orientation="portrait"
        />
      )}
      {barcodes?.barcodes && (
        <Text style={styles.barcodeTextURL}>
          {` answer is ${barcodes.barcodes.length}`}
        </Text>
      )}
      {barcodes?.barcodes?.map((barcode, idx) => {
        const angle = calculateRotation(barcode.cornerPoints);
        const answer = getAnswer(angle);
        return (
          <Text key={idx} style={styles.barcodeTextURL}>
            {`${barcode.text} angle ${angle} answer is ${answer}`}
          </Text>
        );
      })}
    </>
  );
};

const App = () => {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
          height: '100%',
        }}
      >
        <CameraComponent />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  barcodeTextURL: {
    fontSize: 20,
    color: 'white',
    fontWeight: 'bold',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
