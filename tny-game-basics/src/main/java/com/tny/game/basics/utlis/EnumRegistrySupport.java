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

package com.tny.game.basics.utlis;

import com.tny.game.common.enums.*;

import java.util.*;

/**
 * <p>
 * 注册表族泛型中间层——承载 {@link EnumeratorHolder} 的转发六件套（of/check(String)/check(int)/option/all/enumerator）、
 * register 骨架与两式严格失败消息模板（"获取 {} X 不存在" / "获取 ID为 {} 的 X 不存在"），
 * 为 basics 注册表门面族（item：Behaviors/Actions/Abilities/DemandParams/DemandTypes/ItemTypes，
 * mould：Features/Moulds）提供单一事实源。
 * <p>
 * 形态取舍：各门面的 public static 签名逐字冻结（声明、界、描述符均不动），故本层按组合复用而非继承承载
 * （P8"继承复用代码、组合复用行为"；门面 extends ClassImporter 的既有层级与 holder 字段签名完全不动）；
 * 门面各方法体为本层一行薄委托。装载入口 {@code createSelector(Xxx::register)} 消费形态不动。
 *
 * @author : kgtny
 * @date : 2026/10/1
 */
public final class EnumRegistrySupport {

    private EnumRegistrySupport() {
    }

    /**
     * 注册骨架——等价原 {@code holder.register(value)} 一行实现（含 postRegister 扩展点现状）。
     *
     * @param holder 门面自有 holder（字段签名不动，由门面传入）
     * @param value 注册值
     */
    public static <E extends Enumerable<?>> void register(EnumeratorHolder<E> holder, E value) {
        holder.register(value);
    }

    /**
     * 严格查找（身份名通道）：未命中抛 NullPointerException，消息为现状模板"获取 {} {label} 不存在"。
     *
     * @param holder 门面自有 holder
     * @param key 身份名
     * @param label 族类标签（如 "Behavior"）
     * @return 命中值
     */
    public static <E extends Enumerable<?>, T extends E> T check(EnumeratorHolder<E> holder, String key, String label) {
        return holder.check(key, "获取 {} " + label + " 不存在", key);
    }

    /**
     * 严格查找（数字标识通道）：未命中抛 NullPointerException，消息为现状模板"获取 ID为 {} 的 {label} 不存在"。
     *
     * @param holder 门面自有 holder
     * @param id 数字标识
     * @param label 族类标签
     * @return 命中值
     */
    public static <E extends Enumerable<?>, T extends E> T check(EnumeratorHolder<E> holder, int id, String label) {
        return holder.check(id, "获取 ID为 {} 的 " + label + " 不存在", id);
    }

    /**
     * 宽松查找：未命中返回 null（原 of 通道现状语义）。
     *
     * @param holder 门面自有 holder
     * @param key 查找键（身份名或数字标识）
     * @return 命中值或 null
     */
    public static <E extends Enumerable<?>, T extends E> T of(EnumeratorHolder<E> holder, Object key) {
        return holder.of(key);
    }

    /**
     * 宽松包装：未命中为空 Optional。
     *
     * @param holder 门面自有 holder
     * @param key 查找键
     * @return Optional 包装
     */
    public static <E extends Enumerable<?>, T extends E> Optional<T> option(EnumeratorHolder<E> holder, Object key) {
        return holder.option(key);
    }

    /**
     * 全部已注册值（现状语义：不可修改视图）。
     *
     * @param holder 门面自有 holder
     * @return 全部值的不可修改集合视图
     */
    public static <E extends Enumerable<?>, T extends E> Collection<T> all(EnumeratorHolder<E> holder) {
        return holder.allValues();
    }

    /**
     * 暴露 holder 本体作 {@link Enumerator} 读接口（现状：返回同一实例）。
     *
     * @param holder 门面自有 holder
     * @return 同一 holder
     */
    public static <E extends Enumerable<?>> Enumerator<E> enumerator(EnumeratorHolder<E> holder) {
        return holder;
    }

}
