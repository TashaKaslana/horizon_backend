package org.phong.horizon.ai.controller;

import lombok.AllArgsConstructor;
import org.phong.horizon.ai.dto.ChatRequest;
import org.phong.horizon.ai.dto.AIChatResponse;
import org.phong.horizon.ai.service.AIChatService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ai/chat")
class AIChatController {
    private final AIChatService chatService;

    @PostMapping()
    public ResponseEntity<RestApiResponse<AIChatResponse>> chatWithOpenRouter(@RequestBody ChatRequest chatRequest) {
        AIChatResponse response = chatService.processChat(chatRequest.getMessage());
        return  RestApiResponse.success(response);
    }
}
