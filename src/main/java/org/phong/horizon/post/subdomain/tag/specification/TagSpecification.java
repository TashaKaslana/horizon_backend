package org.phong.horizon.post.subdomain.tag.specification;

import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.springframework.data.jpa.domain.Specification;

public class TagSpecification {

    public static Specification<Tag> slugEquals(String slug) {
        return (root, _, criteriaBuilder) ->
                slug == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("slug"), slug);
    }

    public static Specification<Tag> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? criteriaBuilder.conjunction() : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }
}

