package com.tny.game.suite.base.module;

import com.tny.game.common.event.bus.*;
import com.tny.game.common.io.config.*;
import com.tny.game.common.version.*;
import com.tny.game.suite.base.module.event.*;
import com.tny.game.suite.utils.*;
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
    private volatile Version devVersion;
    private volatile Version defVersion;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = this.readWriteLock.readLock();
    private Lock writeLock = this.readWriteLock.writeLock();

    FeatureVersionHolder() {
        this.initConfigVersion(Configs.VERSION_CONFIG, Configs.VERSION_FEATURE_VERSION, this::getDefVersion, this::setDefVersion)
                .initConfigVersion(Configs.DEVELOP_CONFIG, Configs.DEVELOP_FEATURE_VERSION, this::getDevVersion, this::setDevVersion);
    }

    public Optional<Version> getFeatureVersion() {
        this.readLock.lock();
        try {
            if (this.devVersion != null) {
                return Optional.of(this.devVersion);
            }
            if (this.version != null) {
                return Optional.of(this.version);
            }
            if (this.defVersion != null) {
                return Optional.of(this.defVersion);
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

    private FeatureVersionHolder doUpdateVersion(String version, Supplier<Version> versionGetter, Consumer<Version> versionSetter, boolean event) {
        return doUpdateVersion(StringUtils.isNoneBlank(version) ? Version.of(version) : null, versionGetter, versionSetter, event);
    }

    private FeatureVersionHolder doUpdateVersion(Version newVersion, Supplier<Version> versionGetter, Consumer<Version> versionSetter,
            boolean event) {
        this.writeLock.lock();
        try {
            Version oldVersion = versionGetter.get();
            if (newVersion != null) {
                if (newVersion.equals(oldVersion)) {
                    return this;
                }
                versionSetter.accept(newVersion);
                if (event) {
                    ON_CHANGE.notify(this);
                }
            } else {
                if (oldVersion == null) {
                    return this;
                }
                versionSetter.accept(null);
                if (event) {
                    ON_CHANGE.notify(this);
                }
            }
        } finally {
            this.writeLock.unlock();
        }
        return this;
    }

    private Version getVersion() {
        return this.version;
    }

    private FeatureVersionHolder setVersion(Version version) {
        this.version = version;
        return this;
    }

    private Version getDevVersion() {
        return this.devVersion;
    }

    private FeatureVersionHolder setDevVersion(Version devVersion) {
        this.devVersion = devVersion;
        return this;
    }

    private Version getDefVersion() {
        return this.defVersion;
    }

    private FeatureVersionHolder setDefVersion(Version defVersion) {
        this.defVersion = defVersion;
        return this;
    }

    private FeatureVersionHolder initConfigVersion(Config config, String key, Supplier<Version> versionGetter, Consumer<Version> versionSetter) {
        this.doUpdateVersion(config.getString(key), versionGetter, versionSetter, false);
        config.addConfigReload(c -> this.doUpdateVersion(config.getString(key), versionGetter, versionSetter, true));
        return this;
    }

}
