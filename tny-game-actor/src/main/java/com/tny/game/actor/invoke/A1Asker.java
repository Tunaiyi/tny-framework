package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A1Asker<R, TS extends TaskStage, A1> extends Asker<A1Asker, TS> {

    TypeAnswer<R> ask(A1 arg1);

    TaskStage asking(A1 arg1);

}
