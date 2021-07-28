package com.tny.game.common.io.config;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

/**
 * @author KGTny
 * @ClassName: ModelFileListener
 * @Description: Model文件监听器
 * @date 2011-11-2 上午9:50:28
 * <p>
 * <p>
 * <br>
 */
public class FileListener extends FileAlterationListenerAdaptor {

    /**
     * 模型管理器
     *
     * @uml.property name="reloadable"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private Reloadable reloadable = null;

    public FileListener(Reloadable reloadable) {
        super();
        this.reloadable = reloadable;
    }

    @Override
    public void onFileChange(File file) {
        this.reloadable.reload();
    }

}
