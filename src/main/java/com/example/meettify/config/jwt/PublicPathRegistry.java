package com.example.meettify.config.jwt;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicPathRegistry {
    private static final Map<String, List<String>> publicPaths = new HashMap<>();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    static {
        // 회원
        publicPaths.put("/api/v1/members", Arrays.asList("GET", "POST"));
        publicPaths.put("/api/v1/members/email/{memberEmail}", List.of("GET"));
        publicPaths.put("/api/v1/members/nickName/{nickName}", List.of("GET"));
        publicPaths.put("/api/v1/members/login", List.of("POST"));
        // 상품
        publicPaths.put("/api/v1/items/search", List.of("GET"));
        // 커뮤니티
        publicPaths.put("/api/v1/community/search", List.of("GET"));
        publicPaths.put("/api/v1/community/communityList", List.of("GET"));
        // 댓글
        publicPaths.put("/api/v1/{communityId}/comment/commentList", List.of("GET"));
        // 공지
        publicPaths.put("/api/v1/notice/noticeList", List.of("GET"));
        // 디폴트
        publicPaths.put("/", List.of("GET"));
        // 스웨거
        publicPaths.put("/swagger-resources/**", List.of("GET"));
        publicPaths.put("/swagger-ui/**", List.of("GET"));
        publicPaths.put("/v3/api-docs/**", List.of("GET"));
        publicPaths.put("/api/swagger-config", List.of("GET"));
        publicPaths.put("/api/logistics", List.of("GET"));
        // 액츄에이터
        publicPaths.put("/monitor/**", List.of("GET"));
        // 프로메테우스
        publicPaths.put("/prometheus", List.of("GET"));
        // 그라파나
        publicPaths.put("/grafana", List.of("GET"));
    }

    public static boolean isPublicPath(String requestURI, String method) {
        return publicPaths.entrySet().stream()
                .anyMatch(entry -> pathMatcher.match(entry.getKey(), requestURI) && entry.getValue().contains(method));
    }
}
