package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;

public interface A5Teller<A1, A2, A3, A4, A5> extends Teller<A5Teller> {

    void tell(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

    VoidAnswer tellOf(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

}
