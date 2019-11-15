package com.tny.game.actor.stage.invok;


import com.tny.game.actor.stage.*;

/**
 * Created by Kun Yang on 16/5/5.
 */
public interface SupplyStageable<TS extends Stage> {

    Stageable<TS> stageable();

}
