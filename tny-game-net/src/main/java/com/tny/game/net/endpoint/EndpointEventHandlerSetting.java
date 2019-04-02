package com.tny.game.net.endpoint;

/**
 * <p>
 */
public interface EndpointEventHandlerSetting {

    /**
     * @return 处理器类
     */
    String getHandlerClass();

    /**
     * @return 线程数
     */
    int getThreads();

    /**
     * @return 消息分发器
     */
    String getMessageDispatcher();

    /**
     * @return 命令执行器
     */
    String getCommandExecutor();

}
