package com.udea.msvc_fishcontrol.repositories.user;

import com.udea.msvc_fishcontrol.models.user.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
}
