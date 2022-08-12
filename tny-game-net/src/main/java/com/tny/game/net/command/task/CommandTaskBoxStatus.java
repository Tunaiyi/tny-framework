/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.task;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/16 11:32 上午
 */
public enum CommandTaskBoxStatus {

    /* executor停止 */
    STOP(CommandTaskBoxConstants.STOP_VALUE),

    /* executor提交 */
    SUBMIT(CommandTaskBoxConstants.SUBMIT_VALUE),

    /* executor执行 */
    PROCESSING(CommandTaskBoxConstants.PROCESSING_VALUE),

    /* executor未完成延迟 */
    DELAY(CommandTaskBoxConstants.DELAY_VALUE),

    //
    ;

    private final int id;

    CommandTaskBoxStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
