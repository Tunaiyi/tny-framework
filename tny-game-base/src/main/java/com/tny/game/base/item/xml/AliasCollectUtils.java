package com.tny.game.base.item.xml;

import java.util.*;

public class AliasCollectUtils {

    private static HashSet<String> aliasSet = new HashSet<String>();

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
