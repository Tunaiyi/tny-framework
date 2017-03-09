package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.base.listener.SessionChangeEvent;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BaseSessionHolder<S extends NetSession<?>> extends NetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.SESSION);
    // private static final Logger LOG_ENCODE = LoggerFactory.getLogger(CoreLogger.CODER);

    protected final ConcurrentHashMap<String, ConcurrentMap<Object, NetSession>> sessionMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ConcurrentMap<Object, ChannelGroup>> channelMap = new ConcurrentHashMap<>();

    @Override
    public <T> Session<T> getSession(String userGroup, T key) {
        return this.getSession0(userGroup, key);
    }

    @SuppressWarnings("unchecked")
    private <T> NetSession<T> getSession0(String userGroup, T key) {
        ConcurrentMap<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return null;
        return (NetSession<T>) userGroupSessionMap.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, Session> getSessionMapByGroup(String userGroup) {
        Map<Object, ? extends Session> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return Collections.emptyMap();
        return (Map<Object, Session>) userGroupSessionMap;
    }

    @Override
    public boolean isOnline(String userGroup, Object key) {
        Session session = this.getSession0(userGroup, key);
        return session != null && session.isOnline();
    }

    private ChannelGroup getChannelGroup(String userGroup, Object channelID) {
        ConcurrentMap<Object, ChannelGroup> userGroupMap = this.channelMap.get(userGroup);
        if (userGroupMap == null)
            return null;
        return userGroupMap.get(channelID);
    }

    @Override
    public boolean addChannelUser(String userGroup, Object channelID, Object uid) {
        NetSession session = this.getSession0(userGroup, uid);
        if (session == null)
            return false;
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return false;
        group.sessionGroup.put(session.getUID(), session);
        this.debugGroupSize(group);
        return true;
    }

    @Override
    public int addChannelUser(String userGroup, Object groupID, Collection<?> uidColl) {
        int num = 0;
        for (Object uid : uidColl)
            num = this.addChannelUser(userGroup, groupID, uid) ? num + 1 : num;
        return num;
    }

    @Override
    public boolean isInChannel(String userGroup, Object channelID, Object uid) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return false;
        Session<?> session = this.getSession0(userGroup, uid);
        return session != null && group.sessionGroup.get(session.getUID()) == session;
    }

    @Override
    public boolean removeChannelUser(String userGroup, Object channelID, Object uid) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return false;
        Session<Object> session = this.getSession0(userGroup, uid);
        if (session == null)
            return false;
        group.sessionGroup.remove(session);
        this.debugGroupSize(group);
        return true;
    }

    @Override
    public int removeChannelUser(String userGroup, Object channelID, Collection<?> uidColl) {
        int num = 0;
        for (Object uid : uidColl)
            num = this.removeChannelUser(userGroup, channelID, uid) ? num + 1 : num;
        return num;
    }

    @Override
    public void clearChannelUser(String userGroup, Object channelID) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return;
        group.sessionGroup.clear();
        this.debugGroupSize(group);
    }

    @Override
    public boolean send2User(String userGroup, Object uid, Protocol protocol, MessageContent content) {
        NetSession session = this.getSession0(userGroup, uid);
        if (session != null) {
            session.sendMessage(protocol, content);
            return true;
        }
        return false;
    }

    @Override
    public boolean send2Channel(String userGroup, Object channelID, Protocol protocol, MessageContent<?> content) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return false;
        this.debugGroupSize(group);
        this.doSendMultiSession(group.sessionGroup.values(), protocol, content);
        return true;
    }


    @Override
    public int send2Users(String userGroup, Collection<?> uidColl, Protocol protocol, MessageContent<?> content) {
        return this.doSendMultiSessionID(userGroup, uidColl, protocol, content);
    }

    @Override
    public void send2AllOnline(String userGroup, Protocol protocol, MessageContent<?> content) {
        ConcurrentMap<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return;
        this.doSendMultiSession(userGroupSessionMap.values(), protocol, content);
    }

    //	@Override
    //	public boolean send2User(String userGroup, Object uid, Message message) {
    //		return send2User(userGroup, uid, message, null);
    //	}
    //
    //	@Override
    //	public int send2User(String userGroup, Collection<?> uidColl, Message message) {
    //		return send2User(userGroup, uidColl, message, null);
    //	}
    //
    //	@Override
    //	public boolean send2Channel(String userGroup, Object channelID, Message message) {
    //		return send2Channel(userGroup, channelID, message, null);
    //	}
    //
    //	@Override
    //	public int send2AllOnline(String userGroup, Message message) {
    //		return send2AllOnline(userGroup, message, null);
    //	}

    @Override
    public int getChannelSize(String userGroup, Object channelID) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group == null)
            return 0;
        return group.sessionGroup.size();
    }

    @Override
    public int size() {
        int size = 0;
        for (ConcurrentMap<Object, NetSession> map : this.sessionMap.values())
            size += map.size();
        return size;
    }

    private void debugSessionSize() {
        if (BaseSessionHolder.LOG.isDebugEnabled())
            BaseSessionHolder.LOG.debug("#DefaultSessionHolder#会话管理器#会话数量为 {}", this.sessionMap.size());
    }

    private void debugGroupSize(ChannelGroup group) {
        ConcurrentMap<Object, ChannelGroup> groupMap = this.channelMap.get(group.getID());
        if (BaseSessionHolder.LOG.isDebugEnabled())
            BaseSessionHolder.LOG.debug("#DefaultSessionHolder#会话管理器#会话组数量 {}, {}会话组数量为 {} ", LogUtils.msg(groupMap.size(), group.id, group.sessionGroup.size()));
    }

    @Override
    public boolean createChannel(String userGroup, Object channelID) {
        return this.getChannelGroup0(userGroup, channelID) != null;
    }

    @Override
    public boolean removeChannel(String userGroup, Object channelID) {
        ConcurrentMap<Object, ChannelGroup> userGroupMap = this.channelMap.get(userGroup);
        if (userGroupMap == null)
            return true;
        ChannelGroup group = userGroupMap.remove(channelID);
        if (group != null) {
            group.sessionGroup.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean isExistChannel(String userGroup, Object channelID) {
        return this.getChannelGroup(userGroup, channelID) != null;
    }

    private ChannelGroup getChannelGroup0(String userGroup, Object channelID) {
        ChannelGroup group = this.getChannelGroup(userGroup, channelID);
        if (group != null)
            return group;
        ConcurrentMap<Object, ChannelGroup> userGroupMap = this.channelMap.get(userGroup);
        if (userGroupMap == null) {
            this.channelMap.putIfAbsent(userGroup, new ConcurrentHashMap<>());
            userGroupMap = this.channelMap.get(userGroup);
        }
        group = new ChannelGroup(userGroup);
        ChannelGroup oldGroup = userGroupMap.putIfAbsent(channelID, group);
        return oldGroup == null ? group : oldGroup;
    }

    @Override
    public int countOnline(String userGroup) {
        return this.sessionMap.size();
    }

    private void doSendMultiSession(Collection<? extends NetSession> sessionCollection, Protocol protocol, MessageContent<?> content) {
        for (NetSession session : sessionCollection)
            session.sendMessage(protocol, content);
    }

    private int doSendMultiSessionID(String userGroup, Collection<?> uidColl, Protocol protocol, MessageContent<?> content) {
        int num = 0;
        for (Object uid : uidColl) {
            NetSession session = this.getSession0(userGroup, uid);
            if (session != null) {
                session.sendMessage(protocol, content);
                num++;
            }
        }
        return num;
    }

    @Override
    public <T> Session<T> offline(String userGroup, T key) {
        NetSession<T> session = this.getSession0(userGroup, key);
        if (session != null) {
            this.disconnect(session);
            this.offline(session);
        }
        return session;
    }

    @Override
    public void offlineAll(String userGroup) {
        ConcurrentMap<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null) {
            return;
        }
        for (Entry<Object, NetSession> entry : userGroupSessionMap.entrySet()) {
            //			this.disconnect(entry.getValue());
            this.offline(entry.getValue());
        }
    }

    @Override
    public void offlineAll() {
        for (ConcurrentMap<Object, NetSession> userGroupSessionMap : this.sessionMap.values()) {
            if (userGroupSessionMap == null) {
                return;
            }
            for (Entry<Object, NetSession> entry : userGroupSessionMap.entrySet()) {
                this.disconnect(entry.getValue());
                this.offline(entry.getValue());
            }
        }
    }

    @Override
    protected void removeAllChannel(String userGroup) {
        ConcurrentMap<Object, ChannelGroup> userGroupMap = this.channelMap.get(userGroup);
        if (userGroupMap == null)
            return;
        userGroupMap.clear();
    }

    @Override
    public void offline(Session<?> session) {
        ConcurrentMap<Object, NetSession> userGroupSessionMap = this.sessionMap.get(session.getGroup());
        if (userGroupSessionMap == null)
            return;
        if (userGroupSessionMap.remove(session.getUID(), session) && session.isConnect()) {
            this.disconnect((NetSession) session);
        }
        ConcurrentMap<Object, ChannelGroup> userGroupMap = this.channelMap.get(session.getGroup());
        if (userGroupMap != null) {
            for (ChannelGroup group : userGroupMap.values()) {
                group.sessionGroup.remove(session);
            }
        }
        this.fireRemoveSession(new SessionChangeEvent<>(this, session));
        this.debugSessionSize();
    }

    private <T> boolean loginSession(NetSession<T> session, LoginCertificate<T> certificate) {
        if (!certificate.isLogin())
            return false;
        session.login(certificate);
        return true;
    }

    @Override
    protected <T> boolean online(NetSession<T> session, LoginCertificate<T> loginInfo) throws ValidatorFailException {
        if (!this.loginSession(session, loginInfo))
            return false;
        ConcurrentMap<Object, NetSession> userGroupSessionMap = this.sessionMap.get(session.getGroup());
        if (userGroupSessionMap == null) {
            this.sessionMap.putIfAbsent(session.getGroup(), new ConcurrentHashMap<>());
            userGroupSessionMap = this.sessionMap.get(session.getGroup());
        }
        NetSession<?> oldSession = userGroupSessionMap.put(session.getUID(), session);
        if (oldSession != null && oldSession != session) {
            this.fireRemoveSession(new SessionChangeEvent<>(this, oldSession));
            if (oldSession.isConnect())
                this.disconnect(oldSession);
        }
        this.fireAddSession(new SessionChangeEvent<>(this, session));
        this.debugSessionSize();
        return true;
    }

    private static class ChannelGroup {

        private final String id;
        private final ConcurrentHashMap<Object, NetSession<?>> sessionGroup = new ConcurrentHashMap<>();

        private ChannelGroup(String id) {
            super();
            this.id = id;
        }

        private String getID() {
            return id;
        }
    }

    // private Response newResponse(Session session, Protocol protocol, ResultCode code, Object body) {
    //     return session.getMessageBuilderFactory()
    //             .newResponseBuilder()
    //             .setProtocol(protocol)
    //             .setResult(code)
    //             .setBody(body)
    //             .build();
    // }

    // private Object encode(DataPacketEncoder encoder, Response response) {
    //     try {
    //         if (encoder != null) {
    //             ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer();
    //             try {
    //                 encoder.encodeObject(response, buf);
    //                 if (buf.readableBytes() > 0) {
    //                     byte[] data = new byte[buf.readableBytes()];
    //                     buf.readBytes(data);
    //                     return data;
    //                 }
    //             } finally {
    //                 buf.release();
    //             }
    //         }
    //         return response;
    //     } catch (Exception e) {
    //         BaseSessionHolder.LOG_ENCODE.error("#BaseEncode# 编码异常 ", e);
    //     }
    //     return null;
    // }

    // private boolean sendAndSave(Map<Object, Object> messageMap, NetSession session, Protocol protocol, ResultCode code, Object body) {
    //     Object data = null;
    //     DataPacketEncoder encoder = null;
    //     if (session != null) {
    //         encoder = session.getEncoder();
    //         if (encoder != null)
    //             data = messageMap.get(encoder);
    //     }
    //     MessageBuilderFactory factory = session.getMessageBuilderFactory();
    //     Response response = (Response) messageMap.get(factory);
    //     if (data == null) {
    //         if (response == null) {
    //             response = this.newResponse(session, protocol, code, body);
    //             messageMap.put(factory, response);
    //         }
    //         if (encoder != null) {
    //             //创建出来如果是ByteBuf 引用为 2
    //             data = this.encode(encoder, response);
    //             if (data == null)
    //                 return false;
    //             messageMap.put(encoder, data);
    //         } else {
    //             data = response;
    //         }
    //     }
    //     return data != null && session.response(protocol, data) != null;
    // }

}
