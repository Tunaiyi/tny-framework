package drama.actor;

import java.util.Optional;

/**
 * Actor对象,负责处理消息.
 *
 * @author KGTny
 */
public interface Actor<M> {

    void aroundReceive(M message);

    void preStart();

    void postStart();

    void aroundPreRestart(Throwable cause, Optional<Object> optionalMessage);

    void aroundPostRestart(Throwable cause);

    void preResume(Throwable reason, M message);

    void postResume(Throwable reason);

    void aroundPostStop();

    void preStop();

    void proStop();

    void receiver(M receiver);

    ActorContext getContext();

    boolean resetActor(ActorContext context, ActorRef replace);

    //    SupervisorStrategy getSupervisorStrategy();

}