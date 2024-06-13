package com.example.Bot.utility;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

public class ImageProcessingUtils {

    public static BufferedImage applyMedianFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int size = 3;
        int[] pixels = new int[size * size];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                getPixels(image, x, y, size, pixels);
                Arrays.sort(pixels);
                int median = pixels[size * size / 2];
                resultImage.getRaster().setPixel(x, y, new int[]{median});
            }
        }

        return resultImage;
    }
    private static void getPixels(BufferedImage image, int x, int y, int size, int[] pixels) {
        int width = image.getWidth();
        int height = image.getHeight();
        int halfSize = size / 2;

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                int px = x - halfSize + i;
                int py = y - halfSize + j;
                px = Math.max(0, Math.min(px, width - 1));
                py = Math.max(0, Math.min(py, height - 1));

                pixels[j * size + i] = image.getRaster().getPixel(px, py, new int[1])[0];
            }
        }
    }

    public static BufferedImage adjustBrightness(BufferedImage image, float factor) {
        RescaleOp op = new RescaleOp(factor, 0, null);
        return op.filter(image, null);
    }

    public static BufferedImage adjustContrast(BufferedImage image, float factor) {
        RescaleOp op = new RescaleOp(factor, 128 * (1 - factor), null);
        return op.filter(image, null);
    }

    public static BufferedImage sharpen(BufferedImage image) {
        float[] SHARPEN_MATRIX = { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
        Kernel kernel = new Kernel(3, 3, SHARPEN_MATRIX);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }
}
