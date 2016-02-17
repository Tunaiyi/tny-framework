package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.common.result.ResultCodeType;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.listener.DispatchExceptionEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestErrorEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestListener;
import com.tny.game.worker.command.Command;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象请求派发器
 *
 * @author KGTny
 * @ClassName: AbstractControllerDispatcher
 * @Description:
 * @date 2011-9-13 下午1:38:26
 * <p>
 * 抽象请求派发器
 * <p>
 * <p>
 * 实现对controllerMap的初始化,派发请求流程<br>
 */
public abstract class NetMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    /**
     * Controller Map
     */
    protected final Map<Integer, MethodHolder> methodHolder = new CopyOnWriteMap<>();

    protected boolean checkTimeOut = true;

    /**
     * 派发错误监听器
     */
    protected final List<DispatcherRequestListener> listeners = new CopyOnWriteArrayList<DispatcherRequestListener>();

    public NetMessageDispatcher(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

    @Override
    public DispatcherCommand<CommandResult> dispatch(Request request, ServerSession session, AppContext context) throws DispatchException {
        request.requsetBy(session);

        // 获取方法持有器
        final MethodHolder methodHolder = this.methodHolder.get(request.getProtocol());
        if (methodHolder == null) {
            NetMessageDispatcher.DISPATCHER_LOG.error("没有存在 {} 方法 ", request.getProtocol());
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }

        if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
            NetMessageDispatcher.DISPATCHER_LOG.debug("请求:{}\n 请求调用{}.{} 业务方法", LogUtils.msg(request.toString(), methodHolder.getMethodClass(), methodHolder.getName()));

        String serverType = context.getScopeType();
        if (serverType != null && !methodHolder.isCanCall(serverType)) {
            NetMessageDispatcher.DISPATCHER_LOG.error("{} 服务器无法调用 {}.{} 业务方法", LogUtils.msg(serverType, methodHolder.getMethodClass(), methodHolder.getName()));
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }
        return new RequestDispatcherCommand(context, methodHolder, request, session);

    }

    @Override
    public DispatcherCommand<Void> dispatch(Response response, ClientSession session, AppContext context) throws DispatchException {
        final MessageFuture<?> future = response.getID() >= 0 ? session.takeFuture(response.getID()) : null;
        if (future != null) {
            future.setResponse(response);
        }
        return new ResponseDispatcherCommand(context, response, session, future);
    }

    @Override
    public void addDispatcherRequestListener(final DispatcherRequestListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeDispatcherRequestListener(final DispatcherRequestListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void clearDispatcherRequestListener() {
        this.listeners.clear();
    }

    /**
     * 触发未登陆事件
     * <p>
     * <p>
     * 触发未登陆事件<br>
     *
     * @param event 未登陆事件
     */
    private void fireExecuteDispatchException(DispatchExceptionEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
            try {
                listener.executeDispatchException(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.unlogin 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行错误事件
     * <p>
     * <p>
     * 触发业务运行错误事件<br>
     *
     * @param event 业务运行错误事件
     */
    private void fireExecuteException(DispatcherRequestErrorEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
            try {
                listener.executeException(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.executeException 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行事件
     * <p>
     * <p>
     * 触发业务运行事件<br>
     *
     * @param event 业务运行事件
     */
    private void fireExecute(DispatcherRequestEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
            try {
                listener.execute(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.execute 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行完成事件
     * <p>
     * <p>
     * 触发业务运行完成事件<br>
     *
     * @param event 业务运行完成事件
     */
    private void fireFinish(DispatcherRequestEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
            try {
                listener.finish(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.finish 监听器异常", listener.getClass(), e);
            }
        }
    }

    public abstract void initDispatcher(AppContext appContext);

    private static abstract class AbstractDispatcherCommand<S extends NetSession, H extends Message, O> implements DispatcherCommand<O> {

        protected final AppContext appContext;
        protected H message;
        protected S session;
        protected boolean executed = false;

        public AbstractDispatcherCommand(AppContext appContext, H message, S session) {
            this.appContext = appContext;
            this.message = message;
            this.session = session;
        }

        @Override
        public long getUserID() {
            return this.session.getUID();
        }

        @Override
        public int getProtocol() {
            return this.message.getProtocol();
        }

        @Override
        public String getName() {
            return this.getClass() + "#" + this.message.getProtocol();
        }

        @Override
        public void run() {
            this.execute();
        }

        @Override
        public boolean isWorking() {
            return true;
        }

        @Override
        public boolean isCompleted() {
            return this.executed;
        }

        @Override
        public boolean isCanExecute() {
            return true;
        }

        @Override
        public Command<O> getCommand() {
            return this;
        }

        @Override
        public void fail(boolean excuted) {
        }

        @Override
        public Session getSession() {
            return this.session;
        }

    }

    private class ResponseDispatcherCommand extends AbstractDispatcherCommand<ClientSession, Response, Void> {

        private MessageFuture<?> future;

        public ResponseDispatcherCommand(AppContext appContext, Response response, ClientSession session, MessageFuture<?> future) {
            super(appContext, response, session);
            this.future = future;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Void execute() {
            if (this.future != null)
                this.future.setResponse(this.message);
            Object body = this.message.getBody(Object.class);
            if (body == null)
                return null;
            List<ResponseMonitor<?>> monitors = new ArrayList<>();//this.session.getResponseMonitors(body);
            if (monitors != null) {
                for (ResponseMonitor<?> mon : monitors) {
                    ResponseMonitor<Object> monitor = (ResponseMonitor<Object>) mon;
                    if (monitor.getMonitorType().isHandle(this.message.getID())) {
                        try {
                            List<Protocol> includes = monitor.includeController();
                            if (!this.match(this.message, includes, true))
                                continue;
                            List<Protocol> excludes = monitor.excludeController();
                            if (this.match(this.message, excludes, false))
                                continue;
                            monitor.handle(this.session, this.message, this.message.getBody(Object.class));
                        } catch (Throwable e) {
                            NetMessageDispatcher.DISPATCHER_LOG.error("call monitor handle {}", monitor.getClass(), e);
                        }
                    }
                }
            }
            return null;
        }

        public boolean match(Message message, List<Protocol> filters, boolean defResult) {
            if (filters != null && !filters.isEmpty()) {
                for (Protocol controller : filters) {
                    if (controller.isOwn(message)) {
                        return true;
                    }
                }
                return false;
            }
            return defResult;
        }
    }

    private class RequestDispatcherCommand extends AbstractDispatcherCommand<ServerSession, Request, CommandResult> {

        private final MethodHolder methodHolder;

        public RequestDispatcherCommand(AppContext appContext, MethodHolder methodHolder, Request request, ServerSession session) {
            super(appContext, request, session);
            this.methodHolder = methodHolder;
        }

        @Override
        public CommandResult execute() {
            Throwable ex = null;
            CommandResult result = null;
            try {
                result = this.doExecute();
            } catch (DispatchException e) {
                NetMessageDispatcher.DISPATCHER_LOG.error(e.getMessage(), e);
                NetMessageDispatcher.this.fireExecuteDispatchException(new DispatchExceptionEvent(this.message, this.methodHolder, e));
                // this.appContext.getSessionHolder().offline(this.session);
                result = ResultFactory.create(e.getResultCode(), null);
            } catch (InvocationTargetException e) {
                ex = e.getTargetException();
                result = this.handleException(ex);
            } catch (Throwable e) {
                result = this.handleException(e);
            } finally {
                this.executed = true;
            }
            if (result != null) {
                ChannelFuture future = this.session.response(this.message, result.getResultCode(), result.getBody());
                if (future != null && result.getResultCode().getType() == ResultCodeType.ERROR) {
                    future.addListener(future1 -> RequestDispatcherCommand.this.appContext.getSessionHolder().offline(RequestDispatcherCommand.this.session));
                }
            }
            return result;
        }

        @Override
        public void run() {
            this.execute();
        }

        public CommandResult handleException(Throwable ex) {
            NetMessageDispatcher.DISPATCHER_LOG.error("Executing " + this.methodHolder.getMethodClass() + "." + this.methodHolder.getName() + " exception", ex);
            DispatcherRequestErrorEvent event = new DispatcherRequestErrorEvent(this.message, this.methodHolder, ex);
            NetMessageDispatcher.this.fireExecuteException(event);
            CommandResult result = event.getResult();
            if (result != null) {
                return result;
            } else {
                return ResultFactory.create(CoreResponseCode.EXCUTE_EXCEPTION, null);
            }
        }

        public CommandResult doExecute() throws Exception {

            this.checkExecutable();

            CurrentCMD.setCurrent(this.message.getUserID(), this.message.getProtocol());

            // 调用方法
            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行事件", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            NetMessageDispatcher.this.fireExecute(new DispatcherRequestEvent(this.message, this.methodHolder));

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}执行业务", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            CommandResult result = this.methodHolder.exectue(this.message);

            // 执行结束触发命令执行完成事件
            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成事件", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            DispatcherRequestEvent finish = new DispatcherRequestEvent(this.message, this.methodHolder, result);
            NetMessageDispatcher.this.fireFinish(finish);

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成", this.methodHolder.getMethodClass(), this.methodHolder.getName());

            result = finish.getResult();
            if (result == null)
                result = ResultFactory.SUCC;
            return result;
        }

        public void checkExecutable() throws DispatchException {

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测请求 {}.{} 业务方法超时", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            // 是否需要做登录校验,判断是否已经登录
            if (NetMessageDispatcher.this.checkTimeOut && this.methodHolder.isTimeOut() && this.message.isTimeOut(this.methodHolder.getRequestLife())) {
                NetMessageDispatcher.DISPATCHER_LOG.error("请求 {}.{} 业务方法超时", this.methodHolder.getMethodClass(), this.methodHolder.getName());
                throw new DispatchException(CoreResponseCode.REQUEST_TIMEOUT);
            }

            // 是否需要做登录校验,判断是否已经登录
            NetSessionHolder sessionHolder = this.appContext.getSessionHolder();

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测请求 {}.{} 所需登陆权限", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            if (this.methodHolder.isAuth() && !this.message.isLogin()) {
                List<AuthProvider> authProviders = this.appContext.getAuthProviders();
                for (AuthProvider provider : authProviders) {
                    if (!provider.isCanValidate(this.message))
                        continue;
                    LoginCertificate loginInfo = provider.validate(this.message);
                    if (loginInfo == null || !loginInfo.isLogin() || !sessionHolder.online(this.session, loginInfo)) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("用户没有登陆无法请求 {}.{} 业务方法", this.methodHolder.getMethodClass(), this.methodHolder.getName());
                        throw new DispatchException(CoreResponseCode.UNLOGIN);
                    } else {
                        break;
                    }
                }
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测用户调用 {}.{} 权限", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            if (!this.methodHolder.isUserGroup(this.session.getGroup())) {
                NetMessageDispatcher.DISPATCHER_LOG.error("{}用户组用户无法调用{}.{} 业务方法", LogUtils.msg(this.session.getGroup(), this.methodHolder.getMethodClass(), this.methodHolder.getName()));
                throw new DispatchException(CoreResponseCode.NO_PERMISSIONS);
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测调用 {}.{} 请求是否被篡改", this.methodHolder.getMethodClass(), this.methodHolder.getName());

            RequestChecker checker = this.session.getChecker();
            if (this.methodHolder.isCheck() && checker != null) {
                // 获取普通调用的校验码密钥
                if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    NetMessageDispatcher.DISPATCHER_LOG.debug("获取校验码秘钥");
                // 检测请求的正确性
                if (checker != null && !checker.match(this.message)) {
                    NetMessageDispatcher.DISPATCHER_LOG.error("调用 {}.{} 请求被篡改", this.methodHolder.getMethodClass(), this.methodHolder.getName());
                    throw new DispatchException(CoreResponseCode.FALSIFY);
                }
            }

        }

    }

}
