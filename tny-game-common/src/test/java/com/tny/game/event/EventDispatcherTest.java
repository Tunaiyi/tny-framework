package com.tny.game.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventDispatcherTest {

    public class TaskEvent extends Event<Integer> {

        public TaskEvent(String handler, Integer source) {
            super(handler, source);
        }

    }

    /**
     * @uml.property name="listener"
     */
    private Object listener = new Object() {

        @SuppressWarnings("unused")
        public void handleTaskEvent(TaskEvent event) {
            assertEquals(new Integer(1), event.getSource());
        }

    };

    @Test
    public void testAddListener() {
        EventDispatcher dispatcher = EventDispatcher.getDispatcher();
        dispatcher.addListener(listener);
        dispatcher.dispatch(new TaskEvent("taskEvent", 1));
        dispatcher.removeListener(listener);
        dispatcher.dispatch(new TaskEvent("taskEvent", 2));
        dispatcher.addListener(listener);
        dispatcher.dispatch(new TaskEvent("taskEvent", 1));
        dispatcher.clearListener();
        dispatcher.dispatch(new TaskEvent("taskEvent", 2));
    }

}
