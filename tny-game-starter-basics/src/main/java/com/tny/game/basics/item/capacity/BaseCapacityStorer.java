package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.capacity.event.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.utils.*;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;

/**
 * 默认能力值存储器
 * <p>
 * Created by Kun Yang on 2017/7/18.
 */
public class BaseCapacityStorer implements CapacityStorer {

    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacitySupplier>>
            ON_SAVE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onSaveSupplier);
    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacitySupplier>>
            ON_DELETE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onDeleteSupplier);
    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacitySupplier>>
            ON_LINK_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onLinkSupplier);
    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacitySupplier>>
            ON_EXPIRE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onExpireSupplier);

    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacityGoal>>
            ON_SAVE_GOAL = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onSaveGoal);
    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacityGoal>>
            ON_DELETE_GOAL = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onDeleteGoal);
    private static BindP1EventBus<CapacityStorerListener, CapacityStorer, Collection<ExpireCapacityGoal>>
            ON_EXPIRE_GOAL = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onExpireGoal);

    /**
     * 玩家ID
     */
    private long playerId;

    protected BaseCapacityStorer(long playerId) {
        this.playerId = playerId;
    }

    private Map<String, CopyOnWriteMap<Long, StoreCapacitySupplier>> typeSuppliersMap
            = new CopyOnWriteMap<>();

    private Map<String, CopyOnWriteMap<Long, StoreCapacityGoal>> typeGoalsMap
            = new CopyOnWriteMap<>();

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public Optional<CapacitySupplier> findSupplier(long id) {
        return Optional.ofNullable(getSupplier(id));
    }

    @Override
    public Optional<ComboCapacitySupplier> findComboSupplier(long id) {
        CapacitySupplier supplier = getSupplier(id);
        if (supplier == null) {
            return Optional.empty();
        }
        if (supplier instanceof ComboCapacitySupplier) {
            return Optional.of(ObjectAide.as(supplier));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CapacityGoal> findGoal(long id) {
        return Optional.ofNullable(getGoal(id));
    }

    @Override
    public boolean isHasSupplier(long id) {
        return suppliersMap(id).containsKey(id);
    }

    @Override
    public boolean isHasGoal(long id) {
        return goalsMap(id).containsKey(id);
    }

    @Override
    public void linkSupplier(CapacitySupplier supplier, long timeoutAt) {
        StoreCapacitySupplier linked;
        suppliersMap(supplier.getId())
                .put(supplier.getId(), linked = supplier2Link(supplier, timeoutAt));
        ON_LINK_SUPPLIER.notify(this, singleton(linked));
    }

    @Override
    public void linkSupplier(Collection<? extends CapacitySupplier> suppliers, long timeoutAt) {
        Map<String, Map<Long, StoreCapacitySupplier>> map =
                suppliers.stream().collect(Collectors.groupingBy(
                        s -> key(s.getId()),
                        Collectors.toMap(
                                CapacitySupplier::getId,
                                s -> supplier2Link(s, timeoutAt))));
        List<ExpireCapacitySupplier> linked = new ArrayList<>();
        map.forEach((k, m) -> {
            suppliersMap(k).putAll(m);
            linked.addAll(m.values());
        });
        if (!linked.isEmpty()) {
            ON_LINK_SUPPLIER.notify(this, linked);
        }
    }

    @Override
    public void saveSupplier(CapacitySupplier supplier, long timeoutAt) {
        StoreCapacitySupplier saved;
        suppliersMap(supplier.getId())
                .put(supplier.getId(), saved = supplier2Save(supplier, timeoutAt));
        ON_SAVE_SUPPLIER.notify(this, singleton(saved));
    }

    @Override
    public void saveSupplier(CapacitySupplierType type, long id, int itemID, CapacitySupply supply, long timeoutAt) {
        StoreCapacitySupplier saved;
        suppliersMap(id)
                .put(id, saved = supplier2Save(type, id, itemID, supply, timeoutAt));
        ON_SAVE_SUPPLIER.notify(this, singleton(saved));
    }

    @Override
    public void saveSupplier(Collection<? extends CapacitySupplier> suppliers, long timeoutAt) {
        Map<String, Map<Long, StoreCapacitySupplier>> map =
                suppliers.stream().collect(Collectors.groupingBy(
                        s -> key(s.getId()),
                        Collectors.toMap(
                                CapacitySupplier::getId,
                                s -> supplier2Save(s, timeoutAt))));
        List<ExpireCapacitySupplier> saved = new ArrayList<>();
        map.forEach((k, m) -> {
            suppliersMap(k).putAll(m);
            saved.addAll(m.values());
        });
        if (!saved.isEmpty()) {
            ON_SAVE_SUPPLIER.notify(this, saved);
        }
    }

    @Override
    public void saveComboSupplier(CapacitySupplierType type, long id, int itemID, Collection<? extends CapacitySupplier> suppliers, long timeoutAt) {
        StoreCapacitySupplier saved;
        suppliersMap(id)
                .put(id, saved = comboSupplier2Save(type, id, itemID, suppliers, timeoutAt));
        ON_SAVE_SUPPLIER.notify(this, singleton(saved));
    }

    @Override
    public void expireAtSupplier(long id, long expireAt) {
        StoreCapacitySupplier expire = suppliersMap(id).get(id);
        if (expire != null) {
            expire.expireAt(expireAt);
            ON_EXPIRE_SUPPLIER.notify(this, singleton(expire));
        }
    }

    @Override
    public void expireAtSuppliers(Collection<Long> ids, long expireAt) {
        List<ExpireCapacitySupplier> expired = ids.stream()
                .map(id -> suppliersMap(id).get(id))
                .filter(Objects::nonNull)
                .peek(e -> e.expireAt(expireAt))
                .collect(Collectors.toList());
        if (!expired.isEmpty()) {
            ON_EXPIRE_SUPPLIER.notify(this, expired);
        }
    }

    @Override
    public void deleteSupplierById(long id) {
        StoreCapacitySupplier supplier = suppliersMap(id).remove(id);
        if (supplier != null) {
            ON_DELETE_SUPPLIER.notify(this, singleton(supplier));
        }
    }

    @Override
    public void deleteSuppliersById(Collection<Long> supplierIDs) {
        Map<String, List<Long>> map = supplierIDs.stream()
                .collect(Collectors.groupingBy(this::key));
        List<ExpireCapacitySupplier> deleted = map.entrySet().stream()
                .map(e -> suppliersMap(e.getKey()).removeAll(e.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (!deleted.isEmpty()) {
            ON_DELETE_SUPPLIER.notify(this, deleted);
        }
    }

    @Override
    public void saveGoal(CapacityGoal goal, long timeoutAt) {
        StoreCapacityGoal saved;
        goalsMap(goal.getId())
                .put(goal.getId(), saved = goal2Save(goal, timeoutAt));
        ON_SAVE_GOAL.notify(this, singleton(saved));
    }

    @Override
    public void saveGoal(long id, int itemID, CapacityGather gather, long timeoutAt) {
        StoreCapacityGoal saved;
        goalsMap(id)
                .put(id, saved = goal2Save(id, itemID, gather, timeoutAt));
        ON_SAVE_GOAL.notify(this, singleton(saved));
    }

    @Override
    public void saveGoal(long id, int itemID, Collection<? extends CapacitySupplier> suppliers, long timeoutAt) {
        StoreCapacityGoal saved;
        goalsMap(id)
                .put(id, saved = goal2Save(id, itemID, suppliers, timeoutAt));
        ON_SAVE_GOAL.notify(this, singleton(saved));
    }

    @Override
    public void saveGoal(Collection<? extends CapacityGoal> goals, long timeoutAt) {
        Map<String, Map<Long, StoreCapacityGoal>> map =
                goals.stream().collect(Collectors.groupingBy(
                        g -> key(g.getId()),
                        Collectors.toMap(
                                CapacityGoal::getId,
                                g -> goal2Save(g, timeoutAt))));
        List<ExpireCapacityGoal> saved = new ArrayList<>();
        map.forEach((k, m) -> {
            goalsMap(k).putAll(m);
            saved.addAll(m.values());
        });
        if (!saved.isEmpty()) {
            ON_SAVE_GOAL.notify(this, saved);
        }
    }

    @Override
    public void expireAtGoal(long id, long expireAt) {
        StoreCapacityGoal expire = goalsMap(id).get(id);
        if (expire != null) {
            expire.expireAt(expireAt);
            ON_EXPIRE_GOAL.notify(this, singleton(expire));
        }
    }

    @Override
    public void expireAtGoals(Collection<Long> ids, long expireAt) {
        List<ExpireCapacityGoal> expired = ids.stream()
                .map(id -> goalsMap(id).get(id))
                .filter(Objects::nonNull)
                .peek(e -> e.expireAt(expireAt))
                .collect(Collectors.toList());
        if (!expired.isEmpty()) {
            ON_EXPIRE_GOAL.notify(this, expired);
        }
    }

    @Override
    public void deleteGoalById(long id) {
        StoreCapacityGoal goal = goalsMap(id).remove(id);
        if (goal != null) {
            ON_DELETE_GOAL.notify(this, singleton(goal));
        }
    }

    @Override
    public void deleteGoalsById(Collection<Long> goalIDs) {
        Map<String, List<Long>> map = goalIDs.stream()
                .collect(Collectors.groupingBy(this::key));
        List<ExpireCapacityGoal> deleted = map.entrySet().stream()
                .map(e -> goalsMap(e.getKey()).removeAll(e.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (!deleted.isEmpty()) {
            ON_EXPIRE_GOAL.notify(this, deleted);
        }
    }

    private StoreCapacitySupplier supplier2Link(CapacitySupplier supplier, long expireAt) {
        return StoreCapacitySupplier.linkBySupplier(supplier, expireAt);
    }

    private StoreCapacityGoal goal2Save(CapacityGoal goal, long expireAt) {
        if (goal instanceof StoreByCopyCapacityGoal) {
            return ObjectAide.as(goal);
        }
        return StoreCapacityGoal.saveByGoal(goal, this, expireAt);
    }

    private StoreCapacityGoal goal2Save(long id, int itemID, CapacityGather gather, long expireAt) {
        return StoreCapacityGoal.saveByGather(id, itemID, gather, this, expireAt);
    }

    private StoreCapacityGoal goal2Save(long id, int itemID, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
        return StoreCapacityGoal.saveBySuppliers(id, itemID, suppliers.stream(), this, expireAt);
    }

    private StoreCapacitySupplier supplier2Save(CapacitySupplier supplier, long expireAt) {
        if (supplier instanceof ComboCapacitySupplier) {
            return StoreCapacitySupplier.saveBySupplier(ObjectAide.as(supplier, ComboCapacitySupplier.class), this, expireAt);
        } else {
            return StoreCapacitySupplier.saveBySupplier(supplier, expireAt);
        }
    }

    private StoreCapacitySupplier supplier2Save(CapacitySupplierType type, long id, int itemID, CapacitySupply supply, long expireAt) {
        return StoreCapacitySupplier.saveBySupply(type, id, itemID, this.getPlayerId(), supply, expireAt);
    }

    private StoreCapacitySupplier comboSupplier2Save(CapacitySupplierType type, long id, int itemID, Collection<? extends CapacitySupplier> supplier,
            long expireAt) {
        return StoreCapacitySupplier.saveByDependSuppliers(type, id, itemID, supplier.stream(), this, expireAt);
    }

    private String key(long id) {
        return String.valueOf(id).substring(0, 4);
    }

    private CopyOnWriteMap<Long, StoreCapacitySupplier> suppliersMap(String key) {
        return this.typeSuppliersMap.computeIfAbsent(key, k -> new CopyOnWriteMap<>());
    }

    private CopyOnWriteMap<Long, StoreCapacityGoal> goalsMap(String key) {
        return this.typeGoalsMap.computeIfAbsent(key, k -> new CopyOnWriteMap<>());
    }

    private CopyOnWriteMap<Long, StoreCapacitySupplier> suppliersMap(long id) {
        return this.typeSuppliersMap.computeIfAbsent(key(id), k -> new CopyOnWriteMap<>());
    }

    private CopyOnWriteMap<Long, StoreCapacityGoal> goalsMap(long id) {
        return this.typeGoalsMap.computeIfAbsent(key(id), k -> new CopyOnWriteMap<>());
    }

    private CapacitySupplier getSupplier(long id) {
        StoreCapacitySupplier supplier = suppliersMap(id).get(id);
        if (supplier == null) {
            return null;
        }
        if (supplier.isExpire()) {
            return null;
        }
        return supplier;
    }

    private CapacityGoal getGoal(long id) {
        StoreCapacityGoal goal = goalsMap(id).get(id);
        if (goal == null) {
            return null;
        }
        if (goal.isExpire()) {
            return null;
        }
        return goal;
    }

    protected BaseCapacityStorer setPlayerId(long playerId) {
        this.playerId = playerId;
        return this;
    }

    @Override
    public Stream<CapacitySupplier> getAllSuppliersSteam() {
        return this.typeSuppliersMap.values().stream()
                .flatMap(suppliers -> suppliers.values().stream());
    }

    @Override
    public Stream<CapacityGoal> getAllGoalsSteam() {
        return this.typeGoalsMap.values().stream()
                .flatMap(goals -> goals.values().stream());
    }

    public Stream<StoreCapacitySupplier> getStoreSuppliersSteam() {
        return this.typeSuppliersMap.values().stream()
                .flatMap(suppliers -> suppliers.values().stream());
    }

    public Stream<StoreCapacityGoal> getStoreGoalsSteam() {
        return this.typeGoalsMap.values().stream()
                .flatMap(goals -> goals.values().stream());
    }

    protected BaseCapacityStorer addStoreSuppliers(Stream<StoreCapacitySupplier> suppliers) {
        Map<String, Map<Long, StoreCapacitySupplier>> map =
                suppliers.collect(Collectors.groupingBy(
                        s -> key(s.getId()),
                        Collectors.toMap(
                                CapacitySupplier::getId,
                                ObjectAide::self)));
        map.forEach((k, m) -> suppliersMap(k).putAll(m));
        return this;
    }

    protected BaseCapacityStorer addStoreGoals(Stream<StoreCapacityGoal> goals) {
        Map<String, Map<Long, StoreCapacityGoal>> map =
                goals.collect(Collectors.groupingBy(
                        g -> key(g.getId()),
                        Collectors.toMap(
                                StoreCapacityGoal::getId,
                                ObjectAide::self)));
        map.forEach((k, m) -> goalsMap(k).putAll(m));
        return this;
    }

}
