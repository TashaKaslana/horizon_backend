package org.phong.horizon.historyactivity.infrstructure.persistence.enitities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "activity_types", uniqueConstraints = {
        @UniqueConstraint(name = "activity_types_code_key", columnNames = {"code"})
})
public class ActivityType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_types_id_gen")
    @SequenceGenerator(name = "activity_types_id_gen", sequenceName = "activity_types_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

}