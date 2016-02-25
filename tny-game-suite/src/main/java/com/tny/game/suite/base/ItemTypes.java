package com.tny.game.suite.base;

import com.tny.game.base.item.ItemType;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class ItemTypes extends AutoImport {

    protected static EnumeratorHolder<ItemType> holder = new EnumeratorHolder<ItemType>() {
        @Override
        protected void postRegister(ItemType object) {
            putAndCheck("$" + object.getAliasHead(), object);
        }
    };

    static {
        loadClass(Configs.SUITE_BASE_ITEM_TYPE_CLASS);
    }

    private ItemTypes() {
    }

    static {
        try {
            Class.forName(Configs.SUITE_CONFIG.getStr(Configs.SUITE_BASE_ITEM_TYPE_CLASS));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    public static <T extends ItemType> T ofItemID(int itemID) {
        int typeID = itemID / 1000000 * 1000000;
        return of(typeID);
    }


}
