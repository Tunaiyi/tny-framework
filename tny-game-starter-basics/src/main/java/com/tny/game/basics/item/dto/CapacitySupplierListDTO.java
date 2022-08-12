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
import com.tny.game.basics.item.capacity.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.*;

@ProtoEx(BasicsProtoIDs.CAPACITY_SUPPLIER_LIST_DTO)
@DTODoc(value = "游戏能力相关对象列表DTO")
public class CapacitySupplierListDTO {

    @VarDoc("能力相关对象列表")
    @ProtoExField(1)
    private List<CapacitySupplierDTO> capacityItems;

    public static CapacitySupplierListDTO create() {
        CapacitySupplierListDTO dto = new CapacitySupplierListDTO();
        dto.capacityItems = new ArrayList<>();
        return dto;
    }

    public static CapacitySupplierListDTO suppliers2DTO(Stream<? extends CapacitySupplier> suppliers) {
        CapacitySupplierListDTO dto = new CapacitySupplierListDTO();
        dto.capacityItems = suppliers
                .filter(CapacitySupplier::isWorking)
                .map(CapacitySupplierDTO::supplier2DTO)
                .collect(Collectors.toList());
        return dto;
    }

    public static CapacitySupplierListDTO suppliers2DTO(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers2DTO(suppliers.stream());
    }

    public static CapacitySupplierListDTO suppliers2RemoveDTO(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers2RemoveDTO(suppliers.stream());
    }

    public static CapacitySupplierListDTO suppliers2RemoveDTO(Stream<? extends CapacitySupplier> suppliers) {
        CapacitySupplierListDTO dto = new CapacitySupplierListDTO();
        dto.capacityItems = suppliers
                .map(CapacitySupplierDTO::supplier2RemoveDTO)
                .collect(Collectors.toList());
        return dto;
    }

    public CapacitySupplierListDTO addSupplier(CapacitySupplier supplier) {
        capacityItems.add(CapacitySupplierDTO.supplier2DTO(supplier));
        return this;
    }

    public CapacitySupplierListDTO addDTO(CapacitySupplierDTO supplierDTO) {
        capacityItems.add(supplierDTO);
        return this;
    }

    public CapacitySupplierListDTO addAllSuppliers(Collection<? extends CapacitySupplier> suppliers) {
        suppliers.forEach(this::addSupplier);
        return this;
    }

    public CapacitySupplierListDTO addAllSuppliers(Stream<? extends CapacitySupplier> suppliers) {
        suppliers.forEach(this::addSupplier);
        return this;
    }

    public CapacitySupplierListDTO addAllDTOs(Collection<CapacitySupplierDTO> supplierDTOs) {
        supplierDTOs.forEach(this::addDTO);
        return this;
    }

    public CapacitySupplierListDTO addAllDTOs(Stream<CapacitySupplierDTO> supplierDTOs) {
        supplierDTOs.forEach(this::addDTO);
        return this;
    }

}
