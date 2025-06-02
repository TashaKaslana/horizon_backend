package org.phong.horizon.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * DTO for handling user login status updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginStatusDto {
    private Boolean isLogin;
    private Instant lastLogin;
}
