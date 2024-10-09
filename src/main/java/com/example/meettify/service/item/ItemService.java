package com.example.meettify.service.item;

import com.example.meettify.dto.item.CreateItemServiceDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.item.UpdateItemServiceDTO;
import org.springframework.security.core.userdetails.UserDetails;
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
                               List<MultipartFile> files,
                               String memberEmail,
                               String role);

    // 상품 상세 페이지
    ResponseItemDTO getItem(Long itemId);

    String deleteItem(Long itemId);
}
