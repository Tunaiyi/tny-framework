package com.tny.game.common.config;

import com.tny.game.LogUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfigLib {

    protected static final Logger LOG = LoggerFactory.getLogger(LogUtils.LOADER);

    private static ConcurrentMap<String, PropertiesConfig> configMap = new ConcurrentHashMap<String, PropertiesConfig>();

    private static Properties createProperties(String path, FileAlterationListener listener) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            if (listener != null)
                inputStream = ConfigLoader.loadInputStream(path, listener);
            else
                inputStream = ConfigLoader.loadInputStream(path);
            if (inputStream == null)
                throw new NullPointerException(MessageFormat.format("＃初始化 ConfigLib＃打开 {0} inputStream 为 null", path));
            properties.load(inputStream);
        } catch (IOException e) {
            LOG.error("#ConfigLib#初始化#读取 {} inputStream 抛出异常", path, e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("#ConfigLib#初始化#关闭 {} inputStream 抛出异常", path, e);
                }
        }
        return properties;
    }

    public static Config getConfigExist(String path, ConfigFormatter... formatter) {
        PropertiesConfig config = configMap.get(path);
        if (config != null)
            return config;
        long now = System.currentTimeMillis();
        File file = ConfigLoader.loadFile(path);
        Config old;
        if (file != null && file.exists()) {
            LOG.info("ConfigLib 读取 {} 配置文件", path);
            Properties properties = createProperties(path, new ConfigFileListener(path));
            config = new PropertiesConfig(properties, formatter);
            old = configMap.putIfAbsent(path, config);
            LOG.info("ConfigLib 读取 {} 配置文件完成 | 耗时 {} ms", path, System.currentTimeMillis() - now);
        } else {
            config = new PropertiesConfig(new HashMap<>(), formatter);
            old = configMap.putIfAbsent(path, config);
        }
        return old != null ? old : config;
    }

    public static Config getConfig(String path, ConfigFormatter... formatter) {
        PropertiesConfig config = configMap.get(path);
        if (config != null)
            return config;
        long now = System.currentTimeMillis();
        LOG.info("ConfigLib 读取 {} 配置文件", path);
        Properties properties = createProperties(path, new ConfigFileListener(path));
        config = new PropertiesConfig(properties, formatter);
        Config old = configMap.putIfAbsent(path, config);
        LOG.info("ConfigLib 读取 {} 配置文件完成 | 耗时 {} ms", path, System.currentTimeMillis() - now);
        return old != null ? old : config;
    }

    private static class ConfigFileListener extends FileAlterationListenerAdaptor {

        private final String path;

        private ConfigFileListener(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onFileChange(File file) {
            PropertiesConfig config = configMap.get(this.path);
            Properties properties = createProperties(this.path, null);
            if (config != null)
                config.reload(properties);
        }

    }

}
