package com.tny.game.common.scheduler.cycle;


import org.joda.time.DateTime;

/**
 * Created by Kun Yang on 16/2/20.
 */
public interface TimeCycle {

    DateTime getTimeAfter(DateTime dateTime);

}
