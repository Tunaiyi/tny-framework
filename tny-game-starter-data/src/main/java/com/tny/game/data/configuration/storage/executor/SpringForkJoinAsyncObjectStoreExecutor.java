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
