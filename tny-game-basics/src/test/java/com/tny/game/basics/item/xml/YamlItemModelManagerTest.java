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
