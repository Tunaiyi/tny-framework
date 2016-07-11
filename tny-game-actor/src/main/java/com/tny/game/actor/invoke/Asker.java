package com.tny.game.actor.invoke;


import com.tny.game.actor.stage.TaskStage;

import java.util.function.Consumer;

public interface Asker<AK extends Asker, TS extends TaskStage> {

    AK then(Consumer<TS> stage);

}
