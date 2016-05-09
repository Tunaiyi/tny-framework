package com.tny.game.actor.local;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorPath;
import com.tny.game.actor.Answer;
import com.tny.game.actor.exception.ActorTerminatedException;
import com.tny.game.worker.CommandBox;
import com.tny.game.worker.CommandWorker;

import java.util.Queue;

/**
 * Actor单元,负责管理与当前Actor相关的对象
 * Created by Kun Yang on 16/4/25.
 */
class ActorCell extends ActorCommandBox<ActorCell> implements ActorDispatcher {

    private static final ActorLifeCycle DEFAULT_LIFE_CYCLE = new ActorLifeCycle() {
    };

    private ActorPath actorPath;

    private Actor<?, ?> actor;

    private ActorHandler<?> handler = (mail) -> null;

    private ActorLifeCycle lifeCycle = DEFAULT_LIFE_CYCLE;

    private volatile boolean terminated = false;

    ActorCell(Object id, ActorPath path, ActorProps props) {
        this.actorPath = path;
        this.handler = props.getActorHandler();
        this.handler = this.handler != null ? handler : (mail) -> null;
        this.lifeCycle = props.getLifeCycle();
        this.lifeCycle = lifeCycle != null ? new ProxyActorLifeCycle(lifeCycle) : DEFAULT_LIFE_CYCLE;
        this.actor = new LocalTypeActor<>(id, this);
    }

    @SuppressWarnings("unchecked")
    public <ACT extends Actor> ACT getActor() {
        return (ACT) actor;
    }

    void terminate() {
        this.tell(ActorTerminateMessage.message(), this.actor);
    }

    boolean detach() {
        return this.worker != null && this.worker.unregister(this);
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
        this.checkTerminated();
        if (!needAnswer) {
            if (message instanceof ActorCommand)
                this.accept((ActorCommand<?, ?, ?>) message);
            else
                this.accept(new ActorMailCommand<>(this, message, sender));
            return null;
        } else {
            ActorAnswerCommand<V, ?, ?> command = null;
            if (message instanceof ActorAnswerCommand)
                this.accept(command = (ActorAnswerCommand) message);
            else
                this.accept(new ActorMailCommand<>(this, message, sender, new LocalTypeAnswer<>()));
            return command != null ? (A) command.getAnswer() : null;
        }

    }

    private void checkTerminated() {
        if (this.isTerminated())
            throw new ActorTerminatedException(actor);
    }

    boolean isTerminated() {
        return this.terminated;
    }

    private void doTerminate() {
        if (this.terminated)
            return;
        this.terminated = true;
        Queue<ActorCommand<?, ?, ?>> queue = this.acceptQueue();
        while (!queue.isEmpty()) {
            ActorCommand<?, ?, ?> cmd = queue.poll();
            if (!cmd.isWork())
                continue;
            cmd.cancel();
            this.executeCommand(cmd);
        }
        for (ActorCell cell : boxes())
            cell.terminate();
    }

    public ActorPath getActorPath() {
        return actorPath;
    }

    boolean isTakenOver() {
        return this.isWorking();
    }

    @Override
    public boolean accept(ActorCommand<?, ?, ?> command) {
        this.checkTerminated();
        return super.accept(command);
    }

    @Override
    public boolean bindWorker(CommandWorker worker) {
        return !this.isTerminated() && super.bindWorker(worker);
    }

    @Override
    public boolean unbindWorker() {
        return super.unbindWorker();
    }

    @Override
    public boolean register(CommandBox commandBox) {
        return !this.terminated && super.register(commandBox);
    }

}
