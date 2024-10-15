package com.example.meettify.controller.item;

import com.example.meettify.dto.item.*;
import com.example.meettify.service.item.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController implements ItemControllerDocs{
    private final ItemService itemService;
    private final ModelMapper modelMapper;

    // 상품 등록
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createItem(@Validated @RequestPart CreateItemDTO item,
                                        @RequestPart(value = "files", required = false)List<MultipartFile> files,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult);
            }

            CreateItemServiceDTO changeServiceDTO = modelMapper.map(item, CreateItemServiceDTO.class);

            String email = userDetails.getUsername();
            ResponseItemDTO response = itemService.createItem(changeServiceDTO, files, email);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상품 수정
    @Override
    @PutMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestPart UpdateItemDTO item,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String authority = userDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();
            UpdateItemServiceDTO changeServiceDTO = modelMapper.map(item, UpdateItemServiceDTO.class);
            log.info("changeServiceDTO : " + changeServiceDTO);
            ResponseItemDTO response = itemService.updateItem(itemId, changeServiceDTO, files);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상품 상세 정보
    @Override
    @GetMapping("/{itemId}")
    public ResponseEntity<?> itemDetail(@PathVariable Long itemId) {
        try {
            ResponseItemDTO item = itemService.getItem(itemId);
            log.info("item : " + item);
            return ResponseEntity.ok().body(item);
        } catch (EntityNotFoundException e) {
            log.error("존재하지 않는 상품입니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 상품입니다.");
        }
    }

    // 상품 삭제
    @Override
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String result = itemService.deleteItem(itemId);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // http://localhost:8080/api/v1/items/search?name=당&page=1&sort=itemId,asc&place=종로
    @Override
    @GetMapping("/search")
    public ResponseEntity<?> searchItemsConditions(Pageable pageable,
                                                   ItemSearchCondition condition) {
        try {
            log.info("condition : " + condition);
            Page<ResponseItemDTO> items = itemService.searchItems(condition, pageable);
            log.info("상품 조회 {}", items);

            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", items.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", items.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", items.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", items.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", items.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", items.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", items.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", items.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
