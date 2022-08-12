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
import com.tny.game.basics.item.loader.xstream.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XStreamItemModelManagerTest {

    TempExplorer explorer = new TempExplorer();

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(this.explorer, this.explorer, exprHolderFactory);

    private TestItemModelManager manager =
            new TestItemModelManager("ItemExample.xml", this.context, new XStreamModelLoaderFactory(exprHolderFactory));

    ItemModel itemModel = null;

    public XStreamItemModelManagerTest() throws Exception {
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
