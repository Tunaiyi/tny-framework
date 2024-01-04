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

package com.tny.game.basics.mould;

public interface MouldHandler {

    /**
     * 模块类型
     *
     * @return
     */
    Mould getMould();

    /**
     * 开启模块
     *
     * @param launcher
     * @return
     */
    boolean openMould(FeatureLauncher launcher);

    /**
     * 加载模块
     *
     * @param launcher
     */
    void loadMould(FeatureLauncher launcher);

    /**
     * 删除模块
     *
     * @param launcher
     */
    void removeMould(FeatureLauncher launcher);

}
