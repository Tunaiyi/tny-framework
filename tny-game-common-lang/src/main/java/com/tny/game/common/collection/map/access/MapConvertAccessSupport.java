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

package com.tny.game.common.collection.map.access;

import java.util.function.Function;

import com.tny.game.common.type.Wrapper;

import static com.tny.game.common.utils.ObjectAide.convertTo;

/**
 * ObjectMap/WrapperObjectMap 两枚转换取值链的共享引擎（reduce-code-duplication D4：包私有单一事实源）。
 * <p>
 * 方法体逐字搬运自两侧私有 asObject/asNotNullObject/getNotNullToFunction 的克隆段：
 * <ul>
 * <li>取原值动作留在各侧门面（ObjectMap 经 getObject(key)，Wrapper 经 map.get(key)），本引擎只做
 * null 判定/缺省/抛错/宽松转换尾段——两侧原值取径的覆写语义差异（ObjectMap 为 HashMap 子类，
 * get/getObject 可被子类化覆写）不受影响。</li>
 * <li>fix-registered-defects D8 翻转：原"两侧各自传入 {@code float.class}/{@code Float.class} 承载现状、
 * CCE 消息尾段分叉逐字保留"的钉桩策略，翻到 object-access-conversion 差量承诺方向——
 * 本引擎入口处把原始类参数统一装箱归一（单一事实源一处归一、两侧同得逐字一致的装箱形态尾段；
 * 成功面行为零变化，ObjectAide.convertTo 历史演进本已消化原始类参数），
 * ObjectMap 门面的 float.class 字面参数同批改入 Float.class。</li>
 * </ul>
 * 本类为包私有实现细节，非发布 API。
 */
final class MapConvertAccessSupport {

    private MapConvertAccessSupport() {
    }

    /**
     * D8 装箱归一（fix-registered-defects）：原始类参数在单一事实源入口统一换装箱类，
     * 失败消息的目标类型描述尾段（ObjectAide.convertTo 抛错用原 clazz 打印）由此对两实现恒同形。
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> boxed(Class<T> valueClass) {
        return valueClass.isPrimitive() ? (Class<T>) Wrapper.getWrapper(valueClass) : valueClass;
    }

    /**
     * 缺省形态（原 asObject 尾段）：原值 null 走缺省，否则宽松转换。
     */
    static <T> T convertOrNull(Object rawValue, T defaultValue, Class<T> valueClass) {
        if (rawValue == null) {
            return defaultValue;
        }
        return convertTo(rawValue, boxed(valueClass));
    }

    /**
     * 非空形态（原 asNotNullObject 尾段）：原值 null 抛含键名空指针，否则宽松转换。
     */
    static <T> T convertRequired(Object rawValue, String key, Class<T> valueClass) {
        if (rawValue == null) {
            throw new NullPointerException("[" + key + "] value is null");
        }
        return convertTo(rawValue, boxed(valueClass));
    }

    /**
     * 条件消费形态（原 getNotNullToFunction 尾段）：仅原值存在时转换并调用 function。
     */
    static <T> void convertIfPresent(Object rawValue, Class<T> valueClass, Function<T, ?> function) {
        if (rawValue != null) {
            function.apply(convertTo(rawValue, boxed(valueClass)));
        }
    }

}
