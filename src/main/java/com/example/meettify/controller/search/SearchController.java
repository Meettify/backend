package com.example.meettify.controller.search;


import com.example.meettify.dto.search.*;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.not_found.ResourceNotFoundException;
import com.example.meettify.exception.timeout.RequestTimeoutException;
import com.example.meettify.service.search.RedisSearchLogService;
import com.example.meettify.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController implements  SearchControllerDocs {
    private final SearchService searchService;
    private final RedisSearchLogService redisSearchLogService;

    @Override
    @GetMapping
    public ResponseEntity<?> getSearch(SearchCondition searchCondition,
                                       @Nullable @AuthenticationPrincipal() UserDetails userDetails) throws Exception {
        try {
            log.debug("검색 컨트롤러 동작");
            String email = (userDetails != null) ? userDetails.getUsername() : null;  // 비로그인 사용자에 대해 빈 문자열 처리
            SearchResponseDTO searchResponseDTO = searchService.searchResponseDTO(searchCondition, email);
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (RequestTimeoutException e) {
            throw new RequestTimeoutException(e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (BoardException e) {
            throw new BoardException(e.getMessage());
        } catch (ItemException e) {
            throw new ItemException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @GetMapping("/searchLogs")
    public ResponseEntity<?> findRecentSearchLog(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String email = userDetails.getUsername();
            List<SearchLog> response = redisSearchLogService.findRecentSearchLogs(email);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }

    @DeleteMapping("/searchLog")
    public ResponseEntity<?> deleteRecentSearchLog(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody DeleteSearchLog deleteSearchLog) throws Exception {
        try {
            String email = userDetails.getUsername();
            redisSearchLogService.deleteRecentSearchLog(email, deleteSearchLog);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }
}
