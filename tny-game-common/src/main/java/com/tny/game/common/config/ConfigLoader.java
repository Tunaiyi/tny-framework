package com.tny.game.common.config;

import com.tny.game.LogUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;

public class ConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(LogUtils.LOADER);

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
        final String configPath = path;
        LOG.info("#ConfigLoader#打开 {} ", path);
        final URL url = Thread.currentThread().getContextClassLoader().getResource(configPath);
        if (url == null) {
            LOG.warn("#ConfigLoader#打开 {} 失败", path);
            return null;
        }
        //		System.out.println(url.getPath());
        return new File(url.getPath());
    }

    public static InputStream loadInputStream(String path) throws IOException {
        return loadInputStream(path, null);
    }

    public static InputStream loadInputStream(String path, FileAlterationListener listener) throws IOException {
        final String configPath = path;
        LOG.info("#ConfigLoader#打开 {} ", path);
        final URL url = Thread.currentThread().getContextClassLoader().getResource(configPath);
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
                if (inputStream != null)
                    MONITOR.addFileListener(filePath, listener);
            }
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            if (inputStream != null)
                inputStream.close();
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
        LOG.debug("add{}bs{}ss{}s", LogUtils.msg("a", "d", "D"));
    }

}
