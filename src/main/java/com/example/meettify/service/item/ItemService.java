package com.example.meettify.service.item;

import com.example.meettify.dto.item.CreateItemServiceDTO;
import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.item.UpdateItemServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    // 상품 등록
    ResponseItemDTO createItem(CreateItemServiceDTO item,
                               List<MultipartFile> files,
                               String memberEmail);

    // 상품 수정
    ResponseItemDTO updateItem(Long itemId,
                               UpdateItemServiceDTO updateItemDTO,
                               List<MultipartFile> files);

    // 상품 상세 페이지
    ResponseItemDTO getItem(Long itemId);

    String deleteItem(Long itemId);

    // 동적 검색
    Slice<ResponseItemDTO> searchItems(ItemSearchCondition condition,
                                       Long lastItemId,
                                       int size,
                                       String sort);

    // 상품 추천
    List<ResponseItemDTO> recommendItemsBySearchHistory(String email);

    // 신청 상품 확인
    Slice<ResponseItemDTO> requestItemList(Long lastItemId,
                                          int size,
                                          String sort);

    // 상품 컨펌
    String changeStatusItem(Long itemId);

    // 상품 카운트
    long countItems();

    // 상품 팬매 TOP 10가져오기
    List<ResponseItemDTO> getTopItemIds(int topCount);

}
