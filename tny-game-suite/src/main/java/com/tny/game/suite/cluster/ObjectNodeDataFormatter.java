package com.tny.game.suite.cluster;

import com.tny.game.zookeeper.*;

import java.io.*;

public class ObjectNodeDataFormatter implements NodeDataFormatter {

    @Override
    public byte[] data2Bytes(Object data) {
        ObjectOutputStream objectOPStream = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            objectOPStream = new ObjectOutputStream(outputStream);
            objectOPStream.writeObject(data);
            objectOPStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOPStream != null) {
                try {
                    objectOPStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <D> D bytes2Data(byte[] bytes) {
        ObjectInputStream objectIPStream = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            objectIPStream = new ObjectInputStream(inputStream);
            return (D)objectIPStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIPStream != null) {
                try {
                    objectIPStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
