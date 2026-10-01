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
import java.util.*;
import java.util.concurrent.*;

public class FileIOAide {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

    /** 可观测记录上限：防长跑进程无界累积，超出后保留首见记录 */
    private static final int UNMONITORABLE_MAX = 128;

    /** 带监听请求但形态不可监听（如归档包内条目）而被拒绝登记的资源（与显式告警配套的观测面） */
    private static final CopyOnWriteArrayList<String> UNMONITORABLE = new CopyOnWriteArrayList<>();

    private static FileMonitor MONITOR = null;

    static {
        initLoader(10000);
    }

    private static void initLoader(long time) {
        if (MONITOR == null) {
            MONITOR = new FileMonitor(time);
        }
    }

    /**
     * 取全局共享监视设施；显式停止后按需重建（全新空单元，不携带历史登记——停止后既有回调静默）。
     */
    private static synchronized FileMonitor ensureMonitor() {
        if (MONITOR == null) {
            initLoader(10000);
        }
        return MONITOR;
    }

    /**
     * 测试专用装配（仅包内可见）：以给定轮询间隔重建全局监视设施，供热更/监听时序断言压缩等待。
     * 旧设施不停止（守护线程随 JVM 退出，孤儿观察对象无害），避免连坐其他在途登记。
     */
    static synchronized void useMonitorIntervalForTest(long intervalMillis) {
        MONITOR = new FileMonitor(intervalMillis);
    }

    /**
     * 全局监视设施显式停止入口（resource-loading-robustness「生命周期可解释」契约，BREAKING-ADD 纯增量）：
     * 供进程收尾与测试隔离使用。停止幂等、不向调用方抛出任何异常；停止后既有监听回调不再被触发；
     * 设施状态清零（登记数归零），后续监听登记按需重建设施。
     */
    public static synchronized void stopMonitor() {
        final FileMonitor monitor = MONITOR;
        MONITOR = null;
        UNMONITORABLE.clear();
        if (monitor != null) {
            // FileMonitor.stop 已收容底层异常并留日志：停止不逃逸
            monitor.stop();
        }
    }

    /**
     * 测试专用观测（仅包内可见）：全局设施当前有效登记数（登记/移除成对、不累积泄漏的断言依据）。
     */
    static int monitorListenerCountForTest() {
        final FileMonitor monitor = MONITOR;
        return monitor == null ? 0 : monitor.listenerCount();
    }

    /**
     * 测试专用观测（仅包内可见）：全局设施当前目录级监视单元数（"单目录一单元"断言依据）。
     */
    static int monitorObserverCountForTest() {
        final FileMonitor monitor = MONITOR;
        return monitor == null ? 0 : monitor.observerCount();
    }

    /**
     * 测试专用观测（仅包内可见）：类路径资源解码定位后的监听登记数；
     * 资源缺失或不可监听形态恒为 0（不存在挂在伪路径上的登记）。
     */
    static int listenerRegistrationCountForTest(String path) {
        final FileMonitor monitor = MONITOR;
        if (monitor == null) {
            return 0;
        }
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null || !"file".equals(url.getProtocol())) {
            return 0;
        }
        final String filePath;
        try {
            filePath = new File(decodedPath(url)).toString().replace("\\", "/");
        } catch (Exception e) {
            return 0;
        }
        return monitor.listenerCount(filePath);
    }

    /**
     * 测试专用观测（仅包内可见）：带监听打开但形态不可监听而被拒绝登记的资源记录。
     */
    static List<String> unmonitorableRegistrationsForTest() {
        return Collections.unmodifiableList(new ArrayList<>(UNMONITORABLE));
    }

    /**
     * 测试专用观测（仅包内可见）：清空不可监听形态记录，保证断言起点确定。
     */
    static void clearUnmonitorableRegistrationsForTest() {
        UNMONITORABLE.clear();
    }

    private static void recordUnmonitorable(String path) {
        if (UNMONITORABLE.size() < UNMONITORABLE_MAX) {
            UNMONITORABLE.add(path);
        }
    }

    /**
     * 资源是否存在于当前 classpath（归档/jar 形态亦可判定；与 loadFile 的"可作为文件定位"区分）。
     */
    public static boolean resourceExists(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path) != null;
    }

    /**
     * URL 路径还原 %20 等百分号转义为真实文件系统路径——定位与监听登记两侧 MUST 同口径
     * （原监听登记用字面转义路径，含空格资源永不被观察器命中）。
     */
    private static String decodedPath(final URL url) {
        return java.net.URLDecoder.decode(url.getPath(), java.nio.charset.StandardCharsets.UTF_8);
    }

    public static File loadFile(String path) {
        LOG.info("#ConfigLoader#打开 {} ", path);
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            LOG.warn("#ConfigLoader#打开 {} 失败", path);
            return null;
        }
        String protocol = url.getProtocol();
        if (!"file".equals(protocol)) {
            // jar 等非文件形态：URL→File 假路径既定位不了也监听不了，显式告警而非静默假成功
            LOG.warn("#ConfigLoader# {} 位于 {} 协议资源（如 jar 内），无法作为文件定位/监听；内容读取请走 openInputStream", path, protocol);
            return null;
        }
        try {
            // 还原 %20 等转义（原 url.getPath() 直转 File，空格路径 exists()==false）
            return new File(decodedPath(url));
        } catch (Exception e) {
            LOG.warn("#ConfigLoader#解析 {} 文件路径失败", url, e);
            return null;
        }
    }

    /**
     * 注册文件变更监听（供"胜出实例后挂监听"的时序使用；非文件协议资源显式告警不注册）。
     */
    public static void addFileListener(String path, org.apache.commons.io.monitor.FileAlterationListener listener) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null || !"file".equals(url.getProtocol())) {
            LOG.warn("#ConfigLoader# {} 非可监听文件资源（缺失或 {} 协议），热更监听未注册", path,
                    url == null ? "无" : url.getProtocol());
            if (url != null) {
                // 形态不可监听（如归档包内条目）：登记拒绝留痕，供告警可断言
                recordUnmonitorable(path);
            }
            return;
        }
        String filePath;
        try {
            // 与 loadFile 同口径解码转义（原字面 %20 路径登记后观察器永不命中）
            filePath = new File(decodedPath(url)).toString().replace("\\", "/");
        } catch (Exception e) {
            LOG.warn("#ConfigLoader#解析 {} 监听路径失败，热更监听未注册", url, e);
            return;
        }
        ensureMonitor().addFileListener(filePath, listener);
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
            if (listener != null && "file".equals(url.getProtocol())) {
                // 与 loadFile 同口径解码 %XX 转义（原登记字面转义路径，含空格资源监听永不命中）
                File file = new File(decodedPath(url));
                String filePath = file.toString().replace("\\", "/");
                ensureMonitor().addFileListener(filePath, listener);
            } else if (listener != null) {
                // 不可监听形态（归档包内条目）：显式告警 + 登记拒绝留痕，内容读取不受影响
                LOG.warn("#ConfigLoader# {} 为 {} 协议资源，热更监听不可用（内容读取不受影响）", path, url.getProtocol());
                recordUnmonitorable(path);
            }
        } catch (IOException e) {
            LOG.error("#ConfigLoader#打开 {} 失败：流不可读（保留 null 契约，调用方受控失败）", path, e);
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
