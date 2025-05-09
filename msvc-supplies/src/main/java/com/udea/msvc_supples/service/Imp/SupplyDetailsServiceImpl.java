package com.udea.msvc_supples.service.Imp;

import com.udea.msvc_supples.models.ESupplyType;
import com.udea.msvc_supples.models.SupplyEntity;
import com.udea.msvc_supples.repositories.SupplyRepository;
import com.udea.msvc_supples.service.SupplyDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplyDetailsServiceImpl implements SupplyDetailsService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Override
    public List<SupplyEntity> getAllSupplies() {
        return supplyRepository.findAll();
    }

    @Override
    public Optional<SupplyEntity> getSupplyById(Long id) {
        return supplyRepository.findById(id);
    }

    @Override
    public SupplyEntity createSupply(SupplyEntity supply) {
        return supplyRepository.save(supply);
    }

    @Override
    public SupplyEntity updateSupply(Long id, SupplyEntity supply) {
        if (supplyRepository.existsById(id)) {
            supply.setId(id);
            return supplyRepository.save(supply);
        }
        return null;
    }

    @Override
    public void deleteSupply(Long id) {
        supplyRepository.deleteById(id);
    }

    @Override
    public List<SupplyEntity> getSuppliesByType(ESupplyType type) {
        return supplyRepository.findByType(type);
    }

    @Override
    public List<SupplyEntity> getSuppliesByStage(String stage) {
        return supplyRepository.findByStage(stage);
    }

    @Override
    public Map<ESupplyType, Integer> getInventory() {
        List<SupplyEntity> supplies = supplyRepository.findAll();

        return supplies.stream()
                .collect(Collectors.groupingBy(
                        SupplyEntity::getType,
                        Collectors.summingInt(SupplyEntity::getSuppliesQuantity)
                ));
    }

    @Override
    public List<SupplyEntity> getSupplyByName(String name) {
        return supplyRepository.findBySuppliesName(name);
    }

}
