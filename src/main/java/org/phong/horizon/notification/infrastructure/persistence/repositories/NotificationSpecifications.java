package org.phong.horizon.notification.infrastructure.persistence.repositories;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NotificationSpecifications {
    public static Specification<Notification> buildSpecification(
            UUID recipientUserId,
            NotificationFilterCriteria filters
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (recipientUserId != null) {
                predicates.add(criteriaBuilder.equal(root.get("recipientUser").get("id"), recipientUserId));
            }
            if (filters.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filters.getType()));
            }
            if (filters.getIsRead() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRead"), filters.getIsRead()));
            }
            if (filters.getIsDeleted() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), filters.getIsDeleted()));
            }
            if (filters.getIncludeRecipientUser() != null && Objects.requireNonNull(query).getResultType().equals(Notification.class)) {
                root.fetch("recipientUser", JoinType.LEFT);
            }
            if (filters.getCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filters.getCreatedAt()));
            }
            if (filters.getDeletedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deletedAt"), filters.getDeletedAt()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
