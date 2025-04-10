package org.phong.horizon.historyactivity.annotations;

import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
    ActivityTypeCode activityCode();
    String description() default "";
    SystemCategory targetType();
    String targetIdExpression() default "";
}
