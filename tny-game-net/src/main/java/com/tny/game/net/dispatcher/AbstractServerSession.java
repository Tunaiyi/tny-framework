package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.exception.SessionException;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServerSession implements ServerSession {

    protected LoginCertificate certificate;

    protected List<RequestChecker> checkers;

    protected MessageBuilderFactory messageBuilderFactory;

    private AtomicBoolean lock = new AtomicBoolean(false);

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

    public AbstractServerSession(LoginCertificate loginInfo) {
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

    protected MessageBuilderFactory getMessageBuilderFactory() {
        return this.messageBuilderFactory;
    }


    @Override
    public Optional<NetFuture> response(Protocol protocol, ResultCode code, Object body) {
        if (protocol.isPush()) {
            SessionPushOption option = this.attributes().getAttribute(SessionPushOption.SESSION_PUSH_OPTION, SessionPushOption.PUSH);
            if (!option.isPush()) {
                if (option.isThrowable())
                    throw new SessionException(LogUtils.format("Session {}-{} [{}] 无法推送", this.getCertificate(), this.getGroup(), this.getUID()));
                return Optional.empty();
            }
        }
        Object data;
        if (body instanceof ByteBuf || body instanceof byte[] || body instanceof Response) {
            data = body;
            return this.write(data);
        } else {
            Optional<NetFuture> optional = Optional.empty();
            NetResponse response = (NetResponse) this.getMessageBuilderFactory()
                    .newResponseBuilder(this)
                    .setID(protocol.isPush() ? 0 : DEFAULT_RESPONSE_ID)
                    .setProtocol(protocol)
                    .setResult(code)
                    .setBody(body)
                    .build();
            CoreLogger.log(this, response);
            while (lock.compareAndSet(false, true)) {
                try {
                    response.setNumber(createResponseNumber());
                    data = response;
                    this.prepareWriteResponse(response);
                    optional = this.write(data);
                    break;
                } finally {
                    lock.set(false);
                }
            }
            if (optional.isPresent())
                this.postWriteResponse(response, optional.get());
            return optional;
        }
    }

    protected abstract int createResponseNumber();

    protected abstract Optional<NetFuture> write(Object data);

    protected void prepareWriteResponse(Response response) {
    }

    protected void postWriteResponse(Response response, NetFuture future) {
    }

    @Override
    public void login(LoginCertificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public List<RequestChecker> getCheckers() {
        return this.checkers;
    }

    /**
     * 断开连接
     */
    @Override
    public abstract void disconnect();


}