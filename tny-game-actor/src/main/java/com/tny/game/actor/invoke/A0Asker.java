package com.tny.game.actor.invoke;


import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.Stage;

public interface A0Asker<R, TS extends Stage> extends Asker<A0Asker<R, TS>, TS> {

    TypeAnswer<R> ask();

    Stage asking();


}
