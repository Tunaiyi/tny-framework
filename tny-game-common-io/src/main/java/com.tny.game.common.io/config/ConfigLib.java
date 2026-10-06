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

    /**
     * 读取属性文件。IO 失败/流不可用时返回 null（原实现返回空 Properties，
     * 会让 reload 用空表把在役配置整体清空——fail-open 事故源）。
     */
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
                LOG.error("#ConfigLib#打开 {} inputStream 为 null", path);
                return null;
            }
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException e) {
            LOG.error("#ConfigLib#初始化#读取 {} inputStream 抛出异常", path, e);
            return null;
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

    /** 导入装载栈（线程内 DFS 环检测）：互引用文件在解析期显式失败而非无限互递归 */
    private static final ThreadLocal<Deque<String>> LOADING = ThreadLocal.withInitial(ArrayDeque::new);

    static void checkImportCycle(String path) {
        Deque<String> stack = LOADING.get();
        if (stack.contains(path)) {
            throw new IllegalArgumentException("配置导入成环: " + String.join(" -> ", stack) + " -> " + path);
        }
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
            // 先读内容不挂监听；胜出者再挂——并发首载失败方不留孤儿监听器（reload 双跑根因）
            Properties properties = createProperties(path, null);
            config = new PropertiesConfig(properties != null ? properties : new Properties(), formatter);
            old = configMap.putIfAbsent(path, config);
            if (old == null) {
                FileIOAide.addFileListener(path, new ConfigFileListener(path));
            }
            LOG.info("ConfigLib 读取 {} 配置文件完成 | 耗时 {} ms", path, System.currentTimeMillis() - now);
        } else if (FileIOAide.resourceExists(path)) {
            // 归档形态（jar 等）：loadFile 定位不了文件 ≠ 资源缺失——走流通道读真实内容
            // （原缺陷：误判"不存在"交付空表的假成功面；监听不可用须显式告警而非静默）
            LOG.warn("ConfigLib {} 为非文件形态资源（如归档包内），热更监听不可用；按内容流读取交付", path);
            Properties properties = createProperties(path, null);
            config = new PropertiesConfig(properties != null ? properties : new Properties(), formatter);
            old = configMap.putIfAbsent(path, config);
        } else {
            // 资源确实不存在：空配置保持"文件缺失"合法语义（现契约不变）
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
        checkImportCycle(path);
        long now = System.currentTimeMillis();
        LOG.info("ConfigLib 读取 {} 配置文件", path);
        Deque<String> stack = LOADING.get();
        stack.push(path);
        Properties properties;
        try {
            properties = createProperties(path, null);
            if (properties == null) {
                // 导入/装载来源不可读：首载路径显式失败并携带来源链
                // （原缺陷：null 属性流被换成空表静默合并——"成功但缺内容"的假成功面）
                List<String> chain = new ArrayList<>(stack);
                Collections.reverse(chain);
                throw new IllegalArgumentException("配置来源不可读: " + path
                        + "，装载链: " + String.join(" -> ", chain));
            }
            // 构造期即解析 import 链——装载栈必须覆盖整个构建（reload 在弹栈后执行则环检测漏一跳）
            config = new PropertiesConfig(properties, formatter);
        } finally {
            stack.pop();
            if (stack.isEmpty()) {
                LOADING.remove();
            }
        }
        Config old = configMap.putIfAbsent(path, config);
        if (old == null) {
            FileIOAide.addFileListener(path, new ConfigFileListener(path));
        }
        LOG.info("ConfigLib 读取 {} 配置文件完成 | 耗时 {} ms", path, System.currentTimeMillis() - now);
        return old != null ? old : config;
    }

    public static Config newConfig(Properties properties, ConfigFormatter... formatters) {
        return new PropertiesConfig(properties, formatters);
    }

    public static Config newConfig(Map<String, Object> config, ConfigFormatter... formatters) {
        return new PropertiesConfig(config, formatters);
    }

    /** 热更监听器：包可见以便同包测试直接驱动 onFileChange 双分支断言 */
    static class ConfigFileListener extends FileAlterationListenerAdaptor {

        private final String path;

        ConfigFileListener(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onFileChange(File file) {
            Properties properties = createProperties(this.path, null);
            if (properties == null) {
                // 读取失败保留在役配置（fail-safe），不得用空表清空
                LOG.error("#ConfigLib#热更 {} 失败，保留当前配置", this.path);
                return;
            }
            PropertiesConfig config = configMap.get(this.path);
            if (config != null) {
                try {
                    config.reload(properties);
                } catch (Exception e) {
                    // 热更装载半途失败（如导入指向缺失文件）：换表未发生，保留在役配置并告警
                    // （与"读取失败保留"契约同向——轮询线程不得被异常打断）
                    LOG.error("#ConfigLib#热更 {} 失败，保留当前配置", this.path, e);
                }
            }
        }

    }

}
