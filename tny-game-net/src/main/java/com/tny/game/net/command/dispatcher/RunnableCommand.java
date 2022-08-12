/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.*;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableCommand extends BaseCommand {

    private final Runnable runnable;

    public RunnableCommand(Runnable runnable) {
        super("RunnableCommand");
        this.runnable = runnable;
    }

    @Override
    protected void action() {
        this.runnable.run();
    }

    @Override
    public String getName() {
        return this.runnable.getClass().getCanonicalName();
    }

}

