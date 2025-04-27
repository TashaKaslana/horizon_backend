package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.comment.infrastructure.persistence.entities.ReportComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportCommentRepository extends JpaRepository<ReportComment, UUID> {
    Page<ReportComment> findAllByUser_Id(UUID userId, Pageable pageable);
}