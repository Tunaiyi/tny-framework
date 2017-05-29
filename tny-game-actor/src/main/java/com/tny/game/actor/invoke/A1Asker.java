package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.Stage;

public interface A1Asker<R, TS extends Stage, A1> extends Asker<A1Asker<R, TS, A1>, TS> {

    TypeAnswer<R> ask(A1 arg1);

    Stage asking(A1 arg1);

}
