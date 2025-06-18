package org.phong.horizon.core.services;

import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

@Setter
public class CustomHeaderLocaleResolver implements LocaleResolver {
    private static final String LANGUAGE_HEADER = "X-Language";
    private Locale defaultLocale = Locale.ENGLISH;

    @SuppressWarnings("NullableProblems")
    @Override
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        String lang = request.getHeader(LANGUAGE_HEADER);
        if (StringUtils.hasText(lang)) {
            return Locale.forLanguageTag(lang);
        }
        return defaultLocale;
    }


    @Override
    public void setLocale(@NonNull HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // Not needed for stateless REST APIs
    }

}