package com.keypoint;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import org.jfree.chart.ChartPanel;

public class PngEncoder {
    public static final boolean ENCODE_ALPHA = true;
    public static final int FILTER_LAST = 2;
    public static final int FILTER_NONE = 0;
    public static final int FILTER_SUB = 1;
    public static final int FILTER_UP = 2;
    protected static final byte[] IDAT;
    protected static final byte[] IEND;
    protected static final byte[] IHDR;
    private static float INCH_IN_METER_UNIT;
    public static final boolean NO_ALPHA = false;
    protected static final byte[] PHYS;
    protected int bytePos;
    protected int bytesPerPixel;
    protected int compressionLevel;
    protected CRC32 crc;
    protected long crcValue;
    protected boolean encodeAlpha;
    protected int filter;
    protected int height;
    protected Image image;
    protected byte[] leftBytes;
    protected int maxPos;
    protected byte[] pngBytes;
    protected byte[] priorRow;
    protected int width;
    private int xDpi;
    private int yDpi;

    static {
        IHDR = new byte[]{(byte) 73, (byte) 72, (byte) 68, (byte) 82};
        IDAT = new byte[]{(byte) 73, (byte) 68, (byte) 65, (byte) 84};
        IEND = new byte[]{(byte) 73, (byte) 69, (byte) 78, (byte) 68};
        PHYS = new byte[]{(byte) 112, (byte) 72, (byte) 89, (byte) 115};
        INCH_IN_METER_UNIT = 0.0254f;
    }

    public PngEncoder() {
        this(null, false, FILTER_NONE, FILTER_NONE);
    }

    public PngEncoder(Image image) {
        this(image, false, FILTER_NONE, FILTER_NONE);
    }

    public PngEncoder(Image image, boolean encodeAlpha) {
        this(image, encodeAlpha, FILTER_NONE, FILTER_NONE);
    }

    public PngEncoder(Image image, boolean encodeAlpha, int whichFilter) {
        this(image, encodeAlpha, whichFilter, FILTER_NONE);
    }

    public PngEncoder(Image image, boolean encodeAlpha, int whichFilter, int compLevel) {
        this.crc = new CRC32();
        this.xDpi = FILTER_NONE;
        this.yDpi = FILTER_NONE;
        this.image = image;
        this.encodeAlpha = encodeAlpha;
        setFilter(whichFilter);
        if (compLevel >= 0 && compLevel <= 9) {
            this.compressionLevel = compLevel;
        }
    }

    public void setImage(Image image) {
        this.image = image;
        this.pngBytes = null;
    }

    public Image getImage() {
        return this.image;
    }

    public byte[] pngEncode(boolean encodeAlpha) {
        byte[] pngIdBytes = new byte[]{(byte) -119, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
        if (this.image == null) {
            return null;
        }
        this.width = this.image.getWidth(null);
        this.height = this.image.getHeight(null);
        this.pngBytes = new byte[((((this.width + FILTER_SUB) * this.height) * 3) + ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT)];
        this.maxPos = FILTER_NONE;
        this.bytePos = writeBytes(pngIdBytes, FILTER_NONE);
        writeHeader();
        writeResolution();
        if (writeImageData()) {
            writeEnd();
            this.pngBytes = resizeByteArray(this.pngBytes, this.maxPos);
        } else {
            this.pngBytes = null;
        }
        return this.pngBytes;
    }

    public byte[] pngEncode() {
        return pngEncode(this.encodeAlpha);
    }

    public void setEncodeAlpha(boolean encodeAlpha) {
        this.encodeAlpha = encodeAlpha;
    }

    public boolean getEncodeAlpha() {
        return this.encodeAlpha;
    }

    public void setFilter(int whichFilter) {
        this.filter = FILTER_NONE;
        if (whichFilter <= FILTER_UP) {
            this.filter = whichFilter;
        }
    }

    public int getFilter() {
        return this.filter;
    }

    public void setCompressionLevel(int level) {
        if (level >= 0 && level <= 9) {
            this.compressionLevel = level;
        }
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    protected byte[] resizeByteArray(byte[] array, int newLength) {
        byte[] newArray = new byte[newLength];
        System.arraycopy(array, FILTER_NONE, newArray, FILTER_NONE, Math.min(array.length, newLength));
        return newArray;
    }

    protected int writeBytes(byte[] data, int offset) {
        this.maxPos = Math.max(this.maxPos, data.length + offset);
        if (data.length + offset > this.pngBytes.length) {
            this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, data.length));
        }
        System.arraycopy(data, FILTER_NONE, this.pngBytes, offset, data.length);
        return data.length + offset;
    }

    protected int writeBytes(byte[] data, int nBytes, int offset) {
        this.maxPos = Math.max(this.maxPos, offset + nBytes);
        if (nBytes + offset > this.pngBytes.length) {
            this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, nBytes));
        }
        System.arraycopy(data, FILTER_NONE, this.pngBytes, offset, nBytes);
        return offset + nBytes;
    }

    protected int writeInt2(int n, int offset) {
        byte[] temp = new byte[FILTER_UP];
        temp[FILTER_NONE] = (byte) ((n >> 8) & 255);
        temp[FILTER_SUB] = (byte) (n & 255);
        return writeBytes(temp, offset);
    }

    protected int writeInt4(int n, int offset) {
        return writeBytes(new byte[]{(byte) ((n >> 24) & 255), (byte) ((n >> 16) & 255), (byte) ((n >> 8) & 255), (byte) (n & 255)}, offset);
    }

    protected int writeByte(int b, int offset) {
        byte[] temp = new byte[FILTER_SUB];
        temp[FILTER_NONE] = (byte) b;
        return writeBytes(temp, offset);
    }

    protected void writeHeader() {
        int startPos = writeInt4(13, this.bytePos);
        this.bytePos = startPos;
        this.bytePos = writeBytes(IHDR, this.bytePos);
        this.width = this.image.getWidth(null);
        this.height = this.image.getHeight(null);
        this.bytePos = writeInt4(this.width, this.bytePos);
        this.bytePos = writeInt4(this.height, this.bytePos);
        this.bytePos = writeByte(8, this.bytePos);
        this.bytePos = writeByte(this.encodeAlpha ? 6 : FILTER_UP, this.bytePos);
        this.bytePos = writeByte(FILTER_NONE, this.bytePos);
        this.bytePos = writeByte(FILTER_NONE, this.bytePos);
        this.bytePos = writeByte(FILTER_NONE, this.bytePos);
        this.crc.reset();
        this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
        this.crcValue = this.crc.getValue();
        this.bytePos = writeInt4((int) this.crcValue, this.bytePos);
    }

    protected void filterSub(byte[] pixels, int startPos, int width) {
        int offset = this.bytesPerPixel;
        int actualStart = startPos + offset;
        int nBytes = width * this.bytesPerPixel;
        int leftInsert = offset;
        int leftExtract = FILTER_NONE;
        for (int i = actualStart; i < startPos + nBytes; i += FILTER_SUB) {
            this.leftBytes[leftInsert] = pixels[i];
            pixels[i] = (byte) ((pixels[i] - this.leftBytes[leftExtract]) % 256);
            leftInsert = (leftInsert + FILTER_SUB) % 15;
            leftExtract = (leftExtract + FILTER_SUB) % 15;
        }
    }

    protected void filterUp(byte[] pixels, int startPos, int width) {
        int nBytes = width * this.bytesPerPixel;
        for (int i = FILTER_NONE; i < nBytes; i += FILTER_SUB) {
            byte currentByte = pixels[startPos + i];
            pixels[startPos + i] = (byte) ((pixels[startPos + i] - this.priorRow[i]) % 256);
            this.priorRow[i] = currentByte;
        }
    }

    protected boolean writeImageData() {
        int rowsLeft = this.height;
        int startRow = FILTER_NONE;
        this.bytesPerPixel = this.encodeAlpha ? 4 : 3;
        Deflater deflater = new Deflater(this.compressionLevel);
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream(ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH);
        DeflaterOutputStream compBytes = new DeflaterOutputStream(byteArrayOutputStream, deflater);
        while (rowsLeft > 0) {
            try {
                int nRows = Math.max(Math.min(32767 / (this.width * (this.bytesPerPixel + FILTER_SUB)), rowsLeft), FILTER_SUB);
                int[] pixels = new int[(this.width * nRows)];
                PixelGrabber pg = new PixelGrabber(this.image, FILTER_NONE, startRow, this.width, nRows, pixels, FILTER_NONE, this.width);
                try {
                    pg.grabPixels();
                    if ((pg.getStatus() & 128) != 0) {
                        System.err.println("image fetch aborted or errored");
                        return false;
                    }
                    byte[] scanLines = new byte[(((this.width * nRows) * this.bytesPerPixel) + nRows)];
                    if (this.filter == FILTER_SUB) {
                        this.leftBytes = new byte[16];
                    }
                    if (this.filter == FILTER_UP) {
                        this.priorRow = new byte[(this.width * this.bytesPerPixel)];
                    }
                    int startPos = FILTER_SUB;
                    int i = FILTER_NONE;
                    int scanPos = FILTER_NONE;
                    while (i < this.width * nRows) {
                        int scanPos2;
                        if (i % this.width == 0) {
                            scanPos2 = scanPos + FILTER_SUB;
                            scanLines[scanPos] = (byte) this.filter;
                            startPos = scanPos2;
                            scanPos = scanPos2;
                        }
                        scanPos2 = scanPos + FILTER_SUB;
                        scanLines[scanPos] = (byte) ((pixels[i] >> 16) & 255);
                        scanPos = scanPos2 + FILTER_SUB;
                        scanLines[scanPos2] = (byte) ((pixels[i] >> 8) & 255);
                        scanPos2 = scanPos + FILTER_SUB;
                        scanLines[scanPos] = (byte) (pixels[i] & 255);
                        if (this.encodeAlpha) {
                            scanPos = scanPos2 + FILTER_SUB;
                            scanLines[scanPos2] = (byte) ((pixels[i] >> 24) & 255);
                            scanPos2 = scanPos;
                        }
                        if (i % this.width == this.width - 1 && this.filter != 0) {
                            if (this.filter == FILTER_SUB) {
                                filterSub(scanLines, startPos, this.width);
                            }
                            if (this.filter == FILTER_UP) {
                                filterUp(scanLines, startPos, this.width);
                            }
                        }
                        i += FILTER_SUB;
                        scanPos = scanPos2;
                    }
                    compBytes.write(scanLines, FILTER_NONE, scanPos);
                    startRow += nRows;
                    rowsLeft -= nRows;
                } catch (Exception e) {
                    System.err.println("interrupted waiting for pixels!");
                    return false;
                }
            } catch (IOException e2) {
                System.err.println(e2.toString());
                return false;
            }
        }
        compBytes.close();
        byte[] compressedLines = byteArrayOutputStream.toByteArray();
        int nCompressed = compressedLines.length;
        this.crc.reset();
        this.bytePos = writeInt4(nCompressed, this.bytePos);
        this.bytePos = writeBytes(IDAT, this.bytePos);
        this.crc.update(IDAT);
        this.bytePos = writeBytes(compressedLines, nCompressed, this.bytePos);
        this.crc.update(compressedLines, FILTER_NONE, nCompressed);
        this.crcValue = this.crc.getValue();
        this.bytePos = writeInt4((int) this.crcValue, this.bytePos);
        deflater.finish();
        deflater.end();
        return ENCODE_ALPHA;
    }

    protected void writeEnd() {
        this.bytePos = writeInt4(FILTER_NONE, this.bytePos);
        this.bytePos = writeBytes(IEND, this.bytePos);
        this.crc.reset();
        this.crc.update(IEND);
        this.crcValue = this.crc.getValue();
        this.bytePos = writeInt4((int) this.crcValue, this.bytePos);
    }

    public void setXDpi(int xDpi) {
        this.xDpi = Math.round(((float) xDpi) / INCH_IN_METER_UNIT);
    }

    public int getXDpi() {
        return Math.round(((float) this.xDpi) * INCH_IN_METER_UNIT);
    }

    public void setYDpi(int yDpi) {
        this.yDpi = Math.round(((float) yDpi) / INCH_IN_METER_UNIT);
    }

    public int getYDpi() {
        return Math.round(((float) this.yDpi) * INCH_IN_METER_UNIT);
    }

    public void setDpi(int xDpi, int yDpi) {
        this.xDpi = Math.round(((float) xDpi) / INCH_IN_METER_UNIT);
        this.yDpi = Math.round(((float) yDpi) / INCH_IN_METER_UNIT);
    }

    protected void writeResolution() {
        if (this.xDpi > 0 && this.yDpi > 0) {
            int startPos = writeInt4(9, this.bytePos);
            this.bytePos = startPos;
            this.bytePos = writeBytes(PHYS, this.bytePos);
            this.bytePos = writeInt4(this.xDpi, this.bytePos);
            this.bytePos = writeInt4(this.yDpi, this.bytePos);
            this.bytePos = writeByte(FILTER_SUB, this.bytePos);
            this.crc.reset();
            this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
            this.crcValue = this.crc.getValue();
            this.bytePos = writeInt4((int) this.crcValue, this.bytePos);
        }
    }
}
