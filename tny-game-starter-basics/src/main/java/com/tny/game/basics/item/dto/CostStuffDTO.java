/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * Created by xiaoqing on 2016/3/7.
 */
@ProtoEx(BasicsProtoIDs.COST_STUFF_DTO)
@DTODoc("消耗物品DTO")
public class CostStuffDTO {

    @VarDoc("id")
    @ProtoExField(3)
    private long id;

    @VarDoc("modelId")
    @ProtoExField(1)
    private int modelId;

    @VarDoc("数量")
    @ProtoExField(2)
    private int number;

    public int getModelId() {
        return modelId;
    }

    public int getNumber() {
        return number;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(id);
        buffer.append(modelId);
        buffer.append(number);
        return buffer.toString();
    }

}
