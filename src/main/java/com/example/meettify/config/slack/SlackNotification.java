package com.example.meettify.config.slack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 *   worker  : 유요한
 *   work    : 이 어노테이션을 사용한 메서드는 AOP로 감싸져서 슬랙 알림을 받게 됩니다.
 *   date    : 2024/10/18
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SlackNotification {
}
