package com.tny.game.common.event;

import org.jmock.*;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.*;

import static org.junit.Assert.*;

public class EventDispatcherTest {

    private Mockery context = new JUnit4Mockery();

    public class TaskEvent extends BaseEvent<Integer> {

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
        final TestListener listener = this.context.mock(TestListener.class);
        this.CREATE_EVENT.addListener(listener);
        this.UPGRADE_EVENT.addListener(listener);

        //        listener::handleUpgrade
        //        UPGRADE_EVENT.add(listener::handleUpgrade);
        //        UPGRADE_EVENT.remove(listener::handleUpgrade);

        this.context.checking(new Expectations() {{
            oneOf(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            oneOf(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, EventDispatcherTest.this.level);
        }});
        this.UPGRADE_EVENT.notify(this.SOURCE, this.level);
        this.context.assertIsSatisfied();

        this.UPGRADE_EVENT.removeListener(listener);

        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, EventDispatcherTest.this.level);
        }});
        this.UPGRADE_EVENT.notify(this.SOURCE, this.level);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.clear();
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        VoidEventDelegate<String> createDelegate = listener::handleCreate;
        this.CREATE_EVENT.add(createDelegate);
        this.context.checking(new Expectations() {{
            oneOf(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.remove(createDelegate);
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.add(createDelegate);
        this.CREATE_EVENT.clear();
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();
        //        dispatcher.dispatch(new TaskEvent("taskEvent", 2));
        //        dispatcher.addListener(listener);
        //        dispatcher.dispatch(new TaskEvent("taskEvent", 1));
        //        dispatcher.clearListener();
        //        dispatcher.dispatch(new TaskEvent("taskEvent", 2));

    }

}
