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
public class BaseCapacityStorer implements CapacityObjectStorer {

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapacitySupplier>>
			ON_SAVE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onSaveSupplier);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapacitySupplier>>
			ON_DELETE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onDeleteSupplier);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapacitySupplier>>
			ON_LINK_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onLinkSupplier);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapacitySupplier>>
			ON_EXPIRE_SUPPLIER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onExpireSupplier);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapabler>>
			ON_SAVE_CAPABLER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onSaveCapabler);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapabler>>
			ON_DELETE_CAPABLER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onDeleteCapabler);

	private static BindP1EventBus<CapacityStorerListener, CapacityObjectStorer, Collection<ExpireCapabler>>
			ON_EXPIRE_CAPABLER = EventBuses.of(CapacityStorerListener.class, CapacityStorerListener::onExpireCapabler);

	/**
	 * 玩家ID
	 */
	private long playerId;

	protected BaseCapacityStorer(long playerId) {
		this.playerId = playerId;
	}

	private Map<String, CopyOnWriteMap<Long, StoreCapacitySupplier>> typeSuppliersMap
			= new CopyOnWriteMap<>();

	private Map<String, CopyOnWriteMap<Long, StoreCapabler>> typeCapablerMap
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
	public Optional<CompositeCapacitySupplier> findCompositeSupplier(long id) {
		CapacitySupplier supplier = getSupplier(id);
		if (supplier == null) {
			return Optional.empty();
		}
		if (supplier instanceof CompositeCapacitySupplier) {
			return Optional.of(ObjectAide.as(supplier));
		}
		return Optional.empty();
	}

	@Override
	public Optional<Capabler> findCapabler(long id) {
		return Optional.ofNullable(getCapabler(id));
	}

	@Override
	public boolean isHasSupplier(long id) {
		return suppliersMap(id).containsKey(id);
	}

	@Override
	public boolean isHasCapabler(long id) {
		return capablerMap(id).containsKey(id);
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
	public void saveSupplier(CapacitySupplierType type, long id, int modelId, CapacitySupply supply, long timeoutAt) {
		StoreCapacitySupplier saved;
		suppliersMap(id)
				.put(id, saved = supplier2Save(type, id, modelId, supply, timeoutAt));
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
	public void saveCompositeSupplier(CapacitySupplierType type, long id, int modelId, Collection<? extends CapacitySupplier> suppliers,
			long timeoutAt) {
		StoreCapacitySupplier saved;
		suppliersMap(id)
				.put(id, saved = comboSupplier2Save(type, id, modelId, suppliers, timeoutAt));
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
	public void saveCapabler(Capabler goal, long timeoutAt) {
		StoreCapabler saved;
		capablerMap(goal.getId())
				.put(goal.getId(), saved = goal2Save(goal, timeoutAt));
		ON_SAVE_CAPABLER.notify(this, singleton(saved));
	}

	@Override
	public void saveCapabler(long id, int modelId, CapableComposition composition, long timeoutAt) {
		StoreCapabler saved;
		capablerMap(id)
				.put(id, saved = goal2Save(id, modelId, composition, timeoutAt));
		ON_SAVE_CAPABLER.notify(this, singleton(saved));
	}

	@Override
	public void saveCapabler(long id, int modelId, Collection<? extends CapacitySupplier> suppliers, long timeoutAt) {
		StoreCapabler saved;
		capablerMap(id)
				.put(id, saved = goal2Save(id, modelId, suppliers, timeoutAt));
		ON_SAVE_CAPABLER.notify(this, singleton(saved));
	}

	@Override
	public void saveCapabler(Collection<? extends Capabler> capablers, long timeoutAt) {
		Map<String, Map<Long, StoreCapabler>> map =
				capablers.stream().collect(Collectors.groupingBy(
						g -> key(g.getId()),
						Collectors.toMap(
								Capabler::getId,
								g -> goal2Save(g, timeoutAt))));
		List<ExpireCapabler> saved = new ArrayList<>();
		map.forEach((k, m) -> {
			goalsMap(k).putAll(m);
			saved.addAll(m.values());
		});
		if (!saved.isEmpty()) {
			ON_SAVE_CAPABLER.notify(this, saved);
		}
	}

	@Override
	public void expireAtCapabler(long id, long expireAt) {
		StoreCapabler expire = capablerMap(id).get(id);
		if (expire != null) {
			expire.expireAt(expireAt);
			ON_EXPIRE_CAPABLER.notify(this, singleton(expire));
		}
	}

	@Override
	public void expireAtCapablers(Collection<Long> ids, long expireAt) {
		List<ExpireCapabler> expired = ids.stream()
				.map(id -> capablerMap(id).get(id))
				.filter(Objects::nonNull)
				.peek(e -> e.expireAt(expireAt))
				.collect(Collectors.toList());
		if (!expired.isEmpty()) {
			ON_EXPIRE_CAPABLER.notify(this, expired);
		}
	}

	@Override
	public void deleteCapablerById(long id) {
		StoreCapabler goal = capablerMap(id).remove(id);
		if (goal != null) {
			ON_DELETE_CAPABLER.notify(this, singleton(goal));
		}
	}

	@Override
	public void deleteCapablersById(Collection<Long> goalIDs) {
		Map<String, List<Long>> map = goalIDs.stream()
				.collect(Collectors.groupingBy(this::key));
		List<ExpireCapabler> deleted = map.entrySet().stream()
				.map(e -> goalsMap(e.getKey()).removeAll(e.getValue()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (!deleted.isEmpty()) {
			ON_EXPIRE_CAPABLER.notify(this, deleted);
		}
	}

	private StoreCapacitySupplier supplier2Link(CapacitySupplier supplier, long expireAt) {
		return StoreCapacitySupplier.linkBySupplier(supplier, expireAt);
	}

	private StoreCapabler goal2Save(Capabler goal, long expireAt) {
		if (goal instanceof StoreByCopyCapabler) {
			return ObjectAide.as(goal);
		}
		return StoreCapabler.saveByCapabler(goal, this, expireAt);
	}

	private StoreCapabler goal2Save(long id, int modelId, CapableComposition composition, long expireAt) {
		return StoreCapabler.saveByComposition(id, modelId, composition, this, expireAt);
	}

	private StoreCapabler goal2Save(long id, int modelId, Collection<? extends CapacitySupplier> suppliers, long expireAt) {
		return StoreCapabler.saveBySuppliers(id, modelId, suppliers.stream(), this, expireAt);
	}

	private StoreCapacitySupplier supplier2Save(CapacitySupplier supplier, long expireAt) {
		if (supplier instanceof CompositeCapacitySupplier) {
			return StoreCapacitySupplier.saveBySupplier(ObjectAide.as(supplier, CompositeCapacitySupplier.class), this, expireAt);
		} else {
			return StoreCapacitySupplier.saveBySupplier(supplier, expireAt);
		}
	}

	private StoreCapacitySupplier supplier2Save(CapacitySupplierType type, long id, int modelId, CapacitySupply supply, long expireAt) {
		return StoreCapacitySupplier.saveBySupply(type, id, modelId, this.getPlayerId(), supply, expireAt);
	}

	private StoreCapacitySupplier comboSupplier2Save(CapacitySupplierType type, long id, int modelId,
			Collection<? extends CapacitySupplier> supplier,
			long expireAt) {
		return StoreCapacitySupplier.saveByDependSuppliers(type, id, modelId, supplier.stream(), this, expireAt);
	}

	private String key(long id) {
		return String.valueOf(id).substring(0, 4);
	}

	private CopyOnWriteMap<Long, StoreCapacitySupplier> suppliersMap(String key) {
		return this.typeSuppliersMap.computeIfAbsent(key, k -> new CopyOnWriteMap<>());
	}

	private CopyOnWriteMap<Long, StoreCapabler> goalsMap(String key) {
		return this.typeCapablerMap.computeIfAbsent(key, k -> new CopyOnWriteMap<>());
	}

	private CopyOnWriteMap<Long, StoreCapacitySupplier> suppliersMap(long id) {
		return this.typeSuppliersMap.computeIfAbsent(key(id), k -> new CopyOnWriteMap<>());
	}

	private CopyOnWriteMap<Long, StoreCapabler> capablerMap(long id) {
		return this.typeCapablerMap.computeIfAbsent(key(id), k -> new CopyOnWriteMap<>());
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

	private Capabler getCapabler(long id) {
		StoreCapabler capabler = capablerMap(id).get(id);
		if (capabler == null) {
			return null;
		}
		if (capabler.isExpire()) {
			return null;
		}
		return capabler;
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
	public Stream<Capabler> getAllCapablersSteam() {
		return this.typeCapablerMap.values().stream()
				.flatMap(goals -> goals.values().stream());
	}

	public Stream<StoreCapacitySupplier> getStoreSuppliersSteam() {
		return this.typeSuppliersMap.values().stream()
				.flatMap(suppliers -> suppliers.values().stream());
	}

	public Stream<StoreCapabler> getStoreCapablersSteam() {
		return this.typeCapablerMap.values().stream()
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

	protected BaseCapacityStorer addStoreCapablers(Stream<StoreCapabler> goals) {
		Map<String, Map<Long, StoreCapabler>> map =
				goals.collect(Collectors.groupingBy(
						g -> key(g.getId()),
						Collectors.toMap(
								StoreCapabler::getId,
								ObjectAide::self)));
		map.forEach((k, m) -> goalsMap(k).putAll(m));
		return this;
	}

}
