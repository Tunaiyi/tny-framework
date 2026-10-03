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
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class FileLoader implements Reloadable, NoticeReload {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

    private final String modelPath;

    private final Set<Reloadable> loadAfterList = ConcurrentHashMap.newKeySet();

    private volatile boolean load = false;

    private volatile boolean deleted = false;

    protected FileLoader(String modelPath) {
        this.modelPath = modelPath;
    }

    protected String getPath() {
        return this.modelPath;
    }

    public void load() throws Exception {
        InputStream input = FileIOAide.openInputStream(this.modelPath, new FileListener(this));
        if (input == null) {
            throw new FileNotFoundException(this.modelPath);
        }
        // doLoad 契约不保证关闭流，基类统一负责（原实现每次 load 泄漏一个 fd）
        try {
            this.readConfig(input, false);
        } finally {
            closeQuietly(input);
        }
        this.load = true;
    }

    private void readConfig(InputStream inputStream, boolean reload) throws Exception {
        if (inputStream != null) {
            this.doLoad(inputStream, reload);
        }
    }

    @Override
    public void reload() {
        try {
            InputStream input = FileIOAide.openInputStream(this.modelPath);
            if (input == null) {
                throw new FileNotFoundException(this.modelPath);
            }
            try {
                this.readConfig(input, true);
            } finally {
                closeQuietly(input);
            }
            for (Reloadable loader : this.loadAfterList)
                loader.reload();
        } catch (Exception e) {
            LOG.error("重新读取配置文件{}出错", this.modelPath, e);
        }
    }

    public void addModelLoader(FileLoader loader) {
        this.loadAfterList.add(loader);
    }

    @Override
    public void addReloadable(Reloadable reloadable) {
        this.loadAfterList.add(reloadable);
    }

    @Override
    public void removeReloadable(Reloadable reloadable) {
        this.loadAfterList.remove(reloadable);
    }

    @Override
    public void clearReloadable() {
        this.loadAfterList.clear();
    }

    protected abstract void doLoad(InputStream inputStream, boolean reload) throws Exception;

    private static void closeQuietly(InputStream input) {
        try {
            input.close();
        } catch (IOException e) {
            LOG.warn("关闭模型文件 {} 输入流异常", e.getMessage());
        }
    }

    /**
     * @author KGTny
     */
    public class FileListener extends FileAlterationListenerAdaptor {

        /**
         * 模型管理器
         *
         * @uml.property name="reloadable"
         * @uml.associationEnd
         */
        private Reloadable reloadable = null;

        public FileListener(Reloadable reloadable) {
            super();
            this.reloadable = reloadable;
        }

        @Override
        public void onFileChange(File file) {
            if (FileLoader.this.load) {
                this.reloadable.reload();
            }
        }

        @Override
        public void onFileDelete(File file) {
            FileLoader.this.deleted = true;
        }

        @Override
        public void onFileCreate(File file) {
            if (FileLoader.this.load && FileLoader.this.deleted) {
                this.reloadable.reload();
                FileLoader.this.deleted = false;
            }
        }

    }

}
