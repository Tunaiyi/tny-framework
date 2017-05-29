package com.tny.game.actor.invoke;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.Stage;

public interface A6Teller<A1, A2, A3, A4, A5, A6> extends Teller<A6Teller<A1, A2, A3, A4, A5, A6>> {

    void tell(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);

    VoidAnswer tellOf(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);

    Stage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);

}
