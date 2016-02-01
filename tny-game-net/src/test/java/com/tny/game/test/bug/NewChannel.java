package com.tny.game.test.bug;

import java.io.IOException;
import java.nio.channels.Channel;

public class NewChannel implements Channel {

    @Override
    public boolean isOpen() {

        return false;
    }

    @Override
    public void close() throws IOException {


    }

}
