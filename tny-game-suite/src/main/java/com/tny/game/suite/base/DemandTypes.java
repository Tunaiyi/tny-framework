package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.config.*;
import com.tny.game.common.enums.*;
import com.tny.game.suite.utils.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class DemandTypes extends ClassImporter {

    protected static EnumeratorHolder<DemandType> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_DEMAND_TYPE_CLASS);
    }

    private DemandTypes() {
    }

    static void register(DemandType value) {
        holder.register(value);
    }

    public static <T extends DemandType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends DemandType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends DemandType> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends DemandType> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<DemandType> getAll() {
        return holder.values();
    }

}
