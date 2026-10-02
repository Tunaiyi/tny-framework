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
public interface CommandBoxConstants {

    /* executor停止 */
    int STOP_VALUE = 0;
    /* executor提交 */
    int SUBMIT_VALUE = 1;
    /* executor运行 */
    int PROCESSING_VALUE = 2;
    /* executor未完成延迟 */
    int DELAY_VALUE = 3;

}
