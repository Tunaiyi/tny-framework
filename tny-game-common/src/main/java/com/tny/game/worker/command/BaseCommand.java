package com.tny.game.worker.command;


public abstract class BaseCommand<O> implements Command<O> {

    private volatile long executeTime;

    protected final String name;

    protected boolean executed = false;

    public BaseCommand(String name) {
        this(name, System.currentTimeMillis());
    }

    public BaseCommand(String name, int delay) {
        this(name, System.currentTimeMillis() + delay);
    }

    public BaseCommand(String name, long executeTime) {
        this.name = name;
        this.executeTime = executeTime;
    }

    @Override
    public O execute() {
        try {
            return this.action();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            executed = true;
        }
    }

    protected abstract O action();

    @Override
    public String getName() {
        return name;
    }

    protected void immediately() {
        this.executeTime = System.currentTimeMillis();
    }

    protected void delay(long delay) {
        this.executeTime = System.currentTimeMillis() + delay;
    }

    protected void runAt(long time) {
        this.executeTime = time;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    //	@Override
    protected long getDelay() {
        long delay = executeTime - System.currentTimeMillis();
        return delay < 0 ? 0L : delay;
    }

    @Override
    public boolean isCompleted() {
        return this.executed && !this.isWorking();
    }

    @Override
    public boolean isCanExecute() {
        return getDelay() <= 0;
    }

}
