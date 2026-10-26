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

import com.tny.game.basics.upgrade.notify.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by xiaoqing on 2018/3/10.
 */
public abstract class BaseExpUpdaterBuilder<
        I extends ExpUpgradableItem<?>,
        U extends BaseExpUpdater<I, EM>, EM extends ExpModel,
        B extends BaseExpUpdaterBuilder<I, U, EM, B>>
        extends BaseUpdaterBuilder<I, U, BaseExpUpdaterBuilder<I, U, EM, B>> {

    private OnReceiveExp<I, EM> onReceiveExp;

    private OnPreReceiveExp<I, EM> onPreReceiveExp;

    private int level;

    private long exp;

    public B setOnReceiveExp(OnReceiveExp<I, EM> onReceiveExp) {
        this.onReceiveExp = onReceiveExp;
        return as(this);
    }

    public B setOnPreReceiveExp(OnPreReceiveExp<I, EM> onPreReceiveExp) {
        this.onPreReceiveExp = onPreReceiveExp;
        return as(this);
    }

    @Override
    public B setLevel(int level) {
        this.level = level;
        return as(this);
    }

    public B setExp(long exp) {
        this.exp = exp;
        return as(this);
    }

    @Override
    protected void postInit(U updater) {
        updater.setExp(this.exp)
                .withOnPreReceiveExp(onPreReceiveExp)
                .withOnReceiveExp(onReceiveExp);
        postInitUpdater(updater);
    }

    @Override
    public U createUpdater() {
        return createUpdater(this.item, this.level, this.exp);
    }

    protected abstract void postInitUpdater(U updater);

    public abstract U createUpdater(I item, int level, long exp);

}
