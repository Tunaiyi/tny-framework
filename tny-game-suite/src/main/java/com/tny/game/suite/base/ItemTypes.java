package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.base.item.ItemType.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class ItemTypes extends ClassImporter {

    protected static EnumeratorHolder<ItemType> holder = new EnumeratorHolder<ItemType>() {
        @Override
        protected void postRegister(ItemType object) {
            putAndCheck("$" + object.getAliasHead(), object);
        }
    };

    private ItemTypes() {
    }

    static void register(ItemType value) {
        holder.register(value);
    }

    public static <T extends ItemType> T ofAlias(String alias) {
        String[] heads = StringUtils.split(alias, '$');
        return holder.ofAndCheck("$" + heads[0], "获取 别名前缀 {} 的 ItemType 不存在", heads[0]);
    }

    public static <T extends ItemType> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} ItemType 不存在", key);
    }

    public static <T extends ItemType> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 ItemType 不存在", id);
    }

    public static <T extends ItemType> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends ItemType> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static <T extends ItemType> T ofItemId(int itemID) {
        int typeID = itemID / ID_TAIL_SIZE * ID_TAIL_SIZE;
        return of(typeID);
    }

}
