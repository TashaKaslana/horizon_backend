package org.phong.horizon.historyactivity.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.HistoryActivityBusinessError;
import org.phong.horizon.historyactivity.exceptions.ActivityTypeNotFoundException;
import org.phong.horizon.historyactivity.infrstructure.mapstruct.HistoryActivityMapper;
import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.ActivityType;
import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity;
import org.phong.horizon.historyactivity.infrstructure.persistence.repositories.ActivityTypeRepository;
import org.phong.horizon.historyactivity.infrstructure.persistence.repositories.HistoryActivityRepository;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class ActivityLoggingService {
    private final ActivityTypeRepository activityTypeRepository;
    private final HistoryActivityRepository historyActivityRepository;
    private final HistoryActivityMapper historyActivityMapper;
    private final UserService userService;

    @Transactional
    public void logActivity(CreateHistoryActivity request) {
        logToConsole(request);

        try {
            HistoryActivity historyActivity = historyActivityMapper.toEntity(request);

            ActivityType activityType = activityTypeRepository.findByCode(request.activityTypeCode().getCode())
                    .orElseThrow(() -> new ActivityTypeNotFoundException(
                            HistoryActivityBusinessError.ACTIVITY_TYPE_CODE_NOT_FOUND,
                            request.activityTypeCode().getCode()
                    ));
            historyActivity.setActivityType(activityType);
            historyActivity.setUser(userService.getRefById(request.userId()));

            historyActivityRepository.save(historyActivity);
        } catch (Exception e) {
            log.error("Failed to log: User: {}, Code: {}, Error Message: {}",
                    request.userId(), request.activityTypeCode(), e.getMessage()
            );
        }
    }

    private static void logToConsole(CreateHistoryActivity request) {
        //UA is user agent
        log.debug("ACTIVITY LOGGING : UserId {}, Code: {}, Desc: '{}', Target: [Type :{}, ID :{}], IP: {}, UA: {}, Detail: {}",
                request.userId(),
                request.activityTypeCode(),
                request.activityDescription(),
                request.targetType() != null ? request.targetType() : "N/A",
                request.targetId() != null ? request.targetId() : "N/A",
                request.ipAddress() != null ? request.ipAddress() : "N/A",
                request.userAgent() != null ? request.userAgent().substring(0, Math.min(request.userAgent().length(), 30)) + "..." : "N/A",
                request.activityDetail() != null ? request.activityDetail() : "N/A");
    }
}
