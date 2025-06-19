package org.phong.horizon.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ai.dto.AIChatResponse;
import org.phong.horizon.ai.exception.AICommunicationException;
import org.phong.horizon.ai.functions.AIFunctions;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AIChatService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final AIFunctions aiFunctions;

    public AIChatResponse processChat(String message) {
        try {
            String template = loadPromptTemplate();

            String renderedPrompt = template
                    .replace("{input}", message)
                    .replace("{version}", "1.0.0")
                    .replace("{role}", "admin");

            ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();

            ToolCallingChatOptions chatOptions = ToolCallingChatOptions.builder()
                    .toolCallbacks(ToolCallbacks.from(aiFunctions))
                    .internalToolExecutionEnabled(false)
                    .build();

            Prompt prompt = new Prompt(
                    List.of(
                            new SystemMessage(renderedPrompt),
                            new UserMessage(message)
                    ),
                    chatOptions
            );

            ChatResponse chatResponse = chatModel.call(prompt);

            while (chatResponse.hasToolCalls()) {
                ToolExecutionResult result = toolCallingManager.executeToolCalls(prompt, chatResponse);
                prompt = new Prompt(result.conversationHistory(), chatOptions);
                chatResponse = chatModel.call(prompt);
            }

            String outputText = chatResponse.getResult().getOutput().getText();
            log.warn("AI returned outputText: {}", outputText);

            try {
                assert outputText != null;
                String cleaned = getString(outputText);

                log.debug("Parsing final cleaned JSON: {}", cleaned);
                return objectMapper.readValue(cleaned, AIChatResponse.class);

            } catch (Exception e) {
                log.warn("Unstructured AI response, falling back to raw message.");
                return new AIChatResponse(outputText, null, null);
            }
        } catch (Exception e) {
            log.error("Error processing chat request via Spring AI: {}", e.getMessage(), e);
            throw new AICommunicationException("AI service communication error", e);
        }
    }

    private static String getString(String outputText) {
        String cleaned = outputText.trim();

        // Handle prefix like "json\n"
        if (cleaned.toLowerCase().startsWith("json")) {
            cleaned = cleaned.substring(4).trim();
        }

        // Remove markdown formatting if any
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.replace("```json", "").replace("```", "").trim();
        }

        // Unescape if AI double-encoded
        if (cleaned.startsWith("\"{")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).replace("\\\"", "\"");
        }
        return cleaned;
    }

    private String loadPromptTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("prompts/chat-prompt.st");
        byte[] bytes = resource.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
