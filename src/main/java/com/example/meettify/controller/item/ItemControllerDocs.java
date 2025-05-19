package com.example.meettify.controller.item;

import com.example.meettify.dto.item.CreateItemDTO;
import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.item.UpdateItemDTO;
import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "item", description = "상퓸 API")
public interface ItemControllerDocs {
    @Operation(summary = "상품 등록", description = "상품 등록하는 API")
    ResponseEntity<?> createItem(CreateItemDTO item,
                                 List<MultipartFile> files,
                                 BindingResult bindingResult,
                                 UserDetails userDetails);

    @Operation(summary = "상품 수정", description = "상품 수정하는 API")
    ResponseEntity<?> updateItem(Long itemId,
                                 UpdateItemDTO item,
                                 List<MultipartFile> files);

    @Operation(summary = "상품 상세 정보", description = "상품 상세정보를 보는 API")
    ResponseEntity<?> itemDetail(Long itemId);

    @Operation(summary = "상품 삭제", description = "상품 삭제하는 API")
    ResponseEntity<?> deleteItem(Long itemId, UserDetails userDetails);

    @Operation(summary = "상품 페이징", description = "여러 조건으로 상품을 페이징 처리해서 가져오는 API")
    ResponseEntity<?> searchItemsConditions(Pageable pageable,
                                            String title,
                                            int minPrice,
                                            int maxPrice,
                                            Category category,
                                            ItemStatus status);

    @Operation(summary = "상품 추천", description = "모임 게시글에서 사용할 상품 추천 API")
    ResponseEntity<?> recommendItems(UserDetails userDetails);

    @Operation(summary = "대기 중인 상품 보기", description = "상품 상태가 WAIT인 상품 리시트 보기 API")
    ResponseEntity<?> getItemList(Pageable page);

    @Operation(summary = "상품 상태 변경하기", description = "상품을 컨펌하는 기능 API")
    ResponseEntity<?> changeItemStatus(Long itemId);

    @Operation(summary = "전체 상품 수", description = "전체 상품 수 카운트 API")
    ResponseEntity<?> countItems();

    @Operation(summary = "상품 랭킹", description = "상품 TOP10 보여주는 API")
    ResponseEntity<?> getTopItems();
}
