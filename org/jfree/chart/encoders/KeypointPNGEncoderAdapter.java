package org.jfree.chart.encoders;

import com.keypoint.PngEncoder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.jfree.chart.util.ParamChecks;

public class KeypointPNGEncoderAdapter implements ImageEncoder {
    private boolean encodingAlpha;
    private int quality;

    public KeypointPNGEncoderAdapter() {
        this.quality = 9;
        this.encodingAlpha = false;
    }

    public float getQuality() {
        return (float) this.quality;
    }

    public void setQuality(float quality) {
        this.quality = (int) quality;
    }

    public boolean isEncodingAlpha() {
        return this.encodingAlpha;
    }

    public void setEncodingAlpha(boolean encodingAlpha) {
        this.encodingAlpha = encodingAlpha;
    }

    public byte[] encode(BufferedImage bufferedImage) throws IOException {
        ParamChecks.nullNotPermitted(bufferedImage, "bufferedImage");
        return new PngEncoder(bufferedImage, this.encodingAlpha, 0, this.quality).pngEncode();
    }

    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        ParamChecks.nullNotPermitted(bufferedImage, "bufferedImage");
        ParamChecks.nullNotPermitted(outputStream, "outputStream");
        outputStream.write(new PngEncoder(bufferedImage, this.encodingAlpha, 0, this.quality).pngEncode());
    }
}
