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

import com.tny.game.common.lifecycle.*;
import com.tny.game.data.storage.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/9 3:10 下午
 */
public class SpringForkJoinAsyncObjectStoreExecutor extends ForkJoinAsyncObjectStoreExecutor implements AppClosed {

    public SpringForkJoinAsyncObjectStoreExecutor(AsyncObjectStoreExecutorSetting setting) {
        super(setting);
    }

    @Override
    public PostCloser getPostCloser() {
        return PostCloser.value(this.getClass(), LifecycleLevel.POST_SYSTEM_LEVEL_1);
    }

    @Override
    public void onClosed() throws InterruptedException {
        this.shutdown();
    }

}
