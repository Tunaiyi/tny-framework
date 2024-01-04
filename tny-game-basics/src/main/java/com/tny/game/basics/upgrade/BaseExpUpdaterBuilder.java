/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
