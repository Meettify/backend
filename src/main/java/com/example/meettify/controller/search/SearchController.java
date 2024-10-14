package com.example.meettify.controller.search;


import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchResponseDTO;
import com.example.meettify.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api/v1/search")
@Tag(name = "search", description = "전체 검색하는 API")
@RequiredArgsConstructor
public class SearchController implements  SearchControllerDocs {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> getSearch(SearchCondition searchCondition, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            if(email.isEmpty()){
                email = "";
            }
             SearchResponseDTO searchResponseDTO = searchService.searchResponseDTO(searchCondition, email);
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("에러가 발생함." + e.getMessage());
        }

    }

}
