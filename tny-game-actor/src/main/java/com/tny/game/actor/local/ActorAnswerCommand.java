package com.tny.game.actor.local;


import com.tny.game.actor.stage.TaskStage;

/**
 * Actor命令 持有Answer的Actor命令
 * Created by Kun Yang on 16/4/26.
 */
public abstract class ActorAnswerCommand<T, TS extends TaskStage, A extends BaseAnswer<T, TS>> extends ActorCommand<T, TS, A> {

    private A answer;

    protected ActorAnswerCommand(ActorCell actorCell, A answer) {
        this(actorCell, answer, null);
    }

    protected ActorAnswerCommand(ActorCell actorCell, A answer, TS stage) {
        super(actorCell);
        this.answer = answer;
        if (stage != null)
            this.setStage(stage);
    }

    @Override
    protected void setStage(TS stage) {
        super.setStage(stage);
        if (this.answer != null)
            this.answer.setStage(stage);
    }

    @Override
    public A getAnswer() {
        return answer;
    }

    @Override
    protected void postCancel() {
        if (this.answer != null)
            answer.cancel(true);
    }

    @Override
    protected void postFail(Throwable cause) {
        if (this.answer != null)
            answer.fail(cause);
    }

    @Override
    protected void postSuccess(T result) {
        if (this.answer != null)
            answer.success(result);
    }

}
