/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent.lock;

/**
 * 对象锁对应实体标识接口，用于告知锁创建器具体类实例是实体对象
 *
 * @author KGTny
 */

public interface LockEntity<T extends Comparable<?>> {

    /**
     * 获取可对比大小实体实体标识
     *
     * @return 返回可对比大小实体表示
     */
    public T getIdentity();

}
