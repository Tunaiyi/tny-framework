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
