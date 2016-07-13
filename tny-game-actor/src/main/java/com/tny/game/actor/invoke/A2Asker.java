package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A2Asker<R, TS extends TaskStage, A1, A2> extends Asker<A2Asker, TS> {

    TypeAnswer<R> ask(A1 arg1, A2 arg2);

    TaskStage asking(A1 arg1, A2 arg2);

}
