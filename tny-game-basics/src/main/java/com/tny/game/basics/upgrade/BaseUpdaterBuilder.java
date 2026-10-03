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

package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;
import com.tny.game.basics.upgrade.notify.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 抽象升级器 builder
 *
 * @param <I>
 * @param <U>
 * @param <B>
 */
public abstract class BaseUpdaterBuilder<I extends Item<?>, U extends BaseUpdater<I>, B extends BaseUpdaterBuilder<I, U, B>> {

    private OnUpgrade<I> onUpgrade;

    private OnPreUpgrade<I> onPreUpgrade;

    private OnReset<I> onReset;

    protected I item;

    protected int level;

    public B setOnUpgrade(OnUpgrade<I> onUpgrade) {
        this.onUpgrade = onUpgrade;
        return as(this);
    }

    public B setPrepareUpgrade(OnPreUpgrade<I> onPreUpgrade) {
        this.onPreUpgrade = onPreUpgrade;
        return as(this);
    }

    protected B setOnReset(OnReset<I> onReset) {
        this.onReset = onReset;
        return as(this);
    }

    public B setItem(I item) {
        this.item = item;
        return as(this);
    }

    public B setLevel(int level) {
        this.level = level;
        return as(this);
    }

    public U build() {
        U updater = createUpdater();
        init(updater);
        postInit(updater);
        return updater;
    }

    private void init(U updater) {
        updater.setLevel(this.level)
                .setItem(this.item)
                .withOnPreUpgrade(onPreUpgrade)
                .withOnUpgrade(onUpgrade)
                .withOnReset(onReset);
    }

    protected void postInit(U updater) {

    }

    public abstract U createUpdater();

}
