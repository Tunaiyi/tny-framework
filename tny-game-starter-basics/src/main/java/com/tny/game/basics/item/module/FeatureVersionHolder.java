package com.tny.game.basics.item.module;

import com.tny.game.basics.item.module.event.*;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.version.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * Created by Kun Yang on 2017/12/23.
 */
public class FeatureVersionHolder {

	static final BindVoidEventBus<FeatureVersionChangeListener, FeatureVersionHolder> ON_CHANGE =
			EventBuses.of(FeatureVersionChangeListener.class, FeatureVersionChangeListener::onChange);

	private volatile Version version;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private final Lock readLock = this.readWriteLock.readLock();

	private final Lock writeLock = this.readWriteLock.writeLock();

	FeatureVersionHolder() {
	}

	public Optional<Version> getFeatureVersion() {
		this.readLock.lock();
		try {
			if (this.version != null) {
				return Optional.of(this.version);
			}
			return Optional.empty();
		} finally {
			this.readLock.unlock();
		}
	}

	FeatureVersionHolder updateVersion(Version version) {
		doUpdateVersion(version, this::getVersion, this::setVersion, true);
		return this;
	}

	FeatureVersionHolder updateVersion(String version) {
		doUpdateVersion(version, this::getVersion, this::setVersion, true);
		return this;
	}

	private void doUpdateVersion(String version, Supplier<Version> versionGetter, Consumer<Version> versionSetter, boolean event) {
		doUpdateVersion(StringUtils.isNoneBlank(version) ? Version.of(version) : null, versionGetter, versionSetter, event);
	}

	private void doUpdateVersion(Version newVersion, Supplier<Version> versionGetter, Consumer<Version> versionSetter,
			boolean event) {
		this.writeLock.lock();
		try {
			Version oldVersion = versionGetter.get();
			if (newVersion != null) {
				if (newVersion.equals(oldVersion)) {
					return;
				}
				versionSetter.accept(newVersion);
			} else {
				if (oldVersion == null) {
					return;
				}
				versionSetter.accept(null);
			}
			if (event) {
				ON_CHANGE.notify(this);
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	private Version getVersion() {
		return this.version;
	}

	private FeatureVersionHolder setVersion(Version version) {
		this.version = version;
		return this;
	}

}