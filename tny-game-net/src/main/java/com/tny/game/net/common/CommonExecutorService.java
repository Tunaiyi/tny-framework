package com.tny.game.net.common;

import com.tny.game.common.concurrent.CoreThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class CommonExecutorService {

    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("CommonExecutorScheduledExecutor"));

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}
