package com.tny.game.common.scheduler;

import java.util.*;

/**
 * Created by Kun Yang on 2017/6/13.
 */
public class TriggerContext {

    private Collection<TimeTaskEvent> events;

    public TriggerContext(Collection<TimeTaskEvent> events) {
        this.events = Collections.unmodifiableCollection(events);
    }

    public int countHandler(Class<? extends TimeTaskHandler> handleClass) {
        return this.events.stream()
                          .mapToInt(e -> {
                              int times = 0;
                              for (TimeTaskHandler h : e.getHandlerList()) {
                                  if (handleClass.isInstance(h))
                                      times++;
                              }
                              return times;
                          })
                          .sum();
    }

    public int countHandler(TimeTaskHandler handler) {
        return this.events.stream()
                          .mapToInt(e -> {
                              int times = 0;
                              for (TimeTaskHandler h : e.getHandlerList()) {
                                  if (h.equals(handler))
                                      times++;
                              }
                              return times;
                          })
                          .sum();
    }

}