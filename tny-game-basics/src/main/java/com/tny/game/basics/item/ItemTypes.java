package com.tny.game.basics.item;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.basics.item.ItemType.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class ItemTypes extends ClassImporter {

    private static final EnumerableSymbol<ItemType, String> ALIAS_HEAD_SYMBOL = EnumerableSymbol.symbolOf(
            ItemType.class, "aliasHead", ItemType::getAliasHead);

    protected static EnumeratorHolder<ItemType> holder = new EnumeratorHolder<ItemType>() {

        @Override
        protected void postRegister(ItemType object) {
            putAndCheckSymbol(ALIAS_HEAD_SYMBOL, object);
        }
    };

    private ItemTypes() {
    }

    static void register(ItemType value) {
        holder.register(value);
    }

    public static <T extends ItemType> T ofAlias(String alias) {
        String[] heads = StringUtils.split(alias, '$');
        return holder.checkBySymbol(ALIAS_HEAD_SYMBOL, heads[0], "获取 别名前缀 {} 的 ItemType 不存在", heads[0]);
    }

    public static <T extends ItemType> T check(String key) {
        return holder.check(key, "获取 {} ItemType 不存在", key);
    }

    public static <T extends ItemType> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 ItemType 不存在", id);
    }

    public static <T extends ItemType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends ItemType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends ItemType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends ItemType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends ItemType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<ItemType> enumerator() {
        return holder;
    }

    public static <T extends ItemType> T ofModelId(int modelId) {
        int typeId = modelId / ID_TAIL_SIZE * ID_TAIL_SIZE;
        return of(typeId);
    }

    public static <T extends ItemType> T ofItemId(long id) {
        if (id < 10000L) {
            return ofModelId((int)id);
        }
        if (id < 100000L) {
            return ofModelId((int)(id / 10L));
        }
        if (id < 1000000L) {
            return ofModelId((int)(id / 100L));
        }
        if (id < 10000000L) {
            return ofModelId((int)(id / 1000L));
        }
        if (id < 100000000L) {
            return ofModelId((int)(id / 10000L));
        }
        if (id < 1000000000L) {
            return ofModelId((int)(id / 100000L));
        }
        if (id < 10000000000L) {
            return ofModelId((int)(id / 1000000L));
        }
        if (id < 100000000000L) {
            return ofModelId((int)(id / 10000000L));
        }
        if (id < 1000000000000L) {
            return ofModelId((int)(id / 100000000L));
        }
        if (id < 10000000000000L) {
            return ofModelId((int)(id / 1000000000L));
        }
        if (id < 100000000000000L) {
            return ofModelId((int)(id / 10000000000L));
        }
        if (id < 1000000000000000L) {
            return ofModelId((int)(id / 100000000000L));
        }
        if (id < 10000000000000000L) {
            return ofModelId((int)(id / 1000000000000L));
        }
        if (id < 100000000000000000L) {
            return ofModelId((int)(id / 10000000000000L));
        }
        return ofModelId((int)(id / 100000000000000L));
    }

}
