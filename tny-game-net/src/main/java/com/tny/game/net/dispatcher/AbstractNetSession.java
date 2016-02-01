package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketEncoder;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractNetSession implements ServerSession, ClientSession {

    protected LoginCertificate certificate;

    /**
     * 编码器
     */
    protected DataPacketEncoder encoder;

    protected RequestChecker checker;

    protected MessageBuilderFactory messageBuilderFactory;

    protected ResponseMonitorHolder monitorHolder;

    protected AtomicInteger requestIDCreator = new AtomicInteger(1);

    private AtomicBoolean futureMapLock = new AtomicBoolean(false);

    private volatile HashMap<Integer, MessageFuture<?>> futureMap;

    private volatile transient Attributes attributes;

    @Override
    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    public AbstractNetSession(LoginCertificate loginInfo) {
        super();
        this.certificate = loginInfo;
    }

    @Override
    public long getUID() {
        return this.certificate.getUserID();
    }

    @Override
    public String getGroup() {
        return this.certificate.getUserGroup();
    }

    @Override
    public LoginCertificate getCertificate() {
        return this.certificate;
    }

    @Override
    public boolean isAskerLogin() {
        return this.certificate != null && this.certificate.isLogin();
    }

    @Override
    public long getLoginAt() {
        return this.certificate.getLoginAt();
    }

    @Override
    public DataPacketEncoder getEncoder() {
        return this.encoder;
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
        return this.messageBuilderFactory;
    }

    @Override
    public void login(LoginCertificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public RequestChecker getChecker() {
        return this.checker;
    }

    @Override
    public MessageFuture<?> takeFuture(int id) {
        if (this.futureMap == null)
            return null;
        MessageFuture<?> future = null;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                future = this.futureMap.remove(id);
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
        return future;
    }

    @Override
    public void putFuture(MessageFuture<?> future) {
        if (future == null)
            return;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                if (this.futureMap == null)
                    this.futureMap = new HashMap<>();
                this.futureMap.put(future.getRequestID(), future);
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
    }

    @Override
    public void clearFuture() {
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                this.futureMap = new HashMap<>();
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
    }

    /**
     * 断开连接
     */
    @Override
    public abstract void disconnect();

    //	@Override
    //	public void registerMonitorHolder(ResponseMonitorHolder monitorHolder) {
    //		this.monitorHolder = monitorHolder;
    //	}

    //	@Override
    //	public List<ResponseMonitor<?>> getResponseMonitors(Object body) {
    //		if (this.monitorHolder != null)
    //			return this.monitorHolder.getMonitorHolderList(body);
    //		return Collections.emptyList();
    //	}

}