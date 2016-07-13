package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A0Teller extends Teller<A0Teller> {

    void tell();

    VoidAnswer tellOf();

    TaskStage telling();

}