package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class DemandParams extends ClassImporter {

    protected static EnumeratorHolder<DemandParam> holder = new EnumeratorHolder<>();

    private DemandParams() {
    }

    static void register(DemandParam value) {
        holder.register(value);
    }

    public static <T extends DemandParam> T check(String key) {
        return holder.check(key, "获取 {} DemandParam 不存在", key);
    }

    public static <T extends DemandParam> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 DemandParam 不存在", id);
    }

    public static <T extends DemandParam> T of(int id) {
        return holder.of(id);
    }

    public static <T extends DemandParam> T of(String key) {
        return holder.of(key);
    }

    public static <T extends DemandParam> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends DemandParam> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends DemandParam> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<DemandParam> enumerator() {
        return holder;
    }

}
