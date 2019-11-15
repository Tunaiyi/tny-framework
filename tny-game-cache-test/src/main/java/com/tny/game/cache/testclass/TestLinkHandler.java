package com.tny.game.cache.testclass;

import com.tny.game.cache.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TestLinkHandler extends CacheFormatter<Object, Object> {

    @Override
    public Object format2Save(String key, Object object) {
        if (object == null)
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = null;
        try {
            objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(object);
            return Base64.encodeBase64String(byteOut.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                objectOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object format2Load(String key, Object bytes) {
        if (bytes == null)
            return null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.decodeBase64((String) bytes));
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(byteIn);
            Object result = objectIn.readObject();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                objectIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
