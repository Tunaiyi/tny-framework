package com.tny.game.net.dispatcher;

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
public class CurrentCMD {

    //	private static final String SYSTEM_THREAD_CONTROLLER = "SysThreadController";
    //	private static final String SYSTEM_THREAD_HANDLER = "SysThreadHandler";

    private static ThreadLocal<CurrentCMD> local = new ThreadLocal<CurrentCMD>();

    private long userID;

    private int protocol;

    private Thread thread;

    private CurrentCMD() {
    }

    private CurrentCMD(long userID, int protocol, Thread thread) {
        this.userID = userID;
        this.protocol = protocol;
        this.thread = thread;
    }

    /**
     * 获取当前线程正在执行的控制信息 <br>
     *
     * @return
     */
    public static CurrentCMD getCurrent() {
        CurrentCMD info = local.get();
        if (info == null) {
            info = new CurrentCMD();
            info.protocol = 0;
            info.userID = -1;
            local.set(info);
        }
        return info;
    }

    protected static void setCurrent(long userID, int protocol) {
        CurrentCMD info = local.get();
        if (info == null) {
            info = new CurrentCMD();
            local.set(info);
        }
        info.userID = userID;
        info.protocol = protocol;
    }

    /**
     * 获取当前线程请求对象 <br>
     *
     * @return 当前线程请求对象
     */
    public long getUserID() {
        return this.userID;
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
