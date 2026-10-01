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

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.monitor.*;
import org.slf4j.*;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author KGTny
 * @ClassName: FileMonitor
 * @Description: 文件修改观察对象
 * @date 2011-10-12 下午10:39:26
 * <p>
 * <p>
 * 负责文件的监控<br>
 * <p>
 * 目录级监视单元（resource-loading-robustness「同目录多文件共享一个目录级监视单元，变更通知不被放大」）：
 * 监视表按规范化父目录键控——同一目录的多个登记共享同一个观察器，单个目录一次轮询周期只检视一次；
 * 逐文件的事件隔离由 PathScopedListener 包装器承担——某文件的变更只投递该文件自己的回调且每周期
 * 恰一次，不随同目录其他已登记文件的数量放大。不含目录分隔的裸文件名按当前目录 "." 定位并并入同单元。
 */
public class FileMonitor {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.FILE_MONITOR);

    /**
     * 目录级监视单元表：键为规范化的父目录（裸文件名并入当前目录 "."）
     */
    private final ConcurrentMap<String, FileAlterationObserver> observerMap = new ConcurrentHashMap<>();

    /**
     * 登记账本：规范化文件路径 →（监听器 → 其挂载在目录单元上的包装器）。
     * 移除据此成对执行；未登记路径的移除不产生任何新单元（移除不是追加）。
     */
    private final ConcurrentMap<String, ConcurrentMap<FileAlterationListener, PathScopedListener>> registeredMap = new ConcurrentHashMap<>();

    /**
     * @uml.property name="monitor"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private final FileAlterationMonitor monitor;

    /** 停止终态标记：重复 stop 幂等无操作（commons-io 对未运行监视器抛"Monitor is not running"，不应以异常/错误日志形态重现） */
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public FileMonitor() {
        this(1000L);
    }

    public FileMonitor(long interval) {
        this.monitor = new FileAlterationMonitor(interval);
        try {
            this.monitor.setThreadFactory(new CoreThreadFactory("FileMonitorTread", true));
            this.monitor.start();
        } catch (Exception e) {
            LOG.error("#启动文件监听器#启动异常 ", e);
        }
    }

    /**
     * <p>
     * 添加文件监控监听器<br>
     * <p>
     * 同一文件同一监听器重复登记幂等（不双挂）；不同文件按父目录共享监视单元。
     *
     * @param listener 文件监控监听器
     */
    public void addFileListener(String path, FileAlterationListener listener) {
        if (listener == null) {
            return;
        }
        String normed = normalizePath(path);
        PathScopedListener scoped = new PathScopedListener(normed, listener);
        ConcurrentMap<FileAlterationListener, PathScopedListener> bag = this.registeredMap.computeIfAbsent(normed,
                key -> new ConcurrentHashMap<>());
        if (bag.putIfAbsent(listener, scoped) != null) {
            return;
        }
        this.getDirectoryObserver(directoryOf(normed)).addListener(scoped);
    }

    /**
     * <p>
     * <p>
     * 添加文件监听器集合<br>
     *
     * @param listenerList 文件监听器集合
     */
    public void addFileListener(String path, Collection<FileAlterationListener> listenerList) {
        for (FileAlterationListener listener : listenerList)
            this.addFileListener(path, listener);
    }

    /**
     * <p>
     * <p>
     * 删除文件监听器<br>
     * <p>
     * 未登记路径的移除为无害空操作：MUST NOT 因移除动作新建监视单元或登记
     * （回归：原实现移除误调 addListener 变追加；且 getObserver 副作用会为陌生路径建单元）。
     *
     * @param listener 删除的文件监听器
     */
    public void removeFileListener(String path, FileAlterationListener listener) {
        String normed = normalizePath(path);
        ConcurrentMap<FileAlterationListener, PathScopedListener> bag = this.registeredMap.get(normed);
        if (bag == null) {
            return;
        }
        PathScopedListener scoped = bag.remove(listener);
        if (scoped == null) {
            return;
        }
        FileAlterationObserver observer = this.observerMap.get(directoryOf(normed));
        if (observer != null) {
            observer.removeListener(scoped);
        }
    }

    public void stop() {
        if (!this.stopped.compareAndSet(false, true)) {
            // 重复 stop 幂等无操作（停止入口对调用方永不逃逸异常）
            return;
        }
        try {
            this.monitor.stop();
        } catch (Exception e) {
            LOG.error("#关闭文件监听器#关闭异常 ", e);
        }
    }

    /**
     * 测试/运维观测（仅包内可见）：当前有效登记数（登记与移除成对、不累积泄漏的断言依据）。
     */
    int listenerCount() {
        int count = 0;
        for (ConcurrentMap<FileAlterationListener, PathScopedListener> bag : this.registeredMap.values())
            count += bag.size();
        return count;
    }

    /**
     * 测试/运维观测（仅包内可见）：指定文件路径规范化后的有效登记数。
     */
    int listenerCount(String path) {
        ConcurrentMap<FileAlterationListener, PathScopedListener> bag = this.registeredMap.get(normalizePath(path));
        return bag == null ? 0 : bag.size();
    }

    /**
     * 测试/运维观测（仅包内可见）：当前目录级监视单元数（"单目录一单元、一轮询只检视一次"的断言依据）。
     */
    int observerCount() {
        return this.observerMap.size();
    }

    /**
     * 取（或按需一次性创建）目录级共享监视单元：computeIfAbsent 保证并发下同目录仅建一个单元。
     */
    private FileAlterationObserver getDirectoryObserver(String dirKey) {
        return this.observerMap.computeIfAbsent(dirKey, key -> {
            // 目录粒度共享单元：不带文件过滤（整目录一轮询只检视一次），
            // 逐文件事件隔离由 PathScopedListener 包装器完成
            FileAlterationObserver observer = new FileAlterationObserver(new File(key), (FileFilter) null,
                    IOCase.INSENSITIVE);
            this.monitor.addObserver(observer);
            return observer;
        });
    }

    /**
     * 路径规范化：分隔符统一为 "/"（与 FileIOAide 解码登记口径一致）。
     */
    private static String normalizePath(String path) {
        return path.replace('\\', '/');
    }

    /**
     * 父目录键：裸文件名（无目录分隔）按当前目录 "." 定位并入同单元；"/x" 这类根下文件归 "/"。
     */
    private static String directoryOf(String normedPath) {
        int lastIndex = normedPath.lastIndexOf('/');
        if (lastIndex < 0) {
            return ".";
        }
        if (lastIndex == 0) {
            return "/";
        }
        return normedPath.substring(0, lastIndex);
    }

    /**
     * 路径作用域包装器：挂载于目录级共享单元上，仅把与本登记文件路径一致的文件级事件投递给委托者——
     * 变更只触发该文件自己的回调且每周期恰一次，不随同目录其他登记文件数量放大。
     * 观察者级与目录级事件维持既有透传形态。
     */
    private static final class PathScopedListener implements FileAlterationListener {

        private final String filePath;

        private final FileAlterationListener delegate;

        private PathScopedListener(String filePath, FileAlterationListener delegate) {
            super();
            this.filePath = filePath;
            this.delegate = delegate;
        }

        /**
         * 事件文件与本登记路径同口径匹配：绝对路径相等，或（裸文件名登记时）目录列举的
         * "./name" 以后缀命中；分隔符先归一为 "/"。
         */
        private boolean matches(File file) {
            String path = normalizePath(file.getPath());
            return path.equals(this.filePath) || path.endsWith("/" + this.filePath);
        }

        @Override
        public void onStart(FileAlterationObserver observer) {
            this.delegate.onStart(observer);
        }

        @Override
        public void onDirectoryCreate(File directory) {
            this.delegate.onDirectoryCreate(directory);
        }

        @Override
        public void onDirectoryChange(File directory) {
            this.delegate.onDirectoryChange(directory);
        }

        @Override
        public void onDirectoryDelete(File directory) {
            this.delegate.onDirectoryDelete(directory);
        }

        @Override
        public void onFileCreate(File file) {
            if (this.matches(file)) {
                this.delegate.onFileCreate(file);
            }
        }

        @Override
        public void onFileChange(File file) {
            if (this.matches(file)) {
                this.delegate.onFileChange(file);
            }
        }

        @Override
        public void onFileDelete(File file) {
            if (this.matches(file)) {
                this.delegate.onFileDelete(file);
            }
        }

        @Override
        public void onStop(FileAlterationObserver observer) {
            this.delegate.onStop(observer);
        }

    }

}
