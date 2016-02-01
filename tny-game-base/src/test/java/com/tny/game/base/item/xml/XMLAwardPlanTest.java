package com.tny.game.base.item.xml;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.Award;
import com.tny.game.base.item.behavior.AwardGroup;
import com.tny.game.base.item.behavior.AwardPlan;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.plan.SimpleAwardGroup;
import com.tny.game.base.item.behavior.plan.SimpleAwardPlan;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class XMLAwardPlanTest {

    int NUMBER;

    RandomCreator<AwardGroup> randomer = new RandomCreator<AwardGroup>() {

        @Override
        public List<AwardGroup> random(int range, int number, Collection<? extends AwardGroup> probabiliySet, Map<String, Object> attributeMap) {
            for (AwardGroup p : probabiliySet)
                if (XMLAwardPlanTest.this.NUMBER < p.getProbability())
                    return Arrays.asList(p);
            return null;
        }

    };

    class TeatAward implements Award {

        private String alias;

        private TeatAward(String alias) {
            super();
            this.alias = alias;
        }

        @Override
        public int countNumber(ItemModel model, Map<String, Object> attributes) {
            return 10;
        }

        @Override
        public AlterType getAlterType() {
            return null;
        }

        @Override
        public Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap) {
            return null;
        }

        @Override
        public int getProbability() {
            return 0;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int compareTo(Probability o) {
            return 0;
        }

        @Override
        public String getItemAlias(Map<String, Object> atrributeMap) {
            return this.alias;
        }

    }

    String alias1 = "item1";
    String alias2 = "item2";
    ItemModel award1 = new TestAwardModel(this.alias1);
    ItemModel award2 = new TestAwardModel(this.alias2);

    TempExplorer explorer = new TempExplorer(this.award1, this.award2);

    class TestAwardGroup extends SimpleAwardGroup {

        private TestAwardGroup(int id, int probability, Award... awards) {
            super();
            this.probability = probability;
            this.awardList = new ArrayList<Award>();
            for (Award award : awards)
                this.awardList.add(award);
            this.itemModelExplorer = XMLAwardPlanTest.this.explorer;
        }

    }

    TreeSet<AwardGroup> treeSet = new TreeSet<AwardGroup>();

    {
        this.treeSet.add(new TestAwardGroup(1, 30, new TeatAward(this.alias1)));
        this.treeSet.add(new TestAwardGroup(2, 100, new TeatAward(this.alias2)));
    }

    AwardPlan awardPlan = new SimpleAwardPlan(this.randomer, this.treeSet);

    @Test
    public void testCreateTradeResult() {
        this.NUMBER = 1;
        Trade result = this.awardPlan.createTrade(100, TestAction.GOLD_UPGRADE, null);
        Assert.assertTrue(result.getNumber(this.award1) > 0);
        this.NUMBER = 30;
        result = this.awardPlan.createTrade(100, TestAction.GOLD_UPGRADE, null);
        Assert.assertTrue(result.getNumber(this.award1) > 0);
        this.NUMBER = 100;
        result = this.awardPlan.createTrade(100, TestAction.GOLD_UPGRADE, null);
        Assert.assertFalse(result.getNumber(this.award1) > 0);
        Assert.assertFalse(result.getNumber(this.award2) > 0);
    }

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
    }

}
