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

package com.tny.game.data.configuration.storage.executor;

import com.tny.game.data.storage.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/28 1:07 下午
 */
@ConfigurationProperties(prefix = "tny.data.store-executor.fork-join")
public class AsyncObjectStoreExecutorProperties extends AsyncObjectStoreExecutorSetting {

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public AsyncObjectStoreExecutorProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

}
