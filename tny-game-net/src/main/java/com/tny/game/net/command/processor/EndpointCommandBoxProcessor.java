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
package com.tny.game.net.command.processor;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 11:27 上午
 */
public abstract class EndpointCommandBoxProcessor<T extends CommandBoxProcess>
        implements CommandBoxProcessor, CommandBoxProcessExecutor<T> {

    private int busSpinTimes = 10;

    private int yieldTimes = 10;

    @Override
    public void submit(MessageCommandBox box) {
        driver(box).trySubmit();// 尝试提交
    }

    private CommandBoxProcess driver(MessageCommandBox box) {
        CommandBoxProcess driver = box.getAttachment(this);
        if (driver == null) {
            // 创角任务触发器
            driver = box.setAttachmentIfNull(this, () -> createDriver(box));
        }
        return driver;
    }

    protected abstract T createDriver(MessageCommandBox box);

    public void setBusSpinTimes(int busSpinTimes) {
        this.busSpinTimes = busSpinTimes;
    }

    public void setYieldTimes(int yieldTimes) {
        this.yieldTimes = yieldTimes;
    }

    @Override
    public int getBusSpinTimes() {
        return this.busSpinTimes;
    }

    @Override
    public int getYieldTimes() {
        return this.yieldTimes;
    }

}
