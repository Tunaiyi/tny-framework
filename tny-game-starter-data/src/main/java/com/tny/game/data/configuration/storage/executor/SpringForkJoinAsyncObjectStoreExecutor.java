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
