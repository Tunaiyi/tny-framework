package com.tny.game.net.base;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public interface RpcServicer extends RpcServiceNode, Comparable<RpcServicer> {

    Comparator<RpcServicer> COMPARATOR = Comparator.comparing(RpcServicer::getId);

    long getId();

    @Override
    default int compareTo(@Nonnull RpcServicer o) {
        return COMPARATOR.compare(this, o);
    }

}
