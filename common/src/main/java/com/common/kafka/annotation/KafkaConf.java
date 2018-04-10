package com.common.kafka.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface KafkaConf {
    String topic() default "";
    String groupid() default "";
    int threads() default 1;
}
