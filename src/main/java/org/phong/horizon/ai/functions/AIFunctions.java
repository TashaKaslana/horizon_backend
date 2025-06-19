package org.phong.horizon.ai.functions;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class AIFunctions {

    @Tool(name = "navigate", description = "Navigate the frontend to a specified route or page.")
    public String navigate(String route) {
        System.out.println("🧭 Navigating to: " + route);
        return "Navigated to route: " + route;
    }

    @Tool(name = "toggleTheme", description = "Toggle the frontend theme between light and dark modes.")
    public String toggleTheme(String theme) {
        if (!theme.equalsIgnoreCase("light") && !theme.equalsIgnoreCase("dark")) {
            return "⚠️ Unknown theme: " + theme + ". Please use 'light' or 'dark'.";
        }
        System.out.println("🌗 Switching theme to: " + theme);
        return "Switched to " + theme + " mode.";
    }

    @Tool(name = "highlight", description = "Highlights a specific frontend element by ID.")
    public String highlight(String id) {
        System.out.println("✨ Highlighting element with ID: " + id);
        return "Element with ID '" + id + "' has been highlighted.";
    }

    @Tool(name = "openSetting", description = "Open the settings panel in the frontend UI.")
    public String openSetting() {
        System.out.println("⚙️ Opening settings panel");
        return "Settings panel opened.";
    }

    @Tool(name = "closeSetting", description = "Close the settings panel in the frontend UI.")
    public String closeSetting() {
        System.out.println("❌ Closing settings panel");
        return "Settings panel closed.";
    }

    @Tool(name = "getCurrentTime", description = "Get the current time in the frontend UI.")
    public String getCurrentTime() {
        String currentTime = java.time.LocalTime.now().toString();
        System.out.println("🕒 Current time is: " + currentTime);
        return "Current time is: " + currentTime;
    }

    @Tool(name = "getCurrentDate", description = "Get the current date in the frontend UI.")
    public String getCurrentDate() {
        String currentDate = java.time.LocalDate.now().toString();
        System.out.println("📅 Current date is: " + currentDate);
        return "Current date is: " + currentDate;
    }
}
