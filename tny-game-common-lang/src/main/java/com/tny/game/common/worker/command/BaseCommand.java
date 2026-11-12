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
