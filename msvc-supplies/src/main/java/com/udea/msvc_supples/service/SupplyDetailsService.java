package com.udea.msvc_supples.service;

import com.udea.msvc_supples.models.ESupplyType;
import com.udea.msvc_supples.models.SupplyEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SupplyDetailsService {

    List<SupplyEntity> getAllSupplies();
    Optional<SupplyEntity> getSupplyById(Long id);
    SupplyEntity createSupply(SupplyEntity supply);
    SupplyEntity updateSupply(Long id, SupplyEntity supply);
    void deleteSupply(Long id);

    // métodos para consultas específicas
    List<SupplyEntity> getSuppliesByType(ESupplyType type);
    List<SupplyEntity> getSuppliesByStage(String stage);
    Map<ESupplyType, Integer> getInventory();

    List<SupplyEntity> getSupplyByName(String name);
}
