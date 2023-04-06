package com.visioncamerazxingscanner;

import androidx.camera.core.ImageProxy;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.RGBLuminanceSource;
import android.graphics.Bitmap;
import android.media.Image;
import android.graphics.YuvImage;
import android.graphics.ImageFormat;
import java.io.ByteArrayOutputStream;
import android.graphics.Rect;
import android.graphics.BitmapFactory;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableNativeArray;

public class VisionCameraZxingScannerModule extends FrameProcessorPlugin {
  private MultiFormatReader reader;
  private MultipleBarcodeReader mReader;

  public VisionCameraZxingScannerModule() {
    super("scanBarcodes");
    reader = new MultiFormatReader();
  }

  private Bitmap toBitmap(Image image) {
    Image.Plane[] planes = image.getPlanes();
    ByteBuffer yBuffer = planes[0].getBuffer();
    ByteBuffer uBuffer = planes[1].getBuffer();
    ByteBuffer vBuffer = planes[2].getBuffer();

    int ySize = yBuffer.remaining();
    int uSize = uBuffer.remaining();
    int vSize = vBuffer.remaining();

    byte[] nv21 = new byte[ySize + uSize + vSize];
    // U and V are swapped
    yBuffer.get(nv21, 0, ySize);
    vBuffer.get(nv21, ySize, vSize);
    uBuffer.get(nv21, ySize + vSize, uSize);

    YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

    byte[] imageBytes = out.toByteArray();
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
  }

  @Override
  public Object callback(ImageProxy image, Object[] params) {
    reader.setHints(getHints(params[0]));
    mReader = new GenericMultipleBarcodeReader(reader);
    Image images = image.getImage();
    Bitmap bitmap = toBitmap(images);
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    Result[] results = null;
    try {
      results = mReader
          .decodeMultiple(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))));
    } catch (Exception e) {
      e.printStackTrace();
    }
    List resultArray = new ArrayList<>();
    Map<String, Object> barcodeResponse = new HashMap<String, Object>();
    if (results != null) {
      for (Result result : results) {
        if (result != null) {
          Map<String, Object> resultData = new HashMap<String, Object>();
          resultData.put("type", result.getBarcodeFormat().toString());
          resultData.put("text", result.getText());

          ResultPoint[] points = result.getResultPoints();
          if (points != null && points.length > 0) {
            List pointData = new ArrayList<>();
            for (ResultPoint point : points) {
              Map<String, Integer> xyData = new HashMap<String, Integer>();
              xyData.put("x", ((int) point.getX()));
              xyData.put("y", ((int) point.getY()));
              pointData.add(xyData);
            }
            resultData.put("cornerPoints", pointData);
          }
          resultArray.add(resultData);
        }
      }
    }
    barcodeResponse.put("barcodes", resultArray);
    barcodeResponse.put("width", width);
    barcodeResponse.put("height", height);
    return barcodeResponse;
  }

  private static Map<DecodeHintType, Object> getHints(Object barcodeTypes) {
    ReadableArray barcodeTypeList = (ReadableNativeArray)barcodeTypes;

    Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
    List<BarcodeFormat> formats = new ArrayList<>();
    for (int i = 0; i < barcodeTypeList.size(); i++) {
      formats.add(getBarcodeFormat(barcodeTypeList.getString(i)));
    }
    Log.d("FORMATQR", formats.toString());
    hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
    hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
    return hints;
  }

  private static final BarcodeFormat getBarcodeFormat(String type){
    switch (type) {
      case "Aztec":
        return BarcodeFormat.AZTEC;
      case "Codabar":
        return BarcodeFormat.CODABAR;
      case "Code39":
        return BarcodeFormat.CODE_39;
      case "Code93":
        return BarcodeFormat.CODE_93;
      case "Code128":
        return BarcodeFormat.CODE_128;
      case "DataMatrix":
        return BarcodeFormat.DATA_MATRIX;
      case "Ean8":
        return BarcodeFormat.EAN_8;
      case "Ean13":
        return BarcodeFormat.EAN_13;
      case "ITF":
        return BarcodeFormat.ITF;
      case "MaxiCode":
        return BarcodeFormat.MAXICODE;
      case "PDF417":
        return BarcodeFormat.PDF_417;
      case "QRCode":
        return BarcodeFormat.QR_CODE;
      case "RSS14":
        return BarcodeFormat.RSS_14;
      case "RSSExpanded":
        return BarcodeFormat.RSS_EXPANDED;
      case "UPCA":
        return BarcodeFormat.UPC_A;
      case "UPCE":
        return BarcodeFormat.UPC_E;
      case "UPCEANExtension":
        return BarcodeFormat.UPC_EAN_EXTENSION;
    }
    return null;
  }
}
