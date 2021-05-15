package com.tny.game.base.item.xml;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.result.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.Test;
import org.junit.*;

import java.util.HashMap;

public class XMLDemandTest {

    /**
     * 消耗物品在公式中的名字
     */
    public static final String ITEM_NAME = "data";

    DemandType demandType = new DemandType() {

        @Override
        public ResultCode getResultCode() {
            return null;
        }

        @Override
        public boolean isCost() {
            return false;
        }

        @Override
        public Integer getId() {
            return 0;
        }

    };
    String itemAlias = "pl$player";
    int number = 10;

    class TestDemandItemModel extends AbstractItemModel {

        {
            this.alias = XMLDemandTest.this.itemAlias;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ItemType getItemType() {
            return new ItemType() {

                @Override
                public Integer getId() {
                    return 0;
                }

                @Override
                public String getAliasHead() {
                    return null;
                }

                @Override
                public String getDesc() {
                    return null;
                }

            };
        }

        @Override
        protected void init(ItemModelContext context) {
            super.init(context);
        }

    }

    ;

    private TestDemandItemModel model = new TestDemandItemModel();

    String name = "player";
    int level = 10;

    TestPlayer item = new TestPlayer(this.number, this.level, this.itemAlias);

    TempExplorer explorer = new TempExplorer(this.model, this.item);

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(this.explorer, this.explorer, exprHolderFactory);

    XMLDemand stuffDemand = new XMLDemand(this.itemAlias, ITEM_NAME, 100 + "", exprHolderFactory);

    XMLDemand demand = new XMLDemand("pl$player", null, this.demandType,
            "pl$player != null ? pl$player.level : 0",
            "10",
            "pl$player != null && pl$player.level >= " + ItemsImportKey.EXPECT_VALUE, exprHolderFactory);

    {
        this.model.init(this.context);
        this.demand.init(this.model, this.context);
        this.stuffDemand.init(this.model, this.context);
    }

    @Test
    public void testGetItemId() {
        Assert.assertEquals(this.itemAlias, this.demand.getItemAlias(new HashMap<>()));
        Assert.assertEquals(this.itemAlias, this.stuffDemand.getItemAlias(new HashMap<>()));
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(null, this.demand.getName());
        Assert.assertEquals(ITEM_NAME, this.stuffDemand.getName());
    }

    @Test
    public void testGetDemandType() {
        Assert.assertEquals(this.demandType, this.demand.getDemandType());
        Assert.assertEquals(XMLDemand.TradeDemandType.COST_DEMAND_GE, this.stuffDemand.getDemandType());
    }

    MapRef<String, Object> ref = new MapRef<String, Object>() {
    };

    @Test
    public void testIsSatisfy() {

        Assert.assertFalse(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setNumber(101);
        Assert.assertTrue(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setNumber(100);
        Assert.assertTrue(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setLevel(4);
        Assert.assertFalse(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setLevel(14);
        Assert.assertTrue(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setLevel(10);
        Assert.assertTrue(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

    @Test
    public void testCountExpectValue() {
        Assert.assertEquals(100, this.stuffDemand.countExpectValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        Assert.assertEquals(10, this.demand.countExpectValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

    @Test
    public void testCountCurrentValue() {
        this.item.setNumber(0);
        Assert.assertEquals(0, this.stuffDemand.countCurrentValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setNumber(101);
        Assert.assertEquals(101, this.stuffDemand.countCurrentValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

}
