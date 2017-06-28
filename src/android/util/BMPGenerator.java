package com.mcnc.bizmoblite.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BMPGenerator {
    public BMPGenerator() {
    }

    public static byte[] encodeBMP(Bitmap image, int imageBit) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = new int[height * width];
        image.getPixels(rgb, 0, width, 0, 0, width, height);
        return imageBit == 1 ? encodeBMP1(rgb, width, height) : (imageBit == 24 ? encodeBMP24(rgb, width, height) : encodeBMP32(rgb, width, height));
    }

    public static byte[] encodeBMP24(int[] rgb, int width, int height) throws IOException {
        int pad = (4 - width % 4) % 4;
        int size = 54 + height * (pad + width * 3);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(size);
        DataOutputStream stream = new DataOutputStream(bytes);
        stream.writeByte(66);
        stream.writeByte(77);
        stream.writeInt(swapEndian(size));
        stream.writeInt(0);
        stream.writeInt(swapEndian((int) 54));
        stream.writeInt(swapEndian((int) 40));
        stream.writeInt(swapEndian(width));
        stream.writeInt(swapEndian(height));
        stream.writeShort(swapEndian((short) 1));
        stream.writeShort(swapEndian((short) 24));
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);

        for (int out = height - 1; out >= 0; --out) {
            int i;
            for (i = 0; i < width; ++i) {
                int val = rgb[i + width * out];
                stream.writeByte(val & 255);
                stream.writeByte(val >>> 8 & 255);
                stream.writeByte(val >>> 16 & 255);
            }

            for (i = 0; i < pad; ++i) {
                stream.writeByte(0);
            }
        }

        byte[] var10 = bytes.toByteArray();
        bytes.close();
        if (var10.length != size) {
            throw new RuntimeException("bad math");
        } else {
            return var10;
        }
    }

    public static byte[] encodeBMP32(int[] rgb, int width, int height) throws IOException {
        int pad = (4 - width % 4) % 4;
        int size = 54 + height * (pad + width * 4);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(size);
        DataOutputStream stream = new DataOutputStream(bytes);
        stream.writeByte(66);
        stream.writeByte(77);
        stream.writeInt(swapEndian(size));
        stream.writeInt(0);
        stream.writeInt(swapEndian((int) 54));
        stream.writeInt(swapEndian((int) 40));
        stream.writeInt(swapEndian(width));
        stream.writeInt(swapEndian(height));
        stream.writeShort(swapEndian((short) 1));
        stream.writeShort(swapEndian((short) 32));
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);

        for (int out = height - 1; out >= 0; --out) {
            int i;
            for (i = 0; i < width; ++i) {
                int val = rgb[i + width * out];
                stream.writeInt(swapEndian(val));
            }

            for (i = 0; i < pad; ++i) {
                stream.writeByte(0);
            }
        }

        byte[] var10 = bytes.toByteArray();
        bytes.close();
        if (var10.length != size) {
            throw new RuntimeException("bad math");
        } else {
            return var10;
        }
    }

    public static byte[] encodeBMP1(int[] rgb, int width, int height) throws IOException {
        int pad = (4 - width / 8 % 4) % 4;
        int size = 62 + height * (pad + width / 8);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(size);
        DataOutputStream stream = new DataOutputStream(bytes);
        stream.writeByte(66);
        stream.writeByte(77);
        stream.writeInt(swapEndian(size));
        stream.writeInt(0);
        stream.writeInt(swapEndian((int) 62));
        stream.writeInt(swapEndian((int) 40));
        stream.writeInt(swapEndian(width));
        stream.writeInt(swapEndian(height));
        stream.writeShort(swapEndian((short) 1));
        stream.writeShort(swapEndian((short) 1));
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeInt(0);
        int len = height * width;

        for (int ccc = 0; ccc < len; ++ccc) {
            if (rgb[ccc] == -1) {
                rgb[ccc] = 1;
            } else {
                rgb[ccc] = 0;
            }
        }

        byte[] var13 = new byte[height * width / 8];
        len = var13.length;

        int nWidth;
        for (int ddd = 0; ddd < var13.length; ++ddd) {
            for (nWidth = 0; nWidth < 8; ++nWidth) {
                var13[ddd] = (byte) (var13[ddd] | rgb[ddd * 8 + nWidth] << 7 - nWidth);
            }
        }

        byte[] var14 = new byte[height * width / 8];
        nWidth = width / 8;

        for (int xxxx = 0; xxxx < height; ++xxxx) {
            for (int out = 0; out < nWidth; ++out) {
                var14[xxxx * nWidth + out] = var13[(height - 1 - xxxx) * nWidth + out];
            }
        }

        byte[] var15 = new byte[]{-1, -1, -1, -1};
        stream.write(var15);
        stream.write(var14);
        byte[] var16 = bytes.toByteArray();
        bytes.close();
        if (var16.length != size) {
            throw new RuntimeException("bad math");
        } else {
            return var16;
        }
    }

    private static int swapEndian(int value) {
        int b1 = value & 255;
        int b2 = value >> 8 & 255;
        int b3 = value >> 16 & 255;
        int b4 = value >> 24 & 255;
        return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
    }

    private static short swapEndian(short value) {
        int b1 = value & 255;
        int b2 = value >> 8 & 255;
        return (short) (b1 << 8 | b2 << 0);
    }
}