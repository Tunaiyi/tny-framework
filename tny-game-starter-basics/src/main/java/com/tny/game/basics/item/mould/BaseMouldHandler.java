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

package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;

public abstract class BaseMouldHandler<M extends Mould, C> implements MouldHandler {

    private final M mould;

    public BaseMouldHandler(M mould) {
        super();
        this.mould = mould;
    }

    @Override
    public Mould getMould() {
        return this.mould;
    }

    public abstract void loadContext(FeatureLauncher launcher, C context);

    @Override
    public void removeMould(FeatureLauncher launcher) {
    }

    @Override
    public String toString() {
        return "GameMould [getMould()=" + this.getMould() + "]";
    }

}
