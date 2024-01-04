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
