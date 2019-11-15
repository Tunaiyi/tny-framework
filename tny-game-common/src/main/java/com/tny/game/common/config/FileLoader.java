package com.tny.game.common.config;

import com.tny.game.common.utils.*;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public abstract class FileLoader implements Reloadable, NoticeReload {

    private static final Logger LOG = LoggerFactory.getLogger(Logs.LOADER);

    private final String MODEL_PATH;

    private Set<Reloadable> loadAfterList = new HashSet<Reloadable>();

    private boolean load = false;
    private boolean deleted = false;

    protected FileLoader(String MODEL_PATH) {
        this.MODEL_PATH = MODEL_PATH;
    }

    protected String getPath() {
        return MODEL_PATH;
    }

    public void load() throws Exception {
        InputStream input = ConfigLoader.loadInputStream(this.MODEL_PATH, new FileListener(this));
        if (input == null)
            throw new FileNotFoundException(this.MODEL_PATH);
        this.readConfig(input, false);
        this.load = true;
    }

    private void readConfig(InputStream inputStream, boolean reload) throws Exception {
        if (inputStream != null)
            this.doLoad(inputStream, reload);
    }

    @Override
    public void reload() {
        try {
            InputStream input = ConfigLoader.loadInputStream(this.MODEL_PATH);
            if (input == null)
                throw new FileNotFoundException(this.MODEL_PATH);
            this.readConfig(input, true);
            for (Reloadable loader : this.loadAfterList)
                loader.reload();
        } catch (Exception e) {
            LOG.error("重新读取配置文件{}出错", this.MODEL_PATH, e);
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
            if (FileLoader.this.load)
                this.reloadable.reload();
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
