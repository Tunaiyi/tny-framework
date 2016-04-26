package com.tny.game.worker.command;

public abstract class LoopCommand extends DelayCommand {

    public static final long STOP_LOOP = -1;

    public LoopCommand(String name) {
        this(name, System.currentTimeMillis());
    }

    public LoopCommand(String name, int delay) {
        this(name, System.currentTimeMillis() + delay);
    }

    public LoopCommand(String name, long executeTime) {
        super(name, executeTime);
    }

    @Override
    public void action() {
        try {
            run();
            long delay = nextInterval();
            if (delay <= STOP_LOOP) {
                executed = true;
            } else if (isWork()) {
                delay(delay);
                executed = false;
            }
        } catch (Exception e) {
            if (isWork()) {
                long delay = nextInterval();
                if (delay <= STOP_LOOP) {
                    executed = true;
                } else if (isWork()) {
                    delay(delay);
                    executed = false;
                }
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行任务
     */
    protected abstract void run();

    /**
     * 返回下一次间隔时间
     * 若
     * 返回 STOP_LOOP = -1 终端任务
     * 返回 n 表示 n 毫秒后会再次执行任务;
     *
     * @return
     */
    protected abstract long nextInterval();

}
