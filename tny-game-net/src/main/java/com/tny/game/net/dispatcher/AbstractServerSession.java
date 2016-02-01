package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketEncoder;

public abstract class AbstractServerSession implements ServerSession {

    protected LoginCertificate certificate;

    /**
     * 编码器
     */
    protected DataPacketEncoder encoder;

    protected RequestChecker checker;

    protected MessageBuilderFactory messageBuilderFactory;

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

    /**
     * 断开连接
     */
    @Override
    public abstract void disconnect();


}