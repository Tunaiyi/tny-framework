package com.tny.game.net.base;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 服务者,接入的服务点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public interface RpcServicerPoint extends RpcServicer, Comparable<RpcServicerPoint> {

    long getId();

    Comparator<RpcServicerPoint> COMPARATOR = Comparator.comparing(RpcServicerPoint::getId);

    @Override
    default int compareTo(@Nonnull RpcServicerPoint o) {
        return COMPARATOR.compare(this, o);
    }

}
