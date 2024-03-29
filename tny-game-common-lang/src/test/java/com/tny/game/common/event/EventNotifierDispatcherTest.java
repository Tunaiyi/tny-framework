/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.common.event;

import org.jmock.*;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class EventNotifierDispatcherTest {

    private Mockery context = new JUnit5Mockery();

    public class TaskEventNotice extends BaseEventNotice<Integer> {

        public TaskEventNotice(String handler, Integer source) {
            super(source);
        }

    }

    private java.lang.String SOURCE = "Tom";

    private int level = 100;

    private VoidBindEvent<TestListener, String> CREATE_EVENT
            = Events.ofEvent(TestListener.class, TestListener::handleCreate);

    private A1BindEvent<TestListener, String, Integer> UPGRADE_EVENT
            = Events.ofEvent(TestListener.class, TestListener::handleUpgrade);

    /**
     * @uml.property name="listener"
     */
    private final TestListener listener = new TestListener() {

        @SuppressWarnings("unused")
        public void handleTaskEvent(TaskEventNotice event) {
            assertEquals(Integer.valueOf(1), event.getSource());
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
            oneOf(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            oneOf(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, EventNotifierDispatcherTest.this.level);
        }});
        this.UPGRADE_EVENT.notify(this.SOURCE, this.level);
        this.context.assertIsSatisfied();

        this.UPGRADE_EVENT.removeListener(listener);

        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, EventNotifierDispatcherTest.this.level);
        }});
        this.UPGRADE_EVENT.notify(this.SOURCE, this.level);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.clear();
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        VoidEventDelegate<String> createDelegate = listener::handleCreate;
        this.CREATE_EVENT.add(createDelegate);
        this.context.checking(new Expectations() {{
            oneOf(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.remove(createDelegate);
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, null);
        }});
        this.CREATE_EVENT.notify(this.SOURCE);
        this.context.assertIsSatisfied();

        this.CREATE_EVENT.add(createDelegate);
        this.CREATE_EVENT.clear();
        this.context.checking(new Expectations() {{
            never(listener).handleCreate(EventNotifierDispatcherTest.this.SOURCE);
            never(listener).handleUpgrade(EventNotifierDispatcherTest.this.SOURCE, null);
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
