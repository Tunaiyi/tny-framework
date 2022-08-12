/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;

public class TestItemModelManager extends LoadableModelManager<TestItemModel> {

    private final ItemModelContext context;

    private final ModelLoaderFactory factory;

    protected TestItemModelManager(String PATH, ItemModelContext context, ModelLoaderFactory factory) {
        super(TestItemModelImpl.class, TestBehavior.class, TestDemandType.class, TestAction.class, TestAbility.class,
                TestOption.class, PATH);
        this.factory = factory;
        this.context = context;
    }

    protected void initThis() throws Exception {
        this.initManager();
    }

    @Override
    protected ModelLoaderFactory loaderFactory() {
        return factory;
    }

    @Override
    protected ItemModelContext context() {
        return context;
    }

}
