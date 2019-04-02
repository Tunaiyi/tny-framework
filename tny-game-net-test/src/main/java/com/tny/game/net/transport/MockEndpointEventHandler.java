package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

/**
 * <p>
 */
public class MockEndpointEventHandler<E extends NetEndpoint<Long>> implements EndpointEventHandler<Long, E> {

    private int inputTimes = 0;

    private int outputTimes = 0;

    @Override
    public void onInput(EndpointEventsBox<Long> box, E session) {
        this.inputTimes++;
    }

    @Override
    public void onOutput(EndpointEventsBox<Long> box, E endpoint) {
        this.outputTimes++;
    }

    public int getInputTimes() {
        return inputTimes;
    }

    public int getOutputTimes() {
        return outputTimes;
    }

}
