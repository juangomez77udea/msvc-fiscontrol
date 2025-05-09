package com.udea.msvc_fishcontrol.repositories.user;

import com.udea.msvc_fishcontrol.models.user.RefreshTokenEntity;
import com.udea.msvc_fishcontrol.models.user.RoleEntity;
import com.udea.msvc_fishcontrol.models.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    int deleteByUser(UserEntity user);

    List<RefreshTokenEntity> findAllByUserAndRevokedFalseAndUsedFalse(UserEntity user);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.expiryDate < ?1")
    void deleteAllExpiredTokens(Instant now);

    Optional<RefreshTokenEntity> findByPreviousToken(String previousToken);

}
