package com.tny.game.common.scheduler.cycle;

import java.time.Instant;

/**
 * Created by Kun Yang on 16/2/20.
 */
public interface TimeCycle {

    Instant getTimeAfter(Instant instant);

}
