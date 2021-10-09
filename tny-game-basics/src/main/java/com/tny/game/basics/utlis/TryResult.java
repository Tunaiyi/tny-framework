package com.tny.game.basics.utlis;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/7/29.
 */
public interface TryResult<M> extends DoneResult<M> {

    boolean isFailedToTry();

    TryToDoResult getResult();

    void ifFailedToTry(Consumer<DefaultTryResult<M>> consumer);

    void ifSucceedToTry(Consumer<DefaultTryResult<M>> consumer);

}
