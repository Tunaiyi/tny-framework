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
