package com.tny.game.worker;


public abstract class AbstractWorldWorker implements WorldWorker {

    protected void runCommandBox(CommandBox commandBox) {
        commandBox.run();
    }

}
