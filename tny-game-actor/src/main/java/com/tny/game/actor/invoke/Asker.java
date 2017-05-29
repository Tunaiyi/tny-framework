package com.tny.game.actor.invoke;


import com.tny.game.actor.stage.Stage;

import java.util.function.Consumer;

public interface Asker<AK extends Asker, TS extends Stage> {

    AK then(Consumer<TS> stage);

}
