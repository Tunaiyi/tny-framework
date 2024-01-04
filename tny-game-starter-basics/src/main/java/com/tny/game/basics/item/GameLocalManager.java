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

package com.tny.game.basics.item;

import java.util.*;

/**
 * 没有任何数据库存储的manager
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GameLocalManager<O> extends GameManager<O> {

    protected GameLocalManager(Class<? extends O> entityClass) {
        super(entityClass);
    }

    @Override
    public boolean save(O item) {
        return true;
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public boolean update(O item) {
        return true;
    }

    @Override
    public Collection<O> update(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public boolean insert(O item) {
        return true;
    }

    @Override
    public Collection<O> insert(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public void delete(O item) {
    }

    @Override
    public void delete(Collection<O> itemCollection) {
    }

}
