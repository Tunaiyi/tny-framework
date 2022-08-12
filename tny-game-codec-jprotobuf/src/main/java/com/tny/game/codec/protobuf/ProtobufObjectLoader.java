/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class ProtobufObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufObjectLoader.class);

    @ClassSelectorProvider
    static ClassSelector autoProtobufSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(ProtobufClass.class))
                .setHandler((classes) -> {
                    ProtobufObjectCodecFactory factory = ProtobufObjectCodecFactory.getInstance();
                    classes.forEach(cl -> {
                        try {
                            factory.createCodec(cl);
                        } catch (Throwable e) {
                            LOGGER.error("{} create codecor exception", cl, e);
                            throw e;
                        }
                    });
                });
    }

}
