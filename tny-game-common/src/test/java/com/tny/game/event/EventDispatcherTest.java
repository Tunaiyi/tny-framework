package com.tny.game.event;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventDispatcherTest {

    Mockery context = new JUnit4Mockery();

    public class TaskEvent extends Event<Integer> {

        public TaskEvent(String handler, Integer source) {
            super(source);
        }

    }

    private java.lang.String SOURCE = "Tom";

    private int level = 100;


    BindVoidEventBus<TestListener, String> CREATE_EVENT
            = EventBuses.of(TestListener.class, TestListener::handleCreate);

    BindP1EventBus<TestListener, String, Integer> UPGRADE_EVENT
            = EventBuses.of(TestListener.class, TestListener::handleUpgrade);

    /**
     * @uml.property name="listener"
     */
    private TestListener listener = new TestListener() {

        @SuppressWarnings("unused")
        public void handleTaskEvent(TaskEvent event) {
            assertEquals(new Integer(1), event.getSource());
        }

    };

    @Test
    public void testAddListener() {
        final TestListener listener = context.mock(TestListener.class);
        CREATE_EVENT.addListener(listener);
        UPGRADE_EVENT.addListener(listener);

//        listener::handleUpgrade
//        UPGRADE_EVENT.add(listener::handleUpgrade);
//        UPGRADE_EVENT.remove(listener::handleUpgrade);

        context.checking(new Expectations() {{
            oneOf(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, null);
        }});
        CREATE_EVENT.notify(SOURCE);
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            never(listener).handleCreate(SOURCE);
            oneOf(listener).handleUpgrade(SOURCE, level);
        }});
        UPGRADE_EVENT.notify(SOURCE, level);
        context.assertIsSatisfied();

        UPGRADE_EVENT.removeListener(listener);

        context.checking(new Expectations() {{
            never(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, level);
        }});
        UPGRADE_EVENT.notify(SOURCE, level);
        context.assertIsSatisfied();

        CREATE_EVENT.clear();
        context.checking(new Expectations() {{
            never(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, null);
        }});
        CREATE_EVENT.notify(SOURCE);
        context.assertIsSatisfied();

        VoidEventDelegate<String> createDelegate = listener::handleCreate;
        CREATE_EVENT.add(createDelegate);
        context.checking(new Expectations() {{
            oneOf(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, null);
        }});
        CREATE_EVENT.notify(SOURCE);
        context.assertIsSatisfied();

        CREATE_EVENT.remove(createDelegate);
        context.checking(new Expectations() {{
            never(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, null);
        }});
        CREATE_EVENT.notify(SOURCE);
        context.assertIsSatisfied();

        CREATE_EVENT.add(createDelegate);
        CREATE_EVENT.clear();
        context.checking(new Expectations() {{
            never(listener).handleCreate(SOURCE);
            never(listener).handleUpgrade(SOURCE, null);
        }});
        CREATE_EVENT.notify(SOURCE);
        context.assertIsSatisfied();
//        dispatcher.dispatch(new TaskEvent("taskEvent", 2));
//        dispatcher.addListener(listener);
//        dispatcher.dispatch(new TaskEvent("taskEvent", 1));
//        dispatcher.clearListener();
//        dispatcher.dispatch(new TaskEvent("taskEvent", 2));

    }

}
