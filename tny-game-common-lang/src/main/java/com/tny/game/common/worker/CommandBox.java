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

package com.tny.game.common.worker;

import com.tny.game.common.worker.command.*;

/**
 * 命令箱
 *
 * @param <C>
 */

public interface CommandBox<C extends Command> {

    /**
     * @return box命令列表是否为空
     */
    boolean isEmpty();

    /**
     * @return 命令数量
     */
    int size();

    /**
     * 清除命令
     */
    void clear();

    /**
     * 接受命令
     *
     * @param command 命令
     * @return 是否接收成功
     */
    boolean accept(C command);

    /**
     * 绑定worker
     *
     * @param worker worker
     * @return 返回是否绑定成功
     */
    boolean bindWorker(CommandBoxWorker worker);

    /**
     * @return 是否解绑worker成功
     */
    boolean unbindWorker();

    /**
     * 提交给worker
     */
    void submit();

    /**
     * 处理Command
     */
    void process();

}
