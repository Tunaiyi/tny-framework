package com.tny.game.common.worker.command;


public abstract class DelayCommand implements Command {

    private volatile long executeTime;

    protected final String name;

    protected boolean executed = false;

    public DelayCommand() {
        this(null, System.currentTimeMillis());
    }

    public DelayCommand(String name) {
        this(name, System.currentTimeMillis());
    }

    public DelayCommand(String name, int delay) {
        this(name, System.currentTimeMillis() + delay);
    }

    public DelayCommand(String name, long executeTime) {
        this.name = name;
        this.executeTime = executeTime;
    }

    @Override
    public void execute() {
        if (isCanExecute())
            return;
        try {
            this.action();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            executed = true;
        }
    }

    protected abstract void action();

    @Override
    public String getName() {
        if (this.name == null)
            return Command.super.getName();
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

    protected long getDelay() {
        long delay = executeTime - System.currentTimeMillis();
        return delay < 0 ? 0L : delay;
    }

    @Override
    public boolean isDone() {
        return this.executed && !this.isWork();
    }

    public boolean isCanExecute() {
        return isDone() && getDelay() <= 0;
    }

}