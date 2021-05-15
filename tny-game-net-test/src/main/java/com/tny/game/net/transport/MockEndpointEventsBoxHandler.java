package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.event.*;

/**
 * <p>
 */
public class MockEndpointEventsBoxHandler<E extends NetEndpoint<Long>> implements EndpointEventsBoxHandler<Long, E> {

    private int inputTimes = 0;

    private int outputTimes = 0;

    @Override
    public void onInput(EndpointEventsBox<Long> box, E session) {
        this.inputTimes++;
    }

    @Override
    public void onInputEvent(E endpoint, EndpointInputEvent<Long> event) {

    }

    @Override
    public void onOutput(EndpointEventsBox<Long> box, E endpoint) {
        this.outputTimes++;
    }

    @Override
    public void onOutputEvent(E endpoint, EndpointOutputEvent<Long> event) {

    }

    public int getInputTimes() {
        return this.inputTimes;
    }

    public int getOutputTimes() {
        return this.outputTimes;
    }

}
