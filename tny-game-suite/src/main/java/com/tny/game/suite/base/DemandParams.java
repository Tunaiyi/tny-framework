package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.common.utils.ClassImporter;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class DemandParams extends ClassImporter {

    protected static EnumeratorHolder<DemandParam> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_DEMAND_PARAM_CLASS);
    }

    private DemandParams() {
    }

    static void register(DemandParam value) {
        holder.register(value);
    }

    public static <T extends DemandParam> T of(String key) {
        return holder.of(key);
    }

    public static <T extends DemandParam> T of(int id) {
        return holder.of(id);
    }

    public static <T extends DemandParams> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends DemandParams> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<DemandParam> getAll() {
        return holder.values();
    }

}
