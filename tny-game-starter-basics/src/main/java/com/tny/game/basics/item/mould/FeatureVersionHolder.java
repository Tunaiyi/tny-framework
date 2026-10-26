/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.item.mould;

import com.tny.game.basics.item.mould.event.*;
import com.tny.game.common.event.*;
import com.tny.game.common.version.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * Created by Kun Yang on 2017/12/23.
 */
public class FeatureVersionHolder {

    static final VoidBindEvent<FeatureVersionChangeListener, FeatureVersionHolder> ON_CHANGE =
            Events.ofEvent(FeatureVersionChangeListener.class, FeatureVersionChangeListener::onChange);

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
