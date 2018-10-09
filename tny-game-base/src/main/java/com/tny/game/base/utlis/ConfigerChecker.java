package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.*;
import com.tny.game.base.module.Feature;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;

public class ConfigerChecker {

    private static ConcurrentMap<Object, Set<Object>> ID_CHECK_MAP = new ConcurrentHashMap<Object, Set<Object>>();

    private static Set<Object> getCheckSet(Object key) {
        Set<Object> idSet = ID_CHECK_MAP.get(key);
        if (idSet == null) {
            idSet = new HashSet<>();
            Set<Object> oldSet = ID_CHECK_MAP.putIfAbsent(key, idSet);
            if (oldSet != null)
                idSet = oldSet;
        }
        return idSet;
    }

    public static void check(Object key, Object value, String message, Object... params) {
        Set<Object> idSet = getCheckSet(key);
        if (!idSet.add(value)) {
            IllegalArgumentException e = new IllegalArgumentException(format(message, params));
            e.printStackTrace();
            throw e;
        }
    }

    public static void checkBehaviorBelogFuncSys(Behavior child, Feature parent) {
        if (!(child.getID() + "").startsWith(parent.getID() + "")) {
            IllegalArgumentException e = new IllegalArgumentException(format("{} 行为[ID:{}]不属于 {} 系统[ID:{}]",
                    child, child.getID(), parent, parent.getID()));
            e.printStackTrace();
            throw e;
        }
    }

    public static void checkActionBelogBehavior(Action child, Behavior parent) {
        if (child.getID() / 1000 != parent.getID()) {
            IllegalArgumentException e = new IllegalArgumentException(format("{} 操作[ID:{}]不属于 {} 行为[ID:{}]",
                    child, child.getID(), parent, parent.getID()));
            e.printStackTrace();
            throw e;
        }
    }

}
