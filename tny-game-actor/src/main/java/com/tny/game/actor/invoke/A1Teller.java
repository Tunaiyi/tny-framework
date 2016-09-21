package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A1Teller<A1> extends Teller<A1Teller<A1>> {

    void tell(A1 arg1);

    VoidAnswer tellOf(A1 arg1);

    TaskStage telling(A1 arg1);

}
