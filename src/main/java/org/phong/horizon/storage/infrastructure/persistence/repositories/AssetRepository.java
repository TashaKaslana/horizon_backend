package org.phong.horizon.storage.infrastructure.persistence.repositories;

import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
  Optional<Asset> findByPublicId(String publicId);

  @Query("SELECT a.createdBy FROM Asset a where a.publicId = :publicId")
  UUID getCreatedByIdByPublicId(String publicId);
}