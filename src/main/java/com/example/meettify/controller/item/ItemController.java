package com.example.meettify.controller.item;

import com.example.meettify.dto.item.*;
import com.example.meettify.service.item.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController implements ItemControllerDocs{
    private final ItemService itemService;
    private final ModelMapper modelMapper;

    // 상품 등록
    @PostMapping("")
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
    @PutMapping("/{itemId}")
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
            ResponseItemDTO response = itemService.updateItem(itemId, changeServiceDTO, files, email, authority);
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
}
