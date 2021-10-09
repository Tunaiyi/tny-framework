package com.tny.game.basics.item;

import com.tny.game.data.*;
import org.slf4j.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class GameCacheManager<O> extends GameManager<O> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameCacheManager.class);

	@Resource
	protected EntityCacheManager<String, O> entityManager;

	private final ItemKeyMaker itemIdMaker = new ItemKeyMaker();

	private final Consumer<O> onLoad;

	protected GameCacheManager(Class<? extends O> entityClass) {
		this(entityClass, null);
	}

	protected GameCacheManager(Class<? extends O> entityClass, Consumer<O> onLoad) {
		super(entityClass);
		this.onLoad = onLoad;
	}

	@Override
	protected O get(long playerId) {
		O value = this.entityManager.getEntity(itemIdMaker.makeId(this.entityClass, playerId));
		if (value == null) {
			return null;
		}
		return onLoad(value);
	}

	@Override
	protected O get(long playerId, long id) {
		O value = this.entityManager.getEntity(itemIdMaker.makeId(this.entityClass, playerId, id));
		if (value == null) {
			return null;
		}
		return onLoad(value);
	}

	@Override
	protected Collection<O> getAll(long playerId, Collection<Long> ids) {
		List<String> keys = ids.stream()
				.map(id -> this.itemIdMaker.makeId(this.entityClass, playerId, id))
				.collect(Collectors.toList());
		return onLoad(this.entityManager.getEntities(keys));
	}

	protected Collection<O> getByKeys(String... keys) {
		return onLoad(this.entityManager.getEntities(Arrays.asList(keys)));
	}

	protected Collection<O> getByKeys(Collection<String> keys) {
		return onLoad(this.entityManager.getEntities(keys));
	}

	protected O getByKey(long playerId, long id) {
		return onLoad(this.entityManager.getEntity(this.itemIdMaker.makeId(this.entityClass, playerId, id)));
	}

	private O onLoad(O o) {
		if (o == null || this.onLoad == null) {
			return o;
		}
		try {
			this.onLoad.accept(o);
		} catch (Throwable e) {
			LOGGER.error("", e);
		}
		return o;
	}

	private Collection<O> onLoad(Collection<O> os) {
		if (os == null || os.isEmpty() || this.onLoad == null) {
			return os;
		}
		for (O o : os) {
			try {
				this.onLoad.accept(o);
			} catch (Throwable e) {
				LOGGER.error("", e);
			}
		}
		return os;
	}

	@Override
	public boolean save(O item) {
		return this.entityManager.saveEntity(item);
	}

	@Override
	public Collection<O> save(Collection<O> itemCollection) {
		return itemCollection.stream().filter(o -> !this.save(o)).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean update(O item) {
		return this.entityManager.updateEntity(item);
	}

	@Override
	public Collection<O> update(Collection<O> itemCollection) {
		return itemCollection.stream().filter(o -> !this.update(o)).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean insert(O item) {
		return this.entityManager.insertEntity(item);
	}

	@Override
	public Collection<O> insert(Collection<O> itemCollection) {
		return itemCollection.stream().filter(o -> !this.insert(o)).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void delete(O item) {
		this.entityManager.deleteEntity(item);
	}

	@Override
	public void delete(Collection<O> itemCollection) {
		itemCollection.forEach(this::delete);
	}

}
