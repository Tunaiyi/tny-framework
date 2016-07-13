package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TaskStage;

public interface A0Asker<R, TS extends TaskStage> extends Asker<A0Asker, TS> {

    TypeAnswer<R> ask();

    TaskStage asking();



}
