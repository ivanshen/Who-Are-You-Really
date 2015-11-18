package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class EncoderUtil {
    public static byte[] encode(BufferedImage image, String format) throws IOException {
        return ImageEncoderFactory.newInstance(format).encode(image);
    }

    public static byte[] encode(BufferedImage image, String format, boolean encodeAlpha) throws IOException {
        return ImageEncoderFactory.newInstance(format, encodeAlpha).encode(image);
    }

    public static byte[] encode(BufferedImage image, String format, float quality) throws IOException {
        return ImageEncoderFactory.newInstance(format, quality).encode(image);
    }

    public static byte[] encode(BufferedImage image, String format, float quality, boolean encodeAlpha) throws IOException {
        return ImageEncoderFactory.newInstance(format, quality, encodeAlpha).encode(image);
    }

    public static void writeBufferedImage(BufferedImage image, String format, OutputStream outputStream) throws IOException {
        ImageEncoderFactory.newInstance(format).encode(image, outputStream);
    }

    public static void writeBufferedImage(BufferedImage image, String format, OutputStream outputStream, float quality) throws IOException {
        ImageEncoderFactory.newInstance(format, quality).encode(image, outputStream);
    }

    public static void writeBufferedImage(BufferedImage image, String format, OutputStream outputStream, boolean encodeAlpha) throws IOException {
        ImageEncoderFactory.newInstance(format, encodeAlpha).encode(image, outputStream);
    }

    public static void writeBufferedImage(BufferedImage image, String format, OutputStream outputStream, float quality, boolean encodeAlpha) throws IOException {
        ImageEncoderFactory.newInstance(format, quality, encodeAlpha).encode(image, outputStream);
    }
}
