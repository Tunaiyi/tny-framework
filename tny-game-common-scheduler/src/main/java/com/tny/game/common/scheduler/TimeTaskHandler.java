package com.tny.game.common.scheduler;

/**
 * @author KGTny
 * @ClassName : TimeTaskHandler
 * @Description : 时间任务处理器
 * @date 2011-10-28 下午4:16:13 时间任务处理器
 * <p>
 * <br>
 */
public interface TimeTaskHandler {

    /**
     * 任务处理器名称 <br>
     *
     * @return 名称
     * @uml.property name="handlerName"
     */
    String getName();

    /**
     * 处理方式
     *
     * @return
     */
    HandleType getHandleType();

    /**
     * 处理 <br>
     *
     * @param receiver 任务接收器
     */
    void handle(TaskReceiver receiver, long executeTime, TriggerContext context);

    /**
     * 任务处理器处理组 <br>
     *
     * @return 可处理的用户组
     */
    boolean isHandleWith(TaskReceiverType group);

}
