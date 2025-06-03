package org.phong.horizon.admin.system.maintenance.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.phong.horizon.admin.system.maintenance.services.MaintenanceService;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.core.services.AuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {
    private final MaintenanceService maintenanceService;
    private final AuthService authService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        if (maintenanceService.isMaintenance()) {
            if (!authService.hasRole(Role.ADMIN)) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("Service is under maintenance, please try again later.");
                return false;
            }
        }

        return true;
    }
}
