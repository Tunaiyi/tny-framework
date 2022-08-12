/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
