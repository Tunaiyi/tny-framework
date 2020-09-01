package com.tny.game.actor.local;

import com.tny.game.actor.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Actor单元,负责管理与当前Actor相关的对象
 * Created by Kun Yang on 16/4/25.
 */
class ActorCell implements ActorDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorCell.class);

    private final ActorURL actorPath;

    private final Actor<?, ?> actor;

    private ActorHandler<?> handler;

    private final ActorCommandBox commandBox;

    private final ActorLifeCycle lifeCycle;

    private final int stepSize;

    ActorCell(Object id, ActorURL path, ActorProps props) {
        this.actorPath = path;
        this.handler = props.getActorHandler();
        this.handler = this.handler != null ? this.handler : (mail) -> null;
        this.lifeCycle = props.getLifeCycle();
        this.actor = new DefaultLocalActor<>(id, this);
        this.commandBox = props.getCommandBoxFactory().create(this);
        this.stepSize = props.getStepSize();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ACT extends Actor<?, ?>> ACT getActor() {
        return (ACT)this.actor;
    }

    void terminate() {
        this.tell(ActorTerminateMessage.message(), this.actor);
    }

    boolean detach() {
        return this.commandBox.detach();
    }

    /**
     * 处理消息
     *
     * @param mail 消息
     * @return 返回处理结果
     */
    public Object handle(ActorMessage mail) {
        if (mail == ActorTerminateMessage.message()) {
            this.doTerminate();
            return null;
        } else if (this.handler != null) {
            return this.handler.handler(as(mail));
        }
        return null;
    }

    /**
     * 执行指定command
     *
     * @param command 执行
     */
    public void execute(ActorCommand<?> command) {
        if (command.isDone()) {
            return;
        }
        try {
            this.lifeCycle.preHandle(command);
            command.handle();
            if (command.isDone()) {
                Object result = command.getResult();
                this.lifeCycle.postSucc(result);
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
            this.lifeCycle.postFail(e);
        } finally {
            this.lifeCycle.postHandle(command);
        }
    }

    public ActorCell actorCell() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Answer<V> sendMessage(Object message, Actor<?, ?> sender, boolean needAnswer) {
        if (!needAnswer) {
            if (message instanceof ActorCommand) {
                this.commandBox.accept((ActorCommand<?>)message);
            } else {
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender));
            }
            return null;
        } else {
            ActorCommand<V> command = null;
            if (message instanceof ActorCommand) {
                command = (ActorCommand<V>)message;
                if (command.getAnswer() == null) {
                    command.setAnswer(new ActorAnswer<>());
                }
                this.commandBox.accept(command);
            } else {
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender, new ActorAnswer<>()));
            }
            return command != null ? command.getAnswer() : null;
        }

    }

    boolean isTerminated() {
        return this.commandBox.isTerminated();
    }

    private void doTerminate() {
        this.commandBox.terminate();
    }

    public ActorURL getActorPath() {
        return this.actorPath;
    }

    boolean isTakenOver() {
        return this.commandBox.isWorking();
    }

    ActorCommandBox getCommandBox() {
        return this.commandBox;
    }

    public int getStepSize() {
        return this.stepSize;
    }

}
