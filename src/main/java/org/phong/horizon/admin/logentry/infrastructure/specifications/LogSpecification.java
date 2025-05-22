package org.phong.horizon.admin.logentry.infrastructure.specifications;

import jakarta.persistence.criteria.Predicate;
import org.phong.horizon.admin.logentry.dtos.LogSearchCriteriaDto;
import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogSpecification {

    public Specification<LogEntry> filterByCriteria(LogSearchCriteriaDto criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getSeverities() != null && !criteria.getSeverities().isEmpty()) {
                predicates.add(root.get("severity").in(criteria.getSeverities()));
            }

            if (criteria.getMessageContains() != null && !criteria.getMessageContains().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("message")),
                        "%" + criteria.getMessageContains().toLowerCase() + "%"));
            }

            if (criteria.getSource() != null && !criteria.getSource().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("source"), criteria.getSource()));
            }

            if (criteria.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), criteria.getUserId()));
            }

            if (criteria.getTimestampAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), criteria.getTimestampAfter()));
            }

            if (criteria.getTimestampBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), criteria.getTimestampBefore()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

