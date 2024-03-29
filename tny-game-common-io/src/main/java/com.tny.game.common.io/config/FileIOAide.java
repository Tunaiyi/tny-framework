/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.io.config;

import com.tny.game.common.utils.*;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.slf4j.*;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;

public class FileIOAide {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

    private static FileMonitor MONITOR = null;

    static {
        initLoader(10000);
    }

    private static void initLoader(long time) {
        if (MONITOR == null) {
            MONITOR = new FileMonitor(time);
        }
    }

    public static File loadFile(String path) {
        LOG.info("#ConfigLoader#打开 {} ", path);
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            LOG.warn("#ConfigLoader#打开 {} 失败", path);
            return null;
        }
        return new File(url.getPath());
    }

    public static InputStream openInputStream(String path) throws IOException {
        return openInputStream(path, null);
    }

    public static InputStream openInputStream(String path, FileAlterationListener listener) throws IOException {
        LOG.info("#ConfigLoader#打开 {} ", path);
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            LOG.warn("#ConfigLoader#打开 {} 失败", path);
            throw new FileNotFoundException(MessageFormat.format("{0} 文件不存在", path));
        }
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            inputStream = new BufferedInputStream(inputStream);
            LOG.info("#ConfigLoader#打开 {} 成功", url);
            if (listener != null) {
                File file = new File(url.getFile());
                String filePath = file.toString().replace("\\", "/");
                MONITOR.addFileListener(filePath, listener);
            }
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            if (inputStream != null) {
                inputStream.close();
            }
            return null;
        }
        return inputStream;
    }

    // private static Map<Object, Object> createProperties(URL url) {
    // LOG.info("ConfigLoader loading " + url);
    // Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();
    // Properties property = new Properties();
    // InputStream inputStream = null;
    // try {
    // inputStream = url.openStream();
    // property.load(inputStream);
    // LOG.info("ConfigLoader loaded " + url + " finish");
    // map.putAll(property);
    // return map;
    // } catch (Exception e) {
    // LOG.error("ConfigLoader load exception", e);
    // } finally {
    // try {
    // inputStream.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // return null;
    // }

    public static void main(String[] args) {
        LOG.debug("add{}bs{}ss{}s", LogAide.msg("a", "d", "D"));
    }

}
