/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.cache.testclass;

import com.tny.game.cache.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TestLinkHandler extends CacheFormatter<Object, Object> {

    @Override
    public Object format2Save(String key, Object object) {
        if (object == null) {
            return null;
        }
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
        if (bytes == null) {
            return null;
        }
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
