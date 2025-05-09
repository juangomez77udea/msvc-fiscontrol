package com.udea.msvc_supples.repositories;

import com.udea.msvc_supples.models.ESupplyType;
import com.udea.msvc_supples.models.SupplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRepository extends JpaRepository<SupplyEntity, Long> {

    // Cambiado para devolver una lista en lugar de Optional
    List<SupplyEntity> findBySuppliesName(String suppliesName);

    // Actualizado para devolver una lista
    @Query("select s from SupplyEntity s where s.suppliesName = ?1")
    List<SupplyEntity> getName(String suppliesName);

    List<SupplyEntity> findByType(ESupplyType type);
    List<SupplyEntity> findByStage(String stage);

}
