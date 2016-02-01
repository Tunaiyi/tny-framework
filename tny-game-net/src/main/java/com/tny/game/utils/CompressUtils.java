package com.tny.game.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

public class CompressUtils {

    private final static int CACHE_SIZE = 1024;

    public static byte[] compressBytes(byte input[]) throws IOException {
        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(input.length);
        final DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);
        DeflaterOutputStream compressStream = new DeflaterOutputStream(dataOutputStream);
        compressStream.write(input);
        compressStream.finish();
        compressStream.flush();
        compressStream.close();
        return byteOutputStream.toByteArray();
    }

    public static byte[] decompressBytes(byte input[]) throws IOException {
        final ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream(input.length);
        final ByteArrayInputStream compressDataStream = new ByteArrayInputStream(input);
        DeflaterInputStream compressStream = new DeflaterInputStream(compressDataStream);
        int readCount;
        byte data[] = new byte[CACHE_SIZE];
        while ((readCount = compressStream.read(data, 0, data.length)) != -1) {
            dataOutputStream.write(data, 0, readCount);
        }
        return dataOutputStream.toByteArray();
    }
}
