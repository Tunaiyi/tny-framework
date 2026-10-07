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

package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.jackson.yaml.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YamlItemModelManagerTest {

    TempExplorer explorer = new TempExplorer();

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(this.explorer, this.explorer, exprHolderFactory);

    TestItemModelManager manager =
            new TestItemModelManager("ItemExample.yml", this.context, new YamlModelLoaderFactory(exprHolderFactory));

    ItemModel itemModel = null;

    public YamlItemModelManagerTest() throws Exception {
        this.manager.initManager();
        this.itemModel = this.manager.getModel(1);
        System.out.println(this.itemModel);
    }

    @Test
    public void testOption() {
        int value = 0;
        value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.CD);
        assertEquals(value, 1000);
        value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.ATTACK_CD);
        assertEquals(value, 200);
        value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.DEFEND_CD);
        assertEquals(value, 300);
    }

}
