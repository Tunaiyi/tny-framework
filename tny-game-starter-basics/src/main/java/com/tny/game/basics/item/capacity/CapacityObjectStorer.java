/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

import java.util.Collection;
import java.util.stream.*;

/**
 * Created by Kun Yang on 2017/7/17.
 */
public interface CapacityObjectStorer extends CapacityObjectQuerier {

    /**
     * 关联supplier (引用)
     *
     * @param supplier 存储能力值提供器
     */
    default void linkSupplier(CapacitySupplier supplier) {
        linkSupplier(supplier, 0);
    }

    /**
     * 关联suppliers (引用)
     *
     * @param suppliers 能力值提供器列表
     */
    default void linkSupplier(Collection<? extends CapacitySupplier> suppliers) {
        linkSupplier(suppliers, 0);
    }

    /**
     * 关联supplier (引用)
     *
     * @param supplier 存储能力值提供器
     * @param expireAt 过期时间
     */
    void linkSupplier(CapacitySupplier supplier, long expireAt);

    /**
     * 关联suppliers (引用)
     *
     * @param suppliers 能力值提供器列表
     * @param expireAt  过期时间
     */
    void linkSupplier(Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储supplier (复制)
     *
     * @param supplier 存储能力值提供器
     */
    default void saveSupplier(CapacitySupplier supplier) {
        saveSupplier(supplier, 0);
    }

    /**
     * 存储指定accessor (复制)
     *
     * @param id       accessor所属ID
     * @param modelId  accessor所属modelId
     * @param accessor accessor
     */
    default void saveSupplier(CapacitySupplierType type, long id, int modelId, CapacitySupply accessor) {
        saveSupplier(type, id, modelId, accessor, 0);
    }

    /**
     * 存储指定accessor (复制)
     *
     * @param item     accessor所属Item
     * @param accessor accessor
     */
    default void saveSupplier(CapacitySupplierType type, Item<?> item, CapacitySupply accessor) {
        this.saveSupplier(type, item.getId(), item.getModelId(), accessor, 0);
    }

    /**
     * 存储指定accessor (复制)
     *
     * @param item     accessor所属Item
     * @param accessor accessor
     */
    default <S extends CapacitySupplier & Item<?>> void saveSupplier(S item, CapacitySupply accessor) {
        this.saveSupplier(item, accessor, 0);
    }

    /**
     * Supplier (复制)
     *
     * @param item CapacitySupplierItem
     */
    default void saveSupplier(CapacitySupplierItem<?> item) {
        this.saveSupplier(item, 0);
    }

    /**
     * 存储suppliers (复制)
     *
     * @param suppliers 存储能力值提供器列表
     */
    default void saveSupplier(Collection<? extends CapacitySupplier> suppliers) {
        saveSupplier(suppliers, 0);
    }

    /**
     * 存储supplier (复制)
     *
     * @param id        supplier ID
     * @param modelId   supplier ItemID
     * @param suppliers 依赖列表
     */
    default void saveCompositeSupplier(CapacitySupplierType type, long id, int modelId, Collection<? extends CapacitySupplier> suppliers) {
        saveCompositeSupplier(type, id, modelId, suppliers, 0);
    }

    /**
     * 存储指定accessor (复制)
     *
     * @param item      accessor所属Item
     * @param suppliers 依赖列表
     */
    default void saveCompositeSupplier(CapacitySupplierType type, Item<?> item, Collection<? extends CapacitySupplier> suppliers) {
        this.saveCompositeSupplier(type, item, suppliers, 0);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item Supplier所属Item
     */
    default <S extends CompositeCapacitySupplier & Item<?>> void saveCompositeSupplier(S item) {
        this.saveCompositeSupplier(item, 0);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item      Supplier所属Item
     * @param suppliers 依赖列表
     */
    default <S extends CapacitySupplier & Item<?>> void saveCompositeSupplier(S item, Collection<? extends CapacitySupplier> suppliers) {
        this.saveCompositeSupplier(item.getSupplierType(), item.getId(), item.getModelId(), suppliers, 0);
    }

    /**
     * 存储supplier (复制)
     *
     * @param supplier 存储能力值提供器
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveSupplier(CapacitySupplier supplier, long expireAt);

    /**
     * 存储指定accessor (复制)
     *
     * @param id       accessor所属ID
     * @param modelId  accessor所属ItemID
     * @param accessor accessor
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveSupplier(CapacitySupplierType type, long id, int modelId, CapacitySupply accessor, long expireAt);

    /**
     * 存储指定accessor (复制)
     *
     * @param item     accessor所属Item
     * @param accessor accessor
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveSupplier(CapacitySupplierType type, Item<?> item, CapacitySupply accessor, long expireAt) {
        this.saveSupplier(type, item.getId(), item.getModelId(), accessor, expireAt);
    }

    /**
     * 存储指定accessor (复制)
     *
     * @param item     accessor所属Item
     * @param accessor accessor
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default <S extends CapacitySupplier & Item<?>> void saveSupplier(S item, CapacitySupply accessor, long expireAt) {
        this.saveSupplier(item.getSupplierType(), item.getId(), item.getModelId(), accessor, expireAt);
    }

    /**
     * Supplier (复制)
     *
     * @param item     CapacitySupplierItem
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveSupplier(CapacitySupplierItem<?> item, long expireAt) {
        this.saveSupplier(item, item, expireAt);
    }

    /**
     * 存储suppliers (复制)
     *
     * @param suppliers 存储能力值提供器列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveSupplier(Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储supplier (复制)
     *
     * @param id        accessor所属ID
     * @param modelId   accessor所属ItemID
     * @param suppliers 依赖列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveCompositeSupplier(CapacitySupplierType type, long id, int modelId, Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储指定accessor (复制)
     *
     * @param item      accessor所属Item
     * @param suppliers 依赖列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveCompositeSupplier(CapacitySupplierType type, Item<?> item, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        this.saveCompositeSupplier(type, item.getId(), item.getModelId(), suppliers, expireAt);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item     Supplier所属Item
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default <S extends CompositeCapacitySupplier & Item<?>> void saveCompositeSupplier(S item, long expireAt) {
        this.saveCompositeSupplier(item.getSupplierType(), item.getId(), item.getModelId(), item.suppliers(), expireAt);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item      Supplier所属Item
     * @param suppliers 依赖列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default <S extends CapacitySupplier & Item<?>> void saveCompositeSupplier(S item, Collection<? extends CapacitySupplier> suppliers,
            long expireAt) {
        this.saveCompositeSupplier(item.getSupplierType(), item.getId(), item.getModelId(), suppliers, expireAt);
    }

    /**
     * 设置失效时间点
     *
     * @param id       accessor所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtSupplier(long id, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param ids      accessor所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtSuppliers(Collection<Long> ids, long expireAt);

    /**
     * 设置有效时间长
     *
     * @param id     accessor所属ID
     * @param expire 时长
     */
    default void expireSupplier(long id, long expire) {
        expireAtSupplier(id, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 设置有效时间长
     *
     * @param ids    accessor所属ID
     * @param expire 时长
     */
    default void expireSuppliers(Collection<Long> ids, long expire) {
        expireAtSuppliers(ids, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 删除supplier
     *
     * @param supplier 删除能力值提供器
     */
    default void deleteSupplier(CapacitySupplier supplier) {
        deleteSupplierById(supplier.getId());
    }

    /**
     * 删除suppliers
     *
     * @param suppliers 删除能力值提供器列表
     */
    default void deleteSuppliers(Collection<? extends CapacitySupplier> suppliers) {
        deleteSuppliersById(suppliers.stream()
                .map(CapacitySupplier::getId)
                .collect(Collectors.toList()));
    }

    /**
     * 删除supplier
     *
     * @param id 删除能力值提供器ID
     */
    void deleteSupplierById(long id);

    /**
     * 删除suppliers
     *
     * @param ids 删除能力值提供器ID列表
     */
    void deleteSuppliersById(Collection<Long> ids);

    /**
     * 存储Capabler (复制)
     *
     * @param capabler 存储能力值目标
     */
    default void saveCapabler(Capabler capabler) {
        saveCapabler(capabler, 0);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param id          grather所属ID
     * @param modelId     grather所属ItemID
     * @param composition grather
     */
    default void saveCapabler(long id, int modelId, CapableComposition composition) {
        saveCapabler(id, modelId, composition, 0);
    }

    /**
     * 存储Capablers (复制)
     *
     * @param goals 存储能力值目标列表
     */
    default void saveCapabler(Collection<? extends Capabler> goals) {
        saveCapabler(goals, 0);
    }

    /**
     * 存储Capablers (复制)
     *
     * @param id        目标ID
     * @param modelId   目标ItemID
     * @param suppliers 提供器列表
     */
    default void saveCapabler(long id, int modelId, Collection<? extends CapacitySupplier> suppliers) {
        saveCapabler(id, modelId, suppliers, 0);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param item        grather所属item
     * @param composition grather
     */
    default void saveCapabler(Item<?> item, CapableComposition composition) {
        saveCapabler(item, composition, 0);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param item grather所属item
     */
    default void saveCapabler(BaseCapablerItem<?> item) {
        saveCapabler(item, 0);
    }

    /**
     * 存储Capabler (复制)
     *
     * @param item      CapablerItem
     * @param suppliers 提供器列表
     */
    default void saveCapabler(Item<?> item, Collection<? extends CapacitySupplier> suppliers) {
        saveCapabler(item, suppliers, 0);
    }

    /**
     * 存储Capabler (复制)
     *
     * @param goal     存储能力值目标
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveCapabler(Capabler goal, long expireAt);

    /**
     * 存储指定grather (复制)
     *
     * @param id          grather所属ID
     * @param modelId     grather所属ItemID
     * @param composition grather
     * @param expireAt    失效时间
     *                    >0是到期失效,
     *                    <0时永久有效,
     *                    =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveCapabler(long id, int modelId, CapableComposition composition, long expireAt);

    /**
     * 存储Capablers (复制)
     *
     * @param id        目标ID
     * @param modelId   目标ItemID
     * @param suppliers 提供器列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveCapabler(long id, int modelId, Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储指定grather (复制)
     *
     * @param item        grather所属item
     * @param composition grather
     * @param expireAt    失效时间
     *                    >0是到期失效,
     *                    <0时永久有效,
     *                    =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveCapabler(Item<?> item, CapableComposition composition, long expireAt) {
        saveCapabler(item.getId(), item.getModelId(), composition, expireAt);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param item     grather所属item
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveCapabler(BaseCapablerItem<?> item, long expireAt) {
        saveCapabler(item.getId(), item.getModelId(), item.suppliers(), expireAt);
    }

    /**
     * 存储Capabler (复制)
     *
     * @param item      CapablerItem
     * @param suppliers 提供器列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveCapabler(Item<?> item, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        saveCapabler(item.getId(), item.getModelId(), suppliers, expireAt);
    }

    /**
     * 存储Capablers (复制)
     *
     * @param capablers 存储能力值目标列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveCapabler(Collection<? extends Capabler> capablers, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param id       capabler所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtCapabler(long id, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param ids      grather所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtCapablers(Collection<Long> ids, long expireAt);

    /**
     * 设置有效时间长
     *
     * @param id     grather所属ID
     * @param expire 时长
     */
    default void expireCapabler(long id, long expire) {
        expireAtCapabler(id, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 设置有效时间长
     *
     * @param ids    grather所属ID
     * @param expire 时长
     */
    default void expireCapablers(Collection<Long> ids, long expire) {
        expireAtCapablers(ids, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 删除goal
     *
     * @param goal 删除能力值目标
     */
    default void deleteCapabler(Capabler goal) {
        deleteCapablerById(goal.getId());
    }

    /**
     * 删除goals
     *
     * @param goals 删除能力值目标列表
     */
    default void deleteCapablers(Collection<? extends Capabler> goals) {
        deleteCapablersById(goals.stream()
                .map(Capabler::getId)
                .collect(Collectors.toList()));
    }

    /**
     * 删除goal
     *
     * @param goalID 删除能力值目标ID
     */
    void deleteCapablerById(long goalID);

    /**
     * 删除goals
     *
     * @param goalIDs 删除能力值目标ID列表
     */
    void deleteCapablersById(Collection<Long> goalIDs);

    Stream<CapacitySupplier> getAllSuppliersSteam();

    Stream<Capabler> getAllCapablersSteam();

}