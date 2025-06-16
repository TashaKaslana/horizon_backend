package org.phong.horizon.core.config;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class LocalizationProvider implements MessageSourceAware {
    private static MessageSourceAccessor accessor;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        accessor = new MessageSourceAccessor(messageSource);
    }

    public static String getMessage(String key, Object... args) {
        if (accessor == null) {
            return key;
        }
        return accessor.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
