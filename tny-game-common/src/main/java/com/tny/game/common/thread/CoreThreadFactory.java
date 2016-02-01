package com.tny.game.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreThreadFactory implements ThreadFactory {

    /**
     * @uml.property name="group"
     */
    private final ThreadGroup group;
    /**
     * @uml.property name="threadNumber"
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * @uml.property name="namePrefix"
     */
    private final String namePrefix;
    /**
     * @uml.property name="daemon"
     */
    private final boolean daemon;

    public CoreThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    public CoreThreadFactory(String namePrefix, boolean daemon) {
        SecurityManager localSecurityManager = System.getSecurityManager();
        this.group = ((localSecurityManager != null) ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = namePrefix + "-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable paramRunnable) {
        Thread localThread = new Thread(this.group, paramRunnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if (this.daemon)
            localThread.setDaemon(this.daemon);
        if (localThread.getPriority() != 5)
            localThread.setPriority(5);
        return localThread;
    }

}
