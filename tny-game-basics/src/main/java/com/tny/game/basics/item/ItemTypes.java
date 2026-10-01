/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.tny.game.basics.utlis.*;
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
        EnumRegistrySupport.register(holder, value);
    }

    //    自有扩展保留（D5）：别名前缀查找为 ItemTypes 独有。
    //    空串/纯分隔符受控失败（fix-registered-defects tasks 6.2 转正，差量「空标识符查找显式失败不越界」）：
    //    拆前判非法，废除 StringUtils.split 剥空段后 heads[0] 的下标越界路径；null 与其余畸形文本维持原通道现状。
    public static <T extends ItemType> T ofAlias(String alias) {
        if (alias != null && (alias.isEmpty() || StringUtils.containsOnly(alias, '$'))) {
            throw new IllegalArgumentException("ItemType 别名查找输入非法（空串或纯分隔符）: \"" + alias + "\"");
        }
        String[] heads = StringUtils.split(alias, '$');
        return holder.checkBySymbol(ALIAS_HEAD_SYMBOL, heads[0], "获取 别名前缀 {} 的 ItemType 不存在", heads[0]);
    }

    public static <T extends ItemType> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "ItemType");
    }

    public static <T extends ItemType> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "ItemType");
    }

    public static <T extends ItemType> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends ItemType> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends ItemType> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends ItemType> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends ItemType> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<ItemType> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

    public static <T extends ItemType> T ofModelId(int modelId) {
        int typeId = modelId / ID_TAIL_SIZE * ID_TAIL_SIZE;
        return of(typeId);
    }

    //    单一商式段基址还原（fix-registered-defects tasks 6.3 转正，design D5/差量「道具位号解析按模型段商式
    //    单一规则收敛」）：废除位宽阶梯（每级先缩位再叠 ofModelId 段内截断，两级规则互相抵消致任意输入恒落
    //    段 0）。号段证据（tasks 1.1 实测）：全局号由 ItemType.itemIdOf 以十进制拼接 idHead+序列号产出、
    //    getIdHead()=id/ID_TAIL_SIZE 单档（注册模型号段基址为 ID_TAIL_SIZE 的个位倍数）——逆运算即"对全局号
    //    反复 ÷10 取商至首位数字，首位即 idHead，×ID_TAIL_SIZE 还原段基址"后交 ofModelId 段基址还原；
    //    同一模型下任意位宽序列号恒归位模型条目，未注册段基址按 ofModelId→宽松 of 通道语义返 null。
    public static <T extends ItemType> T ofItemId(long id) {
        long head = id;
        while (head >= 10L || head <= -10L) {
            head /= 10L;
        }
        return ofModelId((int) (head * ID_TAIL_SIZE));
    }

}
