package com.tny.game.actor.stage.invok;


import com.tny.game.actor.stage.Stageable;
import com.tny.game.actor.stage.TaskStage;

/**
 * Created by Kun Yang on 16/5/5.
 */
public interface SupplyStageable<TS extends TaskStage> {

    Stageable<TS> stageable();

}
