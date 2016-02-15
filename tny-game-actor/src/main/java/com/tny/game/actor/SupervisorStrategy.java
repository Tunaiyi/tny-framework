package com.tny.game.actor;

import com.tny.game.actor.event.Error;
import com.tny.game.actor.event.LogEvent;
import com.tny.game.actor.event.Warning;
import com.tny.game.actor.exception.ActorInitializationException;
import com.tny.game.actor.local.ChildNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * 监控子Actor行为策略
 * Created by Kun Yang on 16/1/17.
 */
public interface SupervisorStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(SupervisorStrategy.class);

    Decider decider();

    /**
     * 处理子节点终止策略
     *
     * @param context  子节点上下文
     * @param child    子节点Actor
     * @param children 与终止节点同级的所有子Actor的Stream
     */
    void handleChildTerminated(ActorContext context, ActorRef child, Stream<ActorRef> children);

    /**
     * 进行终止或重启错误子节点的处理
     *
     * @param context            子节点上下文
     * @param restart            是否重启
     * @param child              子节点Actor
     * @param cause              错误原因
     * @param childNode          子节点
     * @param childrenNodeStream 与终止节点同级的所有子Actor的ChildNode Stream
     */
    void processFailure(ActorContext context, boolean restart, ActorRef child, Throwable cause, ChildNode childNode, Stream<ChildNode> childrenNodeStream);

    /**
     * 恢复错误子节点
     *
     * @param child 子节点
     * @param cause 错误原因
     */
    default void resumeChild(ActorRef child, Throwable cause) {
        if (child instanceof InternalActorRef)
            ((InternalActorRef) child).resume(cause);
    }

    /**
     * 重启错误子节点
     *
     * @param child        错误子节点
     * @param cause        原因
     * @param suspendFirst 是否先挂起再重启
     */
    default void restartChild(ActorRef child, Throwable cause, boolean suspendFirst) {
        if (child instanceof InternalActorRef) {
            InternalActorRef actor = (InternalActorRef) child;
            if (suspendFirst)
                actor.suspend();
            actor.restart(cause);
        }
    }


    /**
     * 处理子节点错误策略
     *
     * @param context            子节点上下文
     * @param child              子节点Actor
     * @param cause              错误原因
     * @param childNode          子节点
     * @param childrenNodeStream 与终止节点同级的所有子Actor的ChildNode Stream
     */
    default boolean handleFailure(ActorContext context, ActorRef child, Throwable cause, ChildNode childNode, Stream<ChildNode> childrenNodeStream) {
        Directive directive = decider().decide(cause, Directive.ESCALATE);
        logFailure(context, child, cause, directive);
        switch (directive) {
            case RESUME:
                this.resumeChild(child, cause);
                return true;
            case RESTART:
                this.processFailure(context, true, child, cause, childNode, childrenNodeStream);
                return true;
            case STOP:
                this.processFailure(context, false, child, cause, childNode, childrenNodeStream);
                return false;
            case ESCALATE:
                return false;
        }
        return false;
    }

    default void logFailure(ActorContext context, ActorRef child, Throwable cause, Directive decision) {
        String logMessage;
        if (cause instanceof ActorInitializationException && cause.getCause() != null) {
            logMessage = cause.getCause().getMessage();
        } else {
            logMessage = cause.getMessage();
        }
        switch (decision) {
            case RESUME:
                publish(context, Warning.warning(child.getPath(), this.getClass(), logMessage));
                break;
            case ESCALATE:
                break;
            default:
                publish(context, Error.error(cause, child.getPath(), this.getClass(), logMessage));
        }
    }

    default void publish(ActorContext context, LogEvent logEvent) {
        try {
            context.getSystem().eventStream().publish(logEvent);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }


}
