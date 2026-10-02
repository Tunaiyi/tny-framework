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

package com.tny.game.test;

import org.mockito.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public final class MockAide extends Mockito {

    public static final Logger LOGGER = LoggerFactory.getLogger(MockAide.class);

    private MockAide() {
    }

    public static <T extends M, M> ArgumentCaptor<T> captorAs(Class<M> argumentClass) {
        return as(ArgumentCaptor.forClass(argumentClass));
    }

    public static <T extends M, M> T mockAs(Class<M> mockClass) {
        return as(Mockito.mock(mockClass));
    }

}
