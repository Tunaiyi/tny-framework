package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A2Teller<A1, A2> extends Teller<A2Teller<A1, A2>> {

    void tell(A1 arg1, A2 arg2);

    VoidAnswer tellOf(A1 arg1, A2 arg2);

    TaskStage telling(A1 arg1, A2 arg2);

}
