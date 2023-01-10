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
package com.tny.game.common.worker.command;

public abstract class BaseCommand implements Command {

    protected final String name;

    private boolean done = false;

    protected BaseCommand() {
        this(null);
    }

    public BaseCommand(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
        try {
            this.action();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            done = true;
        }
    }

    protected abstract void action() throws Throwable;

    @Override
    public String getName() {
        if (this.name == null) {
            return Command.super.getName();
        }
        return name;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

}
