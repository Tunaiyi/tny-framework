package com.tny.game.net.command.dispatcher;

/**
 * @author KGTny
 * @ClassName: ControllerInfo
 * @Description:控制器信息
 * @date 2011-10-26 下午4:22:47
 * <p>
 * 控制器信息
 * <p>
 * <br>
 */
public class ControllerContext {

    //	private static final String SYSTEM_THREAD_CONTROLLER = "SysThreadController";
    //	private static final String SYSTEM_THREAD_HANDLER = "SysThreadHandler";
    private static final ThreadLocal<ControllerContext> CONTEXT = new ThreadLocal<>();

    private int protocol;

    private Thread thread;

    private ControllerContext() {
    }

    private ControllerContext(int protocol, Thread thread) {
        this.protocol = protocol;
        this.thread = thread;
    }

    /**
     * 获取当前线程正在执行的控制信息 <br>
     *
     * @return
     */
    public static ControllerContext getCurrent() {
        ControllerContext info = CONTEXT.get();
        if (info == null) {
            info = new ControllerContext();
            info.protocol = 0;
            CONTEXT.set(info);
        }
        return info;
    }

    static void setCurrent(int protocol) {
        ControllerContext info = CONTEXT.get();
        if (info == null) {
            info = new ControllerContext();
            CONTEXT.set(info);
        }
        info.protocol = protocol;
    }

    public Thread getThread() {
        return this.thread;
    }

    /**
     * 业务方法名称
     * <p>
     *
     * @return
     */
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public String toString() {
        return "ControllerInfo [getProtocol()=" + this.getProtocol() + "]";
    }

}
