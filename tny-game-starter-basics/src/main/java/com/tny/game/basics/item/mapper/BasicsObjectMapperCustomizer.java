/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.common.*;
import com.tny.game.basics.loader.*;
import com.tny.game.basics.mould.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.common.result.*;
import com.tny.game.common.scheduler.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:17 下午
 */
public class BasicsObjectMapperCustomizer implements ObjectMapperCustomizer {

    private final ItemModelJsonSerializer serializer;

    private final ItemModelJsonDeserializer deserializer;

    public BasicsObjectMapperCustomizer(
            ItemModelJsonSerializer serializer, ItemModelJsonDeserializer deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        for (Class<? extends ItemModel> modelClass : ItemModelClassLoader.getClasses()) {
            module.addSerializer(modelClass, serializer);
            module.addDeserializer(as(modelClass), deserializer);
        }
        module.addSerializer(ItemModel.class, serializer);
        module.addDeserializer(as(ItemModel.class), deserializer);

        module.addSerializer(StuffModel.class, serializer);
        module.addDeserializer(as(StuffModel.class), deserializer);

        module.addSerializer(DefaultItemModel.class, serializer);
        module.addDeserializer(as(DefaultItemModel.class), deserializer);

        module.addSerializer(Ability.class, new IdObjectSerializer<>(Ability::getId));
        module.addDeserializer(Ability.class, new IdObjectDeserializer<>(Abilities::of));

        module.addSerializer(ItemType.class, new IdObjectSerializer<>(ItemType::getId));
        module.addDeserializer(ItemType.class, new IdObjectDeserializer<>(ItemTypes::of));

        module.addSerializer(DemandType.class, new IdObjectSerializer<>(DemandType::getId));
        module.addDeserializer(DemandType.class, new IdObjectDeserializer<>(DemandTypes::of));

        module.addSerializer(DemandParam.class, new IdObjectSerializer<>(DemandParam::getId));
        module.addDeserializer(DemandParam.class, new IdObjectDeserializer<>(DemandParams::of));

        module.addSerializer(Action.class, new IdObjectSerializer<>(Action::getId));
        module.addDeserializer(Action.class, new IdObjectDeserializer<>(Actions::of));

        module.addSerializer(Behavior.class, new IdObjectSerializer<>(Behavior::getId));
        module.addDeserializer(Behavior.class, new IdObjectDeserializer<>(Behaviors::of));

        module.addSerializer(ResultCode.class, new IdObjectSerializer<>(ResultCode::getCode));
        module.addDeserializer(ResultCode.class, new IdObjectDeserializer<>(ResultCodes::of));

        module.addSerializer(Mould.class, new IdObjectSerializer<>(Mould::getId));
        module.addDeserializer(Mould.class, new IdObjectDeserializer<>(Moulds::of));

        module.addSerializer(Feature.class, new IdObjectSerializer<>(Feature::getId));
        module.addDeserializer(Feature.class, new IdObjectDeserializer<>(Features::of));

        module.addSerializer(FeatureOpenMode.class, new IdObjectSerializer<>(FeatureOpenMode::getId));
        module.addDeserializer(FeatureOpenMode.class, new IdObjectDeserializer<>(FeatureOpenModes::of));

        module.addSerializer(TaskReceiverType.class, new IdObjectSerializer<>(TaskReceiverType::getId));
        module.addDeserializer(TaskReceiverType.class, new IdObjectDeserializer<>(TaskReceiverTypes::of));

        mapper.registerModule(module);
    }

}
