package org.phong.horizon.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIChatResponse {
    private String message;
    private String action;
    private Map<String, Object> parameters;
}
