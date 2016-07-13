package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A3Asker<R, TS extends TaskStage, A1, A2, A3> extends Asker<A3Asker, TS> {

    TypeAnswer<R> ask(A1 arg1, A2 arg2, A3 arg3);

    TaskStage asking(A1 arg1, A2 arg2, A3 arg3);

}
