package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class DemandTypes extends ClassImporter {

    protected static EnumeratorHolder<DemandType> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_DEMAND_TYPE_CLASS);
    //    }

    private DemandTypes() {
    }

    static void register(DemandType value) {
        holder.register(value);
    }

    public static <T extends DemandType> T check(String key) {
        return holder.of(key);
    }

    public static <T extends DemandType> T check(int id) {
        return holder.of(id);
    }

    public static <T extends DemandType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends DemandType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends DemandType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends DemandType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends DemandType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<DemandType> enumerator() {
        return holder;
    }

}
