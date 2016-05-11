package com.tny.game.actor.local;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorPath;
import com.tny.game.actor.Answer;

/**
 * Actor单元,负责管理与当前Actor相关的对象
 * Created by Kun Yang on 16/4/25.
 */
class ActorCell implements ActorDispatcher {

    private ActorPath actorPath;

    private Actor<?, ?> actor;

    private ActorHandler<?> handler = (mail) -> null;

    private ActorCommandBox commandBox;

    private ActorLifeCycle lifeCycle;

    private volatile boolean terminated = false;

    ActorCell(Object id, ActorPath path, ActorProps props) {
        this.actorPath = path;
        this.handler = props.getActorHandler();
        this.handler = this.handler != null ? handler : (mail) -> null;
        this.lifeCycle = props.getLifeCycle();
        this.actor = new LocalTypeActor<>(id, this);
        this.commandBox = props.getCommandBoxFactory().create(this);
    }

    @SuppressWarnings("unchecked")
    public <ACT extends Actor> ACT getActor() {
        return (ACT) actor;
    }

    void terminate() {
        this.tell(ActorTerminateMessage.message(), this.actor);
    }

    boolean detach() {
        return commandBox.detach();
    }

    /**
     * 处理消息
     *
     * @param mail 消息
     * @return 返回处理结果
     */
    @SuppressWarnings("unchecked")
    public Object handle(ActorMessage mail) {
        if (mail == ActorTerminateMessage.message()) {
            this.doTerminate();
            return null;
        } else if (handler != null) {
            return handler.handler((ActorMail) mail);
        }
        return null;
    }

    /**
     * 执行指定command
     *
     * @param command 执行
     */
    public void execute(ActorCommand<?, ?, ?> command) {
        if (command.isDone())
            return;
        try {
            this.lifeCycle.preHandle(command);
            command.doExecute();
            if (command.isDone()) {
                Object result = command.getResult();
                this.lifeCycle.postSucc(result);
            }
        } catch (Throwable e) {
            e.printStackTrace();
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
    public <V, A extends Answer<V>> A sendMessage(Object message, Actor sender, boolean needAnswer) {
        if (!needAnswer) {
            if (message instanceof ActorCommand)
                this.commandBox.accept((ActorCommand<?, ?, ?>) message);
            else
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender));
            return null;
        } else {
            ActorAnswerCommand<V, ?, ?> command = null;
            if (message instanceof ActorAnswerCommand)
                this.commandBox.accept(command = (ActorAnswerCommand) message);
            else
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender, new LocalTypeAnswer<>()));
            return command != null ? (A) command.getAnswer() : null;
        }

    }

    boolean isTerminated() {
        return this.terminated;
    }

    private void doTerminate() {
        this.commandBox.terminate();
    }

    public ActorPath getActorPath() {
        return actorPath;
    }

    boolean isTakenOver() {
        return this.commandBox.isWorking();
    }

    ActorCommandBox getCommandBox() {
        return commandBox;
    }

}
