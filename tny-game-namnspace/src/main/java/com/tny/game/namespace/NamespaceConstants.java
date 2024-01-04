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

package com.tny.game.namespace;

import java.nio.charset.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/20 17:23
 **/
public final class NamespaceConstants {

    private NamespaceConstants() {
    }

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final long UNLIMITED_SLOT_SIZE = -1;

    public static final long MAX_SLOT_SIZE = -1L >>> 32;

}

