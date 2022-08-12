/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.List;

/**
 * Created by xiaoqing on 2016/3/7.
 */
@ProtoEx(BasicsProtoIDs.COST_STUFF_LIST_DTO)
@DTODoc("消耗物品列表DTO")
public class CostStuffListDTO {

    @VarDoc("物品列表")
    @ProtoExField(1)
    private List<CostStuffDTO> stuffs;

    public List<CostStuffDTO> getStuffs() {
        return stuffs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (CostStuffDTO dto : stuffs) {
            builder.append(dto);
        }
        return builder.toString();
    }

}
