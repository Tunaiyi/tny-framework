package com.tny.game.net.endpoint;

/**
 * <p>
 */
public interface EndpointEventHandlerSetting {

    /**
     * @return 处理器类
     */
    String getEventHandlerClass();

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

    //    /**
    //     * @return 凭证工厂
    //     */
    //    String getCertificateFactory();

    /**
     * @return 获取 endpoint keeper manager
     */
    String getEndpointKeeperManager();

}
