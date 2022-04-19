package com.tny.game.basics.item.xml;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class AliasCollectUtils {

    private static Set<String> aliasSet = new ConcurrentSkipListSet<>();

    public static void addAlias(String alias) {
        if (alias == null || alias.equalsIgnoreCase("null")) {
            throw new IllegalArgumentException("alias is " + alias);
        }
        aliasSet.add(alias);
    }

    public static Set<String> getAllAlias() {
        return aliasSet;
    }

}
