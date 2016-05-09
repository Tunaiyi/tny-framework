package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;

public interface A1Teller<A1> extends Teller<A1Teller> {

    void tell(A1 arg1);

    VoidAnswer tellOf(A1 arg1);

}
