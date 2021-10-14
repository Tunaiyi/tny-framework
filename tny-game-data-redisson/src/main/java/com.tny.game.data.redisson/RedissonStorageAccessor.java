package com.tny.game.data.redisson;

import com.google.common.collect.ImmutableSet;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.redisson.*;
import org.redisson.api.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:15 下午
 */
public class RedissonStorageAccessor<K extends Comparable<?>, O> implements StorageAccessor<K, O> {

	private final String table;

	private final TypedRedisson<O> redisson;

	private final EntityKeyMaker<K, O> keyMaker;

	public RedissonStorageAccessor(String table, TypedRedisson<O> redisson, EntityKeyMaker<K, O> keyMaker) {
		this.table = table;
		this.redisson = redisson;
		this.keyMaker = keyMaker;
	}

	@Override
	public O get(K id) {
		return redisson.getMap(table).get(id);
	}

	@Override
	public List<O> get(Collection<? extends K> keys) {
		return new ArrayList<>(redisson.<K>getMap(table).getAll(ImmutableSet.copyOf(keys)).values());
	}

	@Override
	public boolean insert(O object) {
		return redisson.<K>getMap(table).fastPutIfAbsent(keyMaker.make(object), object);
	}

	@Override
	public Collection<O> insert(Collection<O> objects) {
		Collection<O> failureList = new ArrayList<>();
		RMapAsync<K, O> mapAsync = redisson.getMap(table);
		Map<RFuture<Boolean>, O> futures = new HashMap<>();
		for (O object : objects) {
			futures.put(mapAsync.fastPutIfAbsentAsync(keyMaker.make(object), object), object);
		}
		for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
			RFuture<Boolean> future = entry.getKey();
			if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
				failureList.add(entry.getValue());
			}
		}
		return failureList;
	}

	@Override
	public boolean update(O object) {
		return redisson.<K>getMap(table).fastPutIfExists(keyMaker.make(object), object);
	}

	@Override
	public boolean save(O object) {
		return redisson.getMap(table).fastPut(keyMaker.make(object), object);
	}

	@Override
	public Collection<O> save(Collection<O> objects) {
		Collection<O> failureList = new ArrayList<>();
		RMapAsync<K, O> mapAsync = redisson.getMap(table);
		Map<RFuture<Boolean>, O> futures = new HashMap<>();
		for (O object : objects) {
			futures.put(mapAsync.fastPutAsync(keyMaker.make(object), object), object);
		}
		for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
			RFuture<Boolean> future = entry.getKey();
			if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
				failureList.add(entry.getValue());
			}
		}
		return failureList;
	}

	@Override
	public Collection<O> update(Collection<O> objects) {
		Collection<O> failureList = new ArrayList<>();
		RMapAsync<K, O> mapAsync = redisson.getMap(table);
		Map<RFuture<Boolean>, O> futures = new HashMap<>();
		for (O object : objects) {
			futures.put(mapAsync.fastPutIfExistsAsync(keyMaker.make(object), object), object);
		}
		for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
			RFuture<Boolean> future = entry.getKey();
			if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
				failureList.add(entry.getValue());
			}
		}
		return failureList;
	}

	@Override
	public void delete(O object) {
		redisson.getMap(table)
				.fastRemove(keyMaker.make(object));
	}

	@Override
	public void delete(Collection<O> objects) {
		for (O object : objects) {
			redisson.getMap(table).fastRemove(keyMaker.make(object));
		}
	}

}
