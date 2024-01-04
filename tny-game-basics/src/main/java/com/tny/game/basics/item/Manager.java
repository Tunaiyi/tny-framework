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

import java.util.Collection;

/**
 * 管理器接口
 *
 * @param <O>
 * @author KGTny
 */
public interface Manager<O> {

    boolean save(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回存储失败的列表
     */
    Collection<O> save(Collection<O> itemCollection);

    boolean update(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回更新失败的列表
     */
    Collection<O> update(Collection<O> itemCollection);

    boolean insert(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回插入失败的列表
     */
    Collection<O> insert(Collection<O> itemCollection);

    void delete(O item);

    void delete(Collection<O> itemCollection);

}
