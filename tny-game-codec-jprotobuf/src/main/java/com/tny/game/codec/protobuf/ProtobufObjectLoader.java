/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
