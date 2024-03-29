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
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public abstract class FileLoader implements Reloadable, NoticeReload {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

    private final String modelPath;

    private final Set<Reloadable> loadAfterList = new HashSet<>();

    private boolean load = false;

    private boolean deleted = false;

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
        this.readConfig(input, false);
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
            this.readConfig(input, true);
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
