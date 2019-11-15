package com.tny.game.base.item.xml;

import com.tny.game.base.item.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.Test;
import org.junit.*;

public class AbstractXMLItemModelManagerTest {

    TempExplorer explorer = new TempExplorer();

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(explorer, explorer, exprHolderFactory);

    TestItemModelManager manager = new TestItemModelManager("ItemExample.xml", context);
    ItemModel itemModel = null;

    public AbstractXMLItemModelManagerTest() throws Exception {
        manager.initManager();
        itemModel = manager.getModel(1);
        System.out.println(itemModel);
    }

    @Test
    public void testOption() {
        int value = 0;
        value = itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.CD);
        Assert.assertEquals(value, 1000);
        value = itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.ATTACK_CD);
        Assert.assertEquals(value, 200);
        value = itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.DEFEND_CD);
        Assert.assertEquals(value, 300);
    }

}
