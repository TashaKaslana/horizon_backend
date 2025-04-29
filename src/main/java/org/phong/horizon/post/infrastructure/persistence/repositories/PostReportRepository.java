package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostReportRepository extends JpaRepository<PostReport, UUID> {
}