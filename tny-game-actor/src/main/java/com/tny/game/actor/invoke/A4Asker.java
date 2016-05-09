package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A4Asker<R, TS extends TaskStage, A1, A2, A3, A4> extends Asker<A4Asker, TS> {

    TypeAnswer<R> ask(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

}
