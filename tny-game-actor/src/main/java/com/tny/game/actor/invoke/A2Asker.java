package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.Stage;

public interface A2Asker<R, TS extends Stage, A1, A2> extends Asker<A2Asker<R, TS, A1, A2>, TS> {

    TypeAnswer<R> ask(A1 arg1, A2 arg2);

    Stage asking(A1 arg1, A2 arg2);

}
