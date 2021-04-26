package pl.edu.pwr.lab3.i238162;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

public class ImageProcessingHelper {

    public static int[] getMiddlePixelAsRgb(ImageProxy image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int x = width / 2;
        int y = height / 2;
        return getPixelAsRgb(image, x, y);
    }

    public static int[] getPixelAsRgb(ImageProxy image, int x, int y) {
        ImageProxy.PlaneProxy planeY = image.getPlanes()[0];
        ByteBuffer bufferY = planeY.getBuffer();
        ImageProxy.PlaneProxy planeU = image.getPlanes()[1];
        ByteBuffer bufferU = planeU.getBuffer();
        ImageProxy.PlaneProxy planeV = image.getPlanes()[2];
        ByteBuffer bufferV = planeV.getBuffer();

        int widthRowY = planeY.getRowStride();
        int widthRowUV = planeU.getRowStride();
        int pixelStrideUV = planeU.getPixelStride();

        int uv_x = x / pixelStrideUV;
        int uv_y = y / pixelStrideUV;

        int yIndex = y * widthRowY + x;
        int uIndex = uv_y * widthRowUV + pixelStrideUV * uv_x;
        int vIndex = uv_y * widthRowUV + pixelStrideUV * uv_x;

        byte[] yuvPixel = {bufferY.get(yIndex), bufferU.get(uIndex), bufferV.get(vIndex)};
        return yuvToRgb(yuvPixel);
    }

    private static int[] yuvToRgb(byte[] yuvPixel) {
        float y = yuvPixel[0] & 0xFF;  // Y channel
        float cb = yuvPixel[1] & 0xFF; // U channel
        float cr = yuvPixel[2] & 0xFF; // V channel

        int rTemp = (int) ((y - 16) * 1.164 + (cr - 128) * 1.596);
        int gTemp = (int) ((y - 16) * 1.164 - (cb - 128) * 0.392 - (cr - 128) * 0.813);
        int bTemp = (int) ((y - 16) * 1.164 + (cb - 128) * 2.017);


        int r = Math.max(0, Math.min(255, rTemp));
        int g = Math.max(0, Math.min(255, gTemp));
        int b = Math.max(0, Math.min(255, bTemp));

        return new int[]{r, g, b};
    }
}
