package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;

public interface A3Teller<A1, A2, A3> extends Teller<A3Teller> {

    void tell(A1 arg1, A2 arg2, A3 arg3);

    VoidAnswer tellOf(A1 arg1, A2 arg2, A3 arg3);

}