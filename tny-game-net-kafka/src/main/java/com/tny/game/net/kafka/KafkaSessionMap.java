package com.tny.game.net.kafka;

import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.net.dispatcher.NetSession;

import java.util.Map;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaSessionMap<S extends NetSession> {

    private Map<String, S> sessionMap = new CopyOnWriteMap<>();

    public S get(String group, long uid) {
        return sessionMap.get(key(group, uid));
    }

    public boolean exist(String group, long id) {
        return sessionMap.containsKey(key(group, id));
    }

    public S put(String group, long id, S session) {
        S old = sessionMap.putIfAbsent(key(group, id), session);
        return old != null ? old : session;
    }

    // public S put(KafkaServerInfo connectTo, S session) {
    //     S old = sessionMap.putIfAbsent(key(connectTo.getRemoteType(), connectTo.getID()), session);
    //     return old != null ? old : session;
    // }
    //
    // public S put(LoginCertificate cer, S session) {
    //     S old = sessionMap.putIfAbsent(key(cer.getUserGroup(), cer.getUserID()), session);
    //     return old != null ? old : session;
    // }
    // public S put(S session) {
    //     S old = sessionMap.putIfAbsent(key(session.getGroup(), session.getUID()), session);
    //     return old != null ? old : session;
    // }

    private String key(String group, long id) {
        return group + "-" + id;
    }

    private String key(String serverType, int id) {
        return serverType + "-" + id;
    }

}
