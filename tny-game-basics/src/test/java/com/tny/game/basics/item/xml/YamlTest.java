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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.plan.*;
import com.tny.game.basics.item.loader.jackson.yaml.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.common.io.config.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class YamlTest {

    TempExplorer explorer = new TempExplorer();

    private static ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private ItemModelContext context = new DefaultItemModelContext(this.explorer, this.explorer, exprHolderFactory);

    TestItemModelManager manager =
            new TestItemModelManager("ItemExample.yml", this.context, new YamlModelLoaderFactory(exprHolderFactory));

    ItemModel itemModel = null;

    public YamlTest() throws Exception {
        //		this.manager.initManager();
        //		this.itemModel = this.manager.getModel(1);
        //		System.out.println(this.itemModel);
    }

    @Test
    public void testOption() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(BaseDemand.class, DefaultDemand.class)
                .addAbstractTypeMapping(BaseBehaviorPlan.class, DefaultBehaviorPlan.class)
                .addAbstractTypeMapping(BaseActionPlan.class, DefaultActionPlan.class)
                .addAbstractTypeMapping(BaseDemand.class, DefaultDemand.class)
                .addAbstractTypeMapping(BaseAwardPlan.class, DefaultAwardPlan.class)
                .addAbstractTypeMapping(AwardGroup.class, SimpleAwardGroup.class)
                .addAbstractTypeMapping(Award.class, SimpleAward.class)
                .addAbstractTypeMapping(BaseAward.class, SimpleAward.class)
                .addAbstractTypeMapping(BaseCostPlan.class, DefaultCostPlan.class)
        ;
        module.addKeyDeserializer(Ability.class, new EnumKeyDeserializer<>(TestAbility.class));
        module.addKeyDeserializer(Action.class, new EnumKeyDeserializer<>(TestAction.class));
        module.addKeyDeserializer(Behavior.class, new EnumKeyDeserializer<>(TestBehavior.class));
        module.addKeyDeserializer(Option.class, new EnumKeyDeserializer<>(TestOption.class));
        module.addKeyDeserializer(DemandType.class, new EnumKeyDeserializer<>(TestDemandType.class));
        module.addKeyDeserializer(DemandParam.class, new EnumKeyDeserializer<>(TestDemandType.class));
        module.addDeserializer(ExprHolder.class, new ExprHolderDeserialize(exprHolderFactory));
        module.addDeserializer(Ability.class, new EnumDeserializer<>(TestAbility.class));
        module.addDeserializer(Action.class, new EnumDeserializer<>(TestAction.class));
        module.addDeserializer(Behavior.class, new EnumDeserializer<>(TestBehavior.class));
        module.addDeserializer(Option.class, new EnumDeserializer<>(TestOption.class));
        module.addDeserializer(DemandType.class, new EnumDeserializer<>(TestDemandType.class));
        module.addDeserializer(DemandParam.class, new EnumDeserializer<>(TestDemandType.class));

        mapper.registerModule(module);

        List<TestItemModelImpl> models = mapper.readValue(FileIOAide.loadFile("ItemExample.yml"), new TypeReference<List<TestItemModelImpl>>() {

        });
        System.out.println();
        //		int value = 0;
        //		value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.CD);
        //		assertEquals(value, 1000);
        //		value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.ATTACK_CD);
        //		assertEquals(value, 200);
        //		value = this.itemModel.getActionOption(100, TestAction.NOMAL_UPGRADE, TestOption.DEFEND_CD);
        //		assertEquals(value, 300);
    }

}
