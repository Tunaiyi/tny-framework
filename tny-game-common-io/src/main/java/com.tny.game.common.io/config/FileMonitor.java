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

/**
 * @author KGTny
 * @ClassName: FileMonitor
 * @Description: 文件修改观察对象
 * @date 2011-10-12 下午10:39:26
 * <p>
 * <p>
 * 负责文件的监控<br>
 */
public class FileMonitor {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.FILE_MONITOR);

    /**
     * 观察者
     *
     * @uml.property name="observerMap"
     * @uml.associationEnd qualifier="path:java.lang.String org.apache.commons.io.monitor.FileAlterationObserver"
     */
    private final ConcurrentMap<String, FileAlterationObserver> observerMap = new ConcurrentHashMap<>();

    /**
     * @uml.property name="monitor"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private final FileAlterationMonitor monitor;

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
     *
     * @param listener 文件监控监听器
     */
    public void addFileListener(String path, FileAlterationListener listener) {
        this.getObserver(path).addListener(listener);
    }

    /**
     * <p>
     * <p>
     * 添加文件监听器集合<br>
     *
     * @param listenerList 文件监听器集合
     */
    public void addFileListener(String path, Collection<FileAlterationListener> listenerList) {
        FileAlterationObserver observer = this.getObserver(path);
        for (FileAlterationListener listener : listenerList)
            observer.addListener(listener);
    }

    /**
     * <p>
     * <p>
     * 删除文件监听器<br>
     *
     * @param listener 删除的文件监听器
     */
    public void removeFileListener(String path, FileAlterationListener listener) {
        this.getObserver(path).addListener(listener);
    }

    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            LOG.error("#关闭文件监听器#关闭异常 ", e);
        }
    }

    private FileAlterationObserver getObserver(String path) {
        FileAlterationObserver observer = this.observerMap.get(path);
        if (observer == null) {
            int lastIndex = path.lastIndexOf("/");
            observer = new FileAlterationObserver(path.substring(0, lastIndex), new ConfigFileFilter(path), IOCase.INSENSITIVE);
            FileAlterationObserver oldObserver = this.observerMap.putIfAbsent(path, observer);
            if (oldObserver == null) {
                this.monitor.addObserver(observer);
                return observer;
            }
            return oldObserver;
        }
        return observer;
    }

    private static class ConfigFileFilter implements FileFilter {

        private String file;

        private ConfigFileFilter(String file) {
            super();
            this.file = file;
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.getPath().replace("\\", "/").endsWith(this.file);
        }

    }

}
