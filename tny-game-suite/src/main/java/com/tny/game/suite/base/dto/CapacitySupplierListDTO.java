package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacitySupplier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_ITEM_LIST_DTO)
@DTODoc(value = "游戏能力相关对象列表DTO")
public class CapacitySupplierListDTO {

    @VarDoc("能力相关对象列表")
    @ProtoExField(1)
    private List<CapacitySupplierDTO> capacityItems;

    public static CapacitySupplierListDTO suppliers2DTO(Collection<? extends CapacitySupplier> suppliers) {
        CapacitySupplierListDTO dto = new CapacitySupplierListDTO();
        dto.capacityItems = suppliers.stream()
                .filter(CapacitySupplier::isSupplying)
                .map(CapacitySupplierDTO::supplier2DTO)
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

    public CapacitySupplierListDTO addAllSuppliers(Collection<CapacitySupplier> suppliers) {
        suppliers.forEach(this::addSupplier);
        return this;
    }

    public CapacitySupplierListDTO addAllDTOs(Collection<CapacitySupplierDTO> supplierDTOs) {
        supplierDTOs.forEach(this::addDTO);
        return this;
    }


}
