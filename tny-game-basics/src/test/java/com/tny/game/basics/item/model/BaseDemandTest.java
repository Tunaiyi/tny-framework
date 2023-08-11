/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.model;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.result.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BaseDemandTest {

    /**
     * 消耗物品在公式中的名字
     */
    private static final String ITEM_NAME = "data";

    private DemandType demandType = new DemandType() {

        @Override
        public String name() {
            return "demandType";
        }

        @Override
        public ResultCode getResultCode() {
            return null;
        }

        @Override
        public boolean isCost() {
            return false;
        }

        @Override
        public int id() {
            return 0;
        }

    };

    private String itemAlias = "pl$player";

    private int number = 10;

    class TestDemandItemModel extends AbstractItemModel implements StuffModel {

        {
            this.alias = BaseDemandTest.this.itemAlias;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ItemType getItemType() {
            return new ItemType() {

                @Override
                public String name() {
                    return "item";
                }

                @Override
                public int id() {
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
        protected void onItemInit(ItemModelContext context) {
        }

        @Override
        public ItemType getOwnerType() {
            return null;
        }

    }

    ;

    private TestDemandItemModel model = new TestDemandItemModel();

    String name = "player";

    private int level = 10;

    private TestPlayer item = new TestPlayer(this.number, this.level, this.itemAlias);

    private TempExplorer explorer = new TempExplorer(this.model, this.item);

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(this.explorer, this.explorer, exprHolderFactory);

    private DefaultDemand stuffDemand = new DefaultDemand(this.itemAlias, ITEM_NAME, 100 + "", exprHolderFactory);

    private DefaultDemand demand = new DefaultDemand("pl$player", null, this.demandType,
            "pl$player != null ? pl$player.level : 0",
            "10",
            "pl$player != null && pl$player.level >= " + ItemsImportKey.EXPECT_VALUE, exprHolderFactory);

    {
        this.model.init(this.context);
        this.demand.init(this.model, this.context);
        this.stuffDemand.init(this.model, this.context);
    }

    @Test
    void testGetModelId() {
        assertEquals(this.itemAlias, this.demand.getItemAlias(new HashMap<>()));
        assertEquals(this.itemAlias, this.stuffDemand.getItemAlias(new HashMap<>()));
    }

    @Test
    void testGetName() {
        assertEquals(null, this.demand.getName());
        assertEquals(ITEM_NAME, this.stuffDemand.getName());
    }

    @Test
    void testGetDemandType() {
        assertEquals(this.demandType, this.demand.getDemandType());
        assertEquals(TradeDemandType.DEDUCT_DEMAND_GE, this.stuffDemand.getDemandType());
    }

    private MapRef<String, Object> ref = new MapRef<String, Object>() {

    };

    @Test
    void testIsSatisfy() {

        assertFalse(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setNumber(101);
        assertTrue(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setNumber(100);
        assertTrue(this.stuffDemand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));

        this.item.setLevel(4);
        assertFalse(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setLevel(14);
        assertTrue(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setLevel(10);
        assertTrue(this.demand.isSatisfy(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

    @Test
    void testCountExpectValue() {
        assertEquals(100, this.stuffDemand.countExpectValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        assertEquals(10, this.demand.countExpectValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

    @Test
    void testCountCurrentValue() {
        this.item.setNumber(0);
        assertEquals(0, this.stuffDemand.countCurrentValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
        this.item.setNumber(101);
        assertEquals(101, this.stuffDemand.countCurrentValue(this.item.getPlayerId(), MapBuilder.newBuilder(this.ref).build()));
    }

}
