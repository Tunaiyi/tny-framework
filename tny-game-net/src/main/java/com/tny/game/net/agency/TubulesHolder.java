package com.tny.game.net.agency;

import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/28 10:34 下午
 */
public class TubulesHolder {

    private String userType;

    private final Map<UID, NetTubule<UID>> tubuleMap = new ConcurrentHashMap<>();

    public Optional<Tubule<UID>> getTubuleByUserId(UID userId) {
        return Optional.ofNullable(this.tubuleMap.get(userId));
    }

}
