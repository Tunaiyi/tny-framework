package com.tny.game.actor.local;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorURL;
import com.tny.game.actor.Answer;

/**
 * Actor单元,负责管理与当前Actor相关的对象
 * Created by Kun Yang on 16/4/25.
 */
class ActorCell implements ActorDispatcher {

    private ActorURL actorPath;

    private Actor<?, ?> actor;

    private ActorHandler<?> handler = (mail) -> null;

    private ActorCommandBox commandBox;

    private ActorLifeCycle lifeCycle;

    private int stepSize;

    ActorCell(Object id, ActorURL path, ActorProps props) {
        this.actorPath = path;
        this.handler = props.getActorHandler();
        this.handler = this.handler != null ? handler : (mail) -> null;
        this.lifeCycle = props.getLifeCycle();
        this.actor = new DefaultLocalActor<>(id, this);
        this.commandBox = props.getCommandBoxFactory().create(this);
        this.stepSize = props.getStepSize();
    }

    @Override
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
    public void execute(ActorCommand<?> command) {
        if (command.isDone())
            return;
        try {
            this.lifeCycle.preHandle(command);
            command.handle();
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
    public <V> Answer<V> sendMessage(Object message, Actor sender, boolean needAnswer) {
        Answer answer = new ActorAnswer<>();
        if (!needAnswer) {
            if (message instanceof ActorCommand)
                this.commandBox.accept((ActorCommand<?>) message);
            else
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender));
            return null;
        } else {
            ActorCommand<V> command = null;
            if (message instanceof ActorCommand) {
                command = (ActorCommand<V>) message;
                if (command.getAnswer() == null)
                    command.setAnswer(new ActorAnswer<>());
                this.commandBox.accept(command);
            } else
                this.commandBox.accept(new ActorMailCommand<>(this, message, sender, new ActorAnswer<>()));
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
        return actorPath;
    }

    boolean isTakenOver() {
        return this.commandBox.isWorking();
    }

    ActorCommandBox getCommandBox() {
        return commandBox;
    }

    public int getStepSize() {
        return stepSize;
    }
}
