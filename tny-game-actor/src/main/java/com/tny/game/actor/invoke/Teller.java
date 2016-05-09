package com.tny.game.actor.invoke;


import com.tny.game.actor.stage.VoidTaskStage;

import java.util.function.Consumer;

public interface Teller<TE> {

    TE stage(Consumer<VoidTaskStage> stage);

}
