package org.phong.horizon.core.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocalizationProvider {

    private static MessageSourceAccessor accessor;

    @Autowired
    public LocalizationProvider(MessageSource messageSource) {
        LocalizationProvider.accessor = new MessageSourceAccessor(messageSource);
    }

    public static String getMessage(String key, Object... args) {
        if (accessor == null) {
            log.warn("LocalizationProvider not initialized! Returning key [{}]", key);
            return key;
        }
        String msg = accessor.getMessage(key, args, LocaleContextHolder.getLocale());
        log.debug("Resolved message for key [{}] in locale [{}]: {}", key, LocaleContextHolder.getLocale(), msg);
        return msg;
    }
}

