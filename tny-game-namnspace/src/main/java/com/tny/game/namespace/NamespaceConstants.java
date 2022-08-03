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

