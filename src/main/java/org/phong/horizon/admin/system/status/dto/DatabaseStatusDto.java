package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the status of the database connection.
 */
@Setter
@Getter
@NoArgsConstructor
public class DatabaseStatusDto {
    private String status;
    private String error;


    public DatabaseStatusDto(String status) {
        this.status = status;
    }

    public DatabaseStatusDto(String status, String error) {
        this.status = status;
        this.error = error;
    }

}
