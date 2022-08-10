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

    protected final ObjectMineType<T> mineType;

    public EtcdHashing(String path, ObjectMineType<T> mineType, NamespaceExplorer explorer) {
        this.explorer = explorer;
        this.path = path;
        this.mineType = mineType;
    }

    protected String slotName(long hashCode) {
        long max = getMaxSlots();
        if (hashCode >= max) {
            hashCode = hashCode % max;
        }
        return NumberFormatAide.alignDigits(hashCode, max);
    }

    protected abstract long getMaxSlots();

    public ObjectMineType<T> getMineType() {
        return mineType;
    }

}
