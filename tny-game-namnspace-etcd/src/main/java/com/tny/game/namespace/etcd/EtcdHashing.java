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
package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.common.utils.*;
import com.tny.game.namespace.*;

/**
 * Etcd 插槽栏
 * <p>
 *
 * @author kgtny
 * @date 2022/7/20 17:11
 **/
public abstract class EtcdHashing<T> {

    protected final NamespaceExplorer explorer;

    protected final String path;

    protected final long maxSlots;

    protected final ObjectMimeType<T> mineType;

    public EtcdHashing(String path, long maxSlots, ObjectMimeType<T> mineType, NamespaceExplorer explorer) {
        this.explorer = explorer;
        this.maxSlots = maxSlots;
        this.path = path;
        this.mineType = mineType;
    }

    public String getPath() {
        return path;
    }

    protected String slotName(long hashCode) {
        long max = getMaxSlots();
        if (hashCode >= max) {
            hashCode = hashCode % max;
        }
        return NumberFormatAide.alignDigits(hashCode, max);
    }

    protected long getMaxSlots() {
        return maxSlots;
    }

    public ObjectMimeType<T> getMineType() {
        return mineType;
    }

}
