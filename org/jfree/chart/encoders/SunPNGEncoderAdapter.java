package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.jfree.chart.util.ParamChecks;

public class SunPNGEncoderAdapter implements ImageEncoder {
    public float getQuality() {
        return 0.0f;
    }

    public void setQuality(float quality) {
    }

    public boolean isEncodingAlpha() {
        return false;
    }

    public void setEncodingAlpha(boolean encodingAlpha) {
    }

    public byte[] encode(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encode(bufferedImage, outputStream);
        return outputStream.toByteArray();
    }

    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        ParamChecks.nullNotPermitted(bufferedImage, "bufferedImage");
        ParamChecks.nullNotPermitted(outputStream, "outputStream");
        ImageIO.write(bufferedImage, ImageFormat.PNG, outputStream);
    }
}
