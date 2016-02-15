package com.tny.game.actor;

import com.tny.game.actor.event.EventStream;

import java.util.Collection;

/**
 * Actor系统,通过配置构造ActorSystem,如ActorMailDispatcher, ActorWorker等
 * 可通过ActorSystem获取ActorRef
 *
 * @param <M>
 * @author KGTny
 */
public interface ActorSystem extends ActorRefFactory {

    /**
     * 构建/user/ + childPath的路径对象
     *
     * @param childPath 节点或子路径名字
     * @return 返回新创建的 path
     */
    ActorPath asPath(String childPath);

    /**
     * 构建/user/childPath[0]/childPath[1]/.../childPath[N]的路径对象
     *
     * @param childPath 节点或子路径名字列表
     * @return 返回新创建的 path
     */
    ActorPath asPath(Collection<String> childPath);

    /**
     * ActorSystem名字
     *
     * @return 返回名字
     */
    String name();

    /**
     * 注册一个关闭ActorSystem时调用的任务
     *
     * @param runnable 关闭ActorSystem时调用的任务
     */
    void registerOnTermination(Runnable runnable);

    /**
     * 获取调度器
     *
     * @return 调度器
     */
    Scheduler scheduler();

    /**
     * 获取消息流
     * @return
     */
    EventStream eventStream();

    /**
     * @return 获取死亡消息投递栏
     */
    ActorRef getDeadLetters();


    /**
     * 获取处理无用消息,或停止和不存在ActorRef消息的ActorRef
     * @return 处理死消息ActorRef
     */
    //	 ActorRef<Object> deadLetters();

    /**
     * ActorMailDispatcher 的管理器
     * @return 返回Dispatcher管理器
     */
    //	 ActorMailDispatchers dispatchers();

    /**
     * ActorMailBox 的管理器
     * @return 返回ActorMailBox管理器
     */
    //	 ActorMailBoxes mailBoxes();

    /**
     * Terminates this actor system.
     */
    //	 Future<> terminate();

    /**
     * Returns a Future which will be completed after
     * the ActorSystem has been setTerminated and
     * termination hooks have been executed.
     */
    //  Future<> whenTerminated();

}
