package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.dispatcher.BaseSession;
import com.tny.game.net.dispatcher.BaseSessionHolder;
import com.tny.game.net.dispatcher.ProxyServerSession;
import com.tny.game.net.session.Session;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MobileSessionHolder extends BaseSessionHolder {

    public static AttrKey<MobileAttach> MOBILE_ATTACH = AttributeUtils.key(MobileSessionHolder.class, "MOBILE_ATTACH_KEY");

    public ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("MobileSessionRecycleThread"));

    private int responseCacheSize = 50;
    private long offlineWait = 1000 * 60 * 120;

    private Set<Object> mobileUserGroups = new HashSet<>();

    public MobileSessionHolder() {
        this(Collections.emptySet());
        this.mobileUserGroups.add(Session.DEFAULT_USER_GROUP);
    }

    public void setOfflineWait(long offlineWait) {
        this.offlineWait = offlineWait;
    }

    public void setResponseCacheSize(int responseCacheSize) {
        this.responseCacheSize = responseCacheSize;
    }

    public MobileSessionHolder(Collection<Object> mobileUserGroup) {
        this.mobileUserGroups.addAll(mobileUserGroup);
        this.mobileUserGroups.add(Session.DEFAULT_USER_GROUP);
        this.executorService.schedule(new Runnable() {

            @Override
            public void run() {
                MobileSessionHolder holder = MobileSessionHolder.this;
                long now = System.currentTimeMillis();
                try {
                    for (Object mobileGroup : MobileSessionHolder.this.mobileUserGroups) {
                        ConcurrentMap<Object, ServerSession> map = holder.getSessionMap(mobileGroup);
                        if (map == null)
                            continue;
                        for (Session session : map.values()) {
                            try {
                                MobileAttach attach = MobileSessionHolder.this.getMobileAttach(session);
                                if (attach == null || attach.isOfflineTimeout(now) && attach.invalid()) {
                                    holder.invalid(session);
                                }
                            } catch (Exception e) {
                                BaseSessionHolder.LOG.error("MobileSession [{} - {}]recycle exception", session.getGroup(), session.getUID(), e);
                            }
                        }
                    }
                } finally {
                    MobileSessionHolder.this.executorService.schedule(this, 5, TimeUnit.SECONDS);
                }
            }

        }, 5, TimeUnit.SECONDS);
    }

    protected ConcurrentMap<Object, ServerSession> getSessionMap(Object mobileGroup) {
        return this.sessionMap.get(mobileGroup);
    }

    private MobileAttach getMobileAttach(Session session) {
        return session.attributes().getAttribute(MobileSessionHolder.MOBILE_ATTACH);
    }

    private void invalid(Session session) {
        super.offline(session);
    }

    @Override
    public void offline(Session session) {
        ConcurrentMap<Object, ServerSession> userGroupSessionMap = this.sessionMap.get(session.getGroup());
        if (userGroupSessionMap == null)
            return;
        MobileAttach attach = this.getMobileAttach(session);
        if (attach == null) {
            super.offline(session);
        } else {
            Session current = userGroupSessionMap.get(session.getUID());
            if (!current.equals(session))
                return;
            attach.offline(this.offlineWait);
            if (session.isConnected())
                this.disconnect(session);
        }
    }

    protected void setMobileAttach(BaseSession session, MobileAttach attach) {
        session.attributes().setAttribute(MobileSessionHolder.MOBILE_ATTACH, attach);
    }

    @Override
    protected boolean online(ServerSession session, LoginCertificate loginInfo) throws ValidatorFailException {
        if (!loginInfo.isLogin())
            return false;
        ServerSession addSession;
        if (this.mobileUserGroups.contains(loginInfo.getUserGroup())) {
            ProxyServerSession current = (ProxyServerSession) this.getSession(loginInfo.getUserGroup(), loginInfo.getUserID());
            if (current == null || !loginInfo.isRelogin()) {
                current = new ProxyServerSession(session);
                this.setMobileAttach(current, new MobileAttach(this.responseCacheSize));
            } else {
                if (current.getCertificate().getLoginID() != loginInfo.getLoginID()) {
                    String account = loginInfo.getUserGroup() + "-" + loginInfo.getUserID();
                    throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS, account, session.getHostName());
                }
                MobileAttach attach = this.getMobileAttach(current);
                if (attach != null && (attach.isState(MobileSessionState.ONLINE) || attach.online())) {
                    Session old = current.setSession(session);
                    if (old != null && old != session)
                        this.disconnect(old);
                    this.setMobileAttach(current, attach);
                } else {
                    String account = loginInfo.getUserGroup() + "-" + loginInfo.getUserID();
                    throw new ValidatorFailException(CoreResponseCode.SESSION_TIMEOUT, account, session.getHostName());
                }
            }
            addSession = current;
        } else {
            addSession = session;
        }
        return addSession != null && super.online(addSession, loginInfo);
    }

    public int getActiveSize(Object group) {
        ConcurrentMap<Object, ServerSession> map = this.getSessionMap(group);
        if (map == null)
            return 0;
        int size = 0;
        long now = System.currentTimeMillis();
        for (Session session : map.values()) {
            MobileAttach attach = this.getMobileAttach(session);
            if (attach != null && !attach.isOfflineTimeout(now))
                size++;
        }
        return size;
    }

    public int getConnectSize(Object group) {
        ConcurrentMap<Object, ServerSession> map = this.getSessionMap(group);
        if (map == null)
            return 0;
        int size = 0;
        for (Session session : map.values()) {
            if (session.isConnected())
                size++;
        }
        return size;
    }

}
