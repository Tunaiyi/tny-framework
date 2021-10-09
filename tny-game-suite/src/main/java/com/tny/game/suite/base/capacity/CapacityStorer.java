package com.tny.game.suite.base.capacity;


import com.tny.game.basics.item.*;

import java.util.Collection;
import java.util.stream.*;

/**
 * Created by Kun Yang on 2017/7/17.
 */
public interface CapacityStorer extends CapacityVisitor {

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
     * 存储指定supply (复制)
     *
     * @param id     supply所属ID
     * @param itemID supply所属ItemID
     * @param supply supply
     */
    default void saveSupplier(CapacitySupplierType type, long id, int itemID, CapacitySupply supply) {
        saveSupplier(type, id, itemID, supply, 0);
    }

    /**
     * 存储指定supply (复制)
     *
     * @param item   supply所属Item
     * @param supply supply
     */
    default void saveSupplier(CapacitySupplierType type, Item<?> item, CapacitySupply supply) {
        this.saveSupplier(type, item.getId(), item.getItemId(), supply, 0);
    }

    /**
     * 存储指定supply (复制)
     *
     * @param item   supply所属Item
     * @param supply supply
     */
    default <S extends CapacitySupplier & Item<?>> void saveSupplier(S item, CapacitySupply supply) {
        this.saveSupplier(item, supply, 0);
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
     * @param itemID    supplier ItemID
     * @param suppliers 依赖列表
     */
    default void saveComboSupplier(CapacitySupplierType type, long id, int itemID, Collection<? extends CapacitySupplier> suppliers) {
        saveComboSupplier(type, id, itemID, suppliers, 0);
    }

    /**
     * 存储指定supply (复制)
     *
     * @param item      supply所属Item
     * @param suppliers 依赖列表
     */
    default void saveComboSupplier(CapacitySupplierType type, Item<?> item, Collection<? extends CapacitySupplier> suppliers) {
        this.saveComboSupplier(type, item, suppliers, 0);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item Supplier所属Item
     */
    default <S extends ComboCapacitySupplier & Item<?>> void saveComboSupplier(S item) {
        this.saveComboSupplier(item, 0);
    }

    /**
     * 存储指定Supplier (复制)
     *
     * @param item      Supplier所属Item
     * @param suppliers 依赖列表
     */
    default <S extends CapacitySupplier & Item<?>> void saveComboSupplier(S item, Collection<? extends CapacitySupplier> suppliers) {
        this.saveComboSupplier(item.getSupplierType(), item.getId(), item.getItemId(), suppliers, 0);
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
     * 存储指定supply (复制)
     *
     * @param id       supply所属ID
     * @param itemID   supply所属ItemID
     * @param supply   supply
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveSupplier(CapacitySupplierType type, long id, int itemID, CapacitySupply supply, long expireAt);

    /**
     * 存储指定supply (复制)
     *
     * @param item     supply所属Item
     * @param supply   supply
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveSupplier(CapacitySupplierType type, Item<?> item, CapacitySupply supply, long expireAt) {
        this.saveSupplier(type, item.getId(), item.getItemId(), supply, expireAt);
    }

    /**
     * 存储指定supply (复制)
     *
     * @param item     supply所属Item
     * @param supply   supply
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default <S extends CapacitySupplier & Item<?>> void saveSupplier(S item, CapacitySupply supply, long expireAt) {
        this.saveSupplier(item.getSupplierType(), item.getId(), item.getItemId(), supply, expireAt);
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
        this.saveSupplier(item, item.supply(), expireAt);
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
     * @param id        supply所属ID
     * @param itemID    supply所属ItemID
     * @param suppliers 依赖列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveComboSupplier(CapacitySupplierType type, long id, int itemID, Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储指定supply (复制)
     *
     * @param item      supply所属Item
     * @param suppliers 依赖列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveComboSupplier(CapacitySupplierType type, Item<?> item, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        this.saveComboSupplier(type, item.getId(), item.getItemId(), suppliers, expireAt);
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
    default <S extends ComboCapacitySupplier & Item<?>> void saveComboSupplier(S item, long expireAt) {
        this.saveComboSupplier(item.getSupplierType(), item.getId(), item.getItemId(), item.dependSuppliers(), expireAt);
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
    default <S extends CapacitySupplier & Item<?>> void saveComboSupplier(S item, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        this.saveComboSupplier(item.getSupplierType(), item.getId(), item.getItemId(), suppliers, expireAt);
    }

    /**
     * 设置失效时间点
     *
     * @param id       supply所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtSupplier(long id, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param ids      supply所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtSuppliers(Collection<Long> ids, long expireAt);

    /**
     * 设置有效时间长
     *
     * @param id     supply所属ID
     * @param expire 时长
     */
    default void expireSupplier(long id, long expire) {
        expireAtSupplier(id, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 设置有效时间长
     *
     * @param ids    supply所属ID
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
     * 存储goal (复制)
     *
     * @param goal 存储能力值目标
     */
    default void saveGoal(CapacityGoal goal) {
        saveGoal(goal, 0);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param id     grather所属ID
     * @param itemID grather所属ItemID
     * @param gather grather
     */
    default void saveGoal(long id, int itemID, CapacityGather gather) {
        saveGoal(id, itemID, gather, 0);
    }

    /**
     * 存储goals (复制)
     *
     * @param goals 存储能力值目标列表
     */
    default void saveGoal(Collection<? extends CapacityGoal> goals) {
        saveGoal(goals, 0);
    }

    /**
     * 存储goals (复制)
     *
     * @param id        目标ID
     * @param itemID    目标ItemID
     * @param suppliers 提供器列表
     */
    default void saveGoal(long id, int itemID, Collection<? extends CapacitySupplier> suppliers) {
        saveGoal(id, itemID, suppliers, 0);
    }


    /**
     * 存储指定grather (复制)
     *
     * @param item   grather所属item
     * @param gather grather
     */
    default void saveGoal(Item<?> item, CapacityGather gather) {
        saveGoal(item, gather, 0);
    }

    /**
     * 存储指定grather (复制)
     *
     * @param item grather所属item
     */
    default void saveGoal(CapacityGoalItem<?> item) {
        saveGoal(item, 0);
    }

    /**
     * 存储goal (复制)
     *
     * @param item      CapacityGoalItem
     * @param suppliers 提供器列表
     */
    default void saveGoal(Item<?> item, Collection<? extends CapacitySupplier> suppliers) {
        saveGoal(item, suppliers, 0);
    }

    /**
     * 存储goal (复制)
     *
     * @param goal     存储能力值目标
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveGoal(CapacityGoal goal, long expireAt);

    /**
     * 存储指定grather (复制)
     *
     * @param id       grather所属ID
     * @param itemID   grather所属ItemID
     * @param gather   grather
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveGoal(long id, int itemID, CapacityGather gather, long expireAt);

    /**
     * 存储goals (复制)
     *
     * @param id        目标ID
     * @param itemID    目标ItemID
     * @param suppliers 提供器列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveGoal(long id, int itemID, Collection<? extends CapacitySupplier> suppliers, long expireAt);

    /**
     * 存储指定grather (复制)
     *
     * @param item     grather所属item
     * @param gather   grather
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveGoal(Item<?> item, CapacityGather gather, long expireAt) {
        saveGoal(item.getId(), item.getItemId(), gather, expireAt);
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
    default void saveGoal(CapacityGoalItem<?> item, long expireAt) {
        saveGoal(item.getId(), item.getItemId(), item.gather(), expireAt);
    }

    /**
     * 存储goal (复制)
     *
     * @param item      CapacityGoalItem
     * @param suppliers 提供器列表
     * @param expireAt  失效时间
     *                  >0是到期失效,
     *                  <0时永久有效,
     *                  =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    default void saveGoal(Item<?> item, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        saveGoal(item.getId(), item.getItemId(), suppliers, expireAt);
    }

    /**
     * 存储goals (复制)
     *
     * @param goals    存储能力值目标列表
     * @param expireAt 失效时间
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void saveGoal(Collection<? extends CapacityGoal> goals, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param id       grather所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtGoal(long id, long expireAt);

    /**
     * 设置失效时间点
     *
     * @param ids      grather所属ID
     * @param expireAt 失效时间点
     *                 >0是到期失效,
     *                 <0时永久有效,
     *                 =0时更具对象是否实现ExpireCapacitiable, 如果有则以其getExpireAt为失效时间, 否则为-1
     */
    void expireAtGoals(Collection<Long> ids, long expireAt);

    /**
     * 设置有效时间长
     *
     * @param id     grather所属ID
     * @param expire 时长
     */
    default void expireGoal(long id, long expire) {
        expireAtGoal(id, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 设置有效时间长
     *
     * @param ids    grather所属ID
     * @param expire 时长
     */
    default void expireGoals(Collection<Long> ids, long expire) {
        expireAtGoals(ids, System.currentTimeMillis() + Math.min(0, expire));
    }

    /**
     * 删除goal
     *
     * @param goal 删除能力值目标
     */
    default void deleteGoal(CapacityGoal goal) {
        deleteGoalById(goal.getId());
    }

    /**
     * 删除goals
     *
     * @param goals 删除能力值目标列表
     */
    default void deleteGoals(Collection<? extends CapacityGoal> goals) {
        deleteGoalsById(goals.stream()
                             .map(CapacityGoal::getId)
                             .collect(Collectors.toList()));
    }

    /**
     * 删除goal
     *
     * @param goalID 删除能力值目标ID
     */
    void deleteGoalById(long goalID);

    /**
     * 删除goals
     *
     * @param goalIDs 删除能力值目标ID列表
     */
    void deleteGoalsById(Collection<Long> goalIDs);

    Stream<CapacitySupplier> getAllSuppliersSteam();

    Stream<CapacityGoal> getAllGoalsSteam();
}