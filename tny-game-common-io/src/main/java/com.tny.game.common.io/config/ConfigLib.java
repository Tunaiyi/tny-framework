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
import org.apache.commons.io.monitor.*;
import org.slf4j.*;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;

public class ConfigLib {

    protected static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

    private static final ConcurrentMap<String, PropertiesConfig> configMap = new ConcurrentHashMap<>();

    private static Properties createProperties(String path, FileAlterationListener listener) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            if (listener != null) {
                inputStream = FileIOAide.openInputStream(path, listener);
            } else {
                inputStream = FileIOAide.openInputStream(path);
            }
            if (inputStream == null) {
                throw new NullPointerException(MessageFormat.format("＃初始化 ConfigLib＃打开 {0} inputStream 为 null", path));
            }
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException e) {
            LOG.error("#ConfigLib#初始化#读取 {} inputStream 抛出异常", path, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("#ConfigLib#初始化#关闭 {} inputStream 抛出异常", path, e);
                }
            }
        }
        return properties;
    }

    public static Config getConfig(String path, ConfigFormatter... formatter) {
        PropertiesConfig config = configMap.get(path);
        if (config != null) {
            return config;
        }
        long now = System.currentTimeMillis();
        File file = FileIOAide.loadFile(path);
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

    public static Config getExistConfig(String path, ConfigFormatter... formatter) {
        PropertiesConfig config = configMap.get(path);
        if (config != null) {
            return config;
        }
        long now = System.currentTimeMillis();
        LOG.info("ConfigLib 读取 {} 配置文件", path);
        Properties properties = createProperties(path, new ConfigFileListener(path));
        config = new PropertiesConfig(properties, formatter);
        Config old = configMap.putIfAbsent(path, config);
        LOG.info("ConfigLib 读取 {} 配置文件完成 | 耗时 {} ms", path, System.currentTimeMillis() - now);
        return old != null ? old : config;
    }

    public static Config newConfig(Properties properties, ConfigFormatter... formatters) {
        return new PropertiesConfig(properties, formatters);
    }

    public static Config newConfig(Map<String, Object> config, ConfigFormatter... formatters) {
        return new PropertiesConfig(config, formatters);
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
            if (config != null) {
                config.reload(properties);
            }
        }

    }

}
