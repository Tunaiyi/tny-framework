package com.tny.game.scheduler;

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
     * 处理 <br>
     *
     * @param receiver 任务接收器
     */
    public void handle(TaskReceiver receiver);

    /**
     * 任务处理器名称 <br>
     *
     * @return 名称
     * @uml.property name="handlerName"
     */
    public String getHandlerName();

    /**
     * 处理方式
     *
     * @return
     */
    public HandleType getHandleType();

    /**
     * 任务处理器处理组 <br>
     *
     * @return 可处理的用户组
     */
    public boolean isHandleWith(Object group);

}
