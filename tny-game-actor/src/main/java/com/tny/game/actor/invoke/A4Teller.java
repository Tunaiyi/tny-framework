package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A4Teller<A1, A2, A3, A4> extends Teller<A4Teller> {

    void tell(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

    VoidAnswer tellOf(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

    TaskStage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

}
