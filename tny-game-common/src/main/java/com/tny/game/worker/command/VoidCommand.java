package com.tny.game.worker.command;

/**
 * 无返回的Command
 *
 * @author KGTny
 */
public abstract class VoidCommand extends BaseCommand<Object> {

    public VoidCommand(String name) {
        super(name);
    }

    public VoidCommand(String name, int delay) {
        super(name, delay);
    }

    public VoidCommand(String name, long executeTime) {
        super(name, executeTime);
    }

    @Override
    public Object action() {
        run();
        return null;
    }

    protected abstract void run();

}
