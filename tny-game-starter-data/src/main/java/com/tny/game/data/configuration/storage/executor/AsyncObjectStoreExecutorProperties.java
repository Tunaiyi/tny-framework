/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
