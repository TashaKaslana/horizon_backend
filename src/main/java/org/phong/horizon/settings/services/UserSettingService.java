package org.phong.horizon.settings.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.settings.dtos.UserSettingDto;
import org.phong.horizon.settings.infrastructure.persistence.entities.UserSetting;
import org.phong.horizon.settings.infrastructure.persistence.repositories.UserSettingRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserSettingRepository repository;
    private final AuthService authService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public UserSettingDto getMySetting() {
        UUID userId = authService.getUserIdFromContext();
        return repository.findByUser_Id(userId)
                .map(entity -> new UserSettingDto(entity.getPreferences()))
                .orElseGet(() -> {
                    log.info("User {} has no settings, returning defaults", userId);
                    return new UserSettingDto(defaultPreferences());
                });
    }

    @Transactional
    public UserSettingDto updateMySetting(UserSettingDto dto) {
        UUID userId = authService.getUserIdFromContext();
        UserSetting setting = repository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userService.getRefById(userId);
                    UserSetting ns = new UserSetting();
                    ns.setUser(user);
                    return ns;
                });
        if (setting.getPreferences() == null) {
            setting.setPreferences(new HashMap<>());
        }
        if (dto.preferences() != null) {
            setting.getPreferences().putAll(dto.preferences());
        }
        UserSetting saved = repository.save(setting);
        return new UserSettingDto(saved.getPreferences());
    }

    private Map<String, Object> defaultPreferences() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("darkMode", false);
        defaults.put("allowNotification", true);
        defaults.put("language", null);
        return defaults;
    }
}
