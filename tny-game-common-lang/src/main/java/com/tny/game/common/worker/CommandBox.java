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
