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
package com.tny.game.net.command.processor;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/16 11:32 上午
 */
public enum CommandBoxStatus {

    /* executor停止 */
    STOP(CommandBoxConstants.STOP_VALUE),

    /* executor提交 */
    SUBMIT(CommandBoxConstants.SUBMIT_VALUE),

    /* executor执行 */
    PROCESSING(CommandBoxConstants.PROCESSING_VALUE),

    /* executor未完成延迟 */
    DELAY(CommandBoxConstants.DELAY_VALUE),

    //
    ;

    private final int id;

    CommandBoxStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
