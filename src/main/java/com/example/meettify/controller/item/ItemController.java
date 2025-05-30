package com.example.meettify.controller.item;

import com.example.meettify.dto.item.*;
import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.service.item.ItemService;
import com.example.meettify.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController implements ItemControllerDocs{
    private final ItemService itemService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

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
            // 새로 담긴 상품을 관리자들에게 알림을 보냄
            String email = userDetails.getUsername();
            ResponseItemDTO response = itemService.createItem(changeServiceDTO, files, email);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 상품 수정
    @Override
    @PutMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestPart UpdateItemDTO item,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            UpdateItemServiceDTO changeServiceDTO = modelMapper.map(item, UpdateItemServiceDTO.class);
            log.info("changeServiceDTO : " + changeServiceDTO);
            ResponseItemDTO response = itemService.updateItem(itemId, changeServiceDTO, files);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
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
            throw new ItemException(e.getMessage());
        }
    }

    // 상품 삭제
    @Override
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String result = itemService.deleteItem(itemId);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // http://localhost:8080/api/v1/items/search?title=당&page=1&sort=itemId,asc
    @Override
    @GetMapping("/search")
    public ResponseEntity<?> searchItemsConditions(@RequestParam(required = false) Long lastItemId,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(value = "sort", required = false) String sort,
                                                   @RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "minPrice", required = false, defaultValue = "0") int minPrice,
                                                   @RequestParam(value = "maxPrice", required = false, defaultValue = "0") int maxPrice,
                                                   @RequestParam(value = "category", required = false) Category category,
                                                   @RequestParam(value = "status", required = false)ItemStatus status
                                                   ) {
        try {
            ItemSearchCondition condition = ItemSearchCondition.builder()
                    .title(title)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .category(category)
                    .status(status)
                    .build();

            Slice<ResponseItemDTO> items = itemService.searchItems(condition, lastItemId, size, sort);
            log.info("condition : " + condition);
            log.info("상품 조회 {}", items);

            Map<String, Object> response = responseItemInfo(items);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    private static @NotNull Map<String, Object> responseItemInfo(Slice<ResponseItemDTO> items) {
        Map<String, Object> response = new HashMap<>();
        // 현재 페이지의 아이템 목록
        response.put("items", items.getContent());
        // 현재 페이지 번호
        response.put("nowPageNumber", items.getNumber() + 1);
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
        return response;
    }

    // 상품 추천
    @Override
    @GetMapping("/recommend-items")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> recommendItems(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername() != null ? userDetails.getUsername() : null;
            List<ResponseItemDTO> response = itemService.recommendItemsBySearchHistory(email);
            log.info("response {}", response);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 신청 상품 확인
    @Override
    @GetMapping("/item-list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getItemList(Long lastItemId,
                                         int size,
                                         String sort) {
        try {
            Slice<ResponseItemDTO> items = itemService.requestItemList(lastItemId, size, sort);
            log.info("condition : " + items);
            log.info("상품 조회 {}", items);

            Map<String, Object> response = responseItemInfo(items);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 상품 컨펌
    @Override
    @GetMapping("/confirm/{itemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeItemStatus(@PathVariable Long itemId) {
        try {
            String response = itemService.changeStatusItem(itemId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 모든 상품 카운트
    @Override
    @GetMapping("/count-items")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> countItems() {
        try {
            long responseCount = itemService.countItems();
            return ResponseEntity.ok().body(responseCount);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 상품 TOP 10
    @Override
    @GetMapping("/top")
    public ResponseEntity<?> getTopItems() {
        try {
            List<ResponseItemDTO> result = itemService.getTopItemIds(10);
            log.debug("반환할 값 {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ItemException(e.getMessage());
        }
    }
}
