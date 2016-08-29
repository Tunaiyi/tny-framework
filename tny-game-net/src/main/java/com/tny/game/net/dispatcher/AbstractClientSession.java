package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.LoginCertificate;

public abstract class AbstractClientSession implements ClientSession {

    protected LoginCertificate certificate;

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

    public AbstractClientSession(LoginCertificate loginInfo) {
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
    public boolean isAskerLogin() {
        return this.certificate != null && this.certificate.isLogin();
    }

    @Override
    public long getLoginAt() {
        return this.certificate.getLoginAt();
    }

    public MessageBuilderFactory getMessageBuilderFactory() {
        return this.messageBuilderFactory;
    }

    @Override
    public void login(LoginCertificate certificate) {
        this.certificate = certificate;
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

    @Override
    public LoginCertificate getCertificate() {
        return certificate;
    }

}