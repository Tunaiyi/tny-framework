package com.tny.game.common.worker.command;

public abstract class BaseCommand implements Command {

    protected final String name;

    private boolean done = false;

    public BaseCommand() {
        this(null);
    }

    public BaseCommand(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
        try {
            this.action();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            done = true;
        }
    }

    protected abstract void action();

    @Override
    public String getName() {
        if (this.name == null) {
            return Command.super.getName();
        }
        return name;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

}
