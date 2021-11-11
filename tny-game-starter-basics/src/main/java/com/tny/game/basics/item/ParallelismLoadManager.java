package com.tny.game.basics.item;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 并发读取Manager
 * Created by Kun Yang on 16/4/22.
 */
public abstract class ParallelismLoadManager<O> extends GameCacheManager<O> {

	private final String tableHead;

	private final int groupSize;

	private static final ForkJoinPool forkJoinPool = ForkJoinPool.getCommonPoolParallelism() >= 100 ?
			ForkJoinPool.commonPool() : new ForkJoinPool(20);

	private boolean parallelism;

	protected ParallelismLoadManager(Class<? extends O> entityClass, String tableHead) {
		this(entityClass, null, tableHead, 75);
	}

	protected ParallelismLoadManager(Class<? extends O> entityClass, String tableHead, int groupSize) {
		super(entityClass, null);
		this.tableHead = tableHead;
		this.groupSize = groupSize <= 0 ? 75 : groupSize;
	}

	protected ParallelismLoadManager(Class<? extends O> entityClass, Consumer<O> consumer, String tableHead) {
		this(entityClass, consumer, tableHead, 75);
	}

	protected ParallelismLoadManager(Class<? extends O> entityClass, Consumer<O> consumer, String tableHead, int groupSize) {
		super(entityClass, consumer);
		this.tableHead = tableHead;
		this.groupSize = groupSize <= 0 ? 75 : groupSize;
	}

	protected abstract List<AnyUnid> findKeys(String tableHead, long playerId);

	protected abstract List<AnyUnid> findAllKeys(String tableHead);

	protected Collection<O> getObjects(long playerId) {
		List<AnyUnid> keys = this.findKeys(this.tableHead, playerId);
		if (keys.isEmpty()) {
			return Collections.emptyList();
		}
		return gets(keys, false);
	}

	protected Collection<O> getAllObjects() {
		List<AnyUnid> keys = this.findAllKeys(this.tableHead);
		if (keys.isEmpty()) {
			return Collections.emptyList();
		}
		return gets(keys, false);
	}

	protected Collection<O> getObjects(long playerId, boolean parallelism) {
		List<AnyUnid> keys = this.findKeys(this.tableHead, playerId);
		if (keys.isEmpty()) {
			return Collections.emptyList();
		}
		return gets(keys, parallelism);
	}

	protected Collection<O> getAllObjects(boolean parallelism) {
		List<AnyUnid> keys = this.findAllKeys(this.tableHead);
		if (keys.isEmpty()) {
			return Collections.emptyList();
		}
		return gets(keys, parallelism);
	}

	private Collection<O> doGet(Collection<AnyUnid> keys, boolean parallelism) {
		List<List<AnyUnid>> keysList = keys.parallelStream()
				.collect(() -> {
							List<List<AnyUnid>> newList = new ArrayList<>();
							newList.add(new ArrayList<>());
							return newList;
						},
						(list, v) -> {
							List<AnyUnid> lastList = list.get(list.size() - 1);
							if (lastList.size() >= this.groupSize) {
								lastList = new ArrayList<>();
								list.add(lastList);
							}
							lastList.add(v);
						},
						List::addAll);
		Stream<List<AnyUnid>> keySteam = parallelism ? keysList.parallelStream() : keysList.stream();
		return keySteam
				.map(this::getByKeys)
				.reduce(new ConcurrentLinkedQueue<>(), (total, value) -> {
					if (total != value) {
						total.addAll(value);
					}
					return total;
				});
	}

	protected Collection<O> gets(Collection<AnyUnid> keys) {
		return gets(keys, false);
	}

	protected Collection<O> gets(Collection<AnyUnid> keys, boolean parallelism) {
		if (parallelism) {
			return forkJoinPool.submit(() -> doGet(keys, true)).join();
		} else {
			return doGet(keys, false);
		}
	}

}
