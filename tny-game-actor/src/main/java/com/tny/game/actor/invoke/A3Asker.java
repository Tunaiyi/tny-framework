package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.Stage;

public interface A3Asker<R, TS extends Stage, A1, A2, A3> extends Asker<A3Asker<R, TS, A1, A2, A3>, TS> {

    TypeAnswer<R> ask(A1 arg1, A2 arg2, A3 arg3);

    Stage asking(A1 arg1, A2 arg2, A3 arg3);

}
