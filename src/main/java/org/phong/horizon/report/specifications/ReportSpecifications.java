package org.phong.horizon.report.specifications;

import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.enums.ModerationItemType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportSpecifications {

    public static Specification<Report> withDynamicQuery(
            UUID reporterId,
            UUID reportedUserId,
            ModerationStatus status,
            ModerationItemType itemType,
            UUID itemId
    ) {
        return (root, _, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (reporterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reporter").get("id"), reporterId));
            }
            if (reportedUserId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reportedUser").get("id"), reportedUserId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (itemType != null) {
                predicates.add(criteriaBuilder.equal(root.get("itemType"), itemType));
                if (itemId != null) {
                    switch (itemType) {
                        case POST:
                            predicates.add(criteriaBuilder.equal(root.get("post").get("id"), itemId));
                            break;
                        case COMMENT:
                            predicates.add(criteriaBuilder.equal(root.get("comment").get("id"), itemId));
                            break;
                        case USER:
                            // If itemType is USER, itemId refers to the reportedUser's ID.
                            // This is now handled by the simplified reportedUserId check above if itemId is the same as reportedUserId.
                            // If a different itemId is passed for itemType USER, it implies reportedUserId should match itemId.
                            predicates.add(criteriaBuilder.equal(root.get("reportedUser").get("id"), itemId));
                            break;
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
