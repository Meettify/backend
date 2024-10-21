package com.example.meettify.config.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/*
 *   worker  : 유요한
 *   work    : 로깅을 구조적으로 변화시키기 위한 클래스
 *   date    : 2024/10/21
 * */
public class MdcLoggingInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String handlerName = handlerMethod.getMethod().getName();
        String methodName = handlerMethod.getBeanType().getSimpleName();
        String info = methodName + "." + handlerName;
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        MDC.put("serviceName", info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        MDC.clear();
    }
}
