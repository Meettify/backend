package com.example.meettify.service.item;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.item.*;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.item.ItemImgEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.item.ItemImgRepository;
import com.example.meettify.repository.item.ItemRepository;
import com.example.meettify.repository.member.MemberRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Transactional
@RequiredArgsConstructor
@Log4j2
@Service
public class ItemServiceImpl implements ItemService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final S3ImageUploadService s3ImageUploadService;


    // 상품 등록 메서드
    @Override
    @TimeTrace
    public ResponseItemDTO createItem(CreateItemServiceDTO item, List<MultipartFile> files, String memberEmail) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);
            if (findMember != null) {
                List<ResponseItemImgDTO> itemImages = uploadItemImages(files);
                ItemEntity itemEntity = ItemEntity.createEntity(item);
                List<ItemImgEntity> imagesEntity = ItemImgEntity.createEntityList(itemImages, itemEntity);
                itemEntity.getImages().addAll(imagesEntity);
                ItemEntity saveItem = itemRepository.save(itemEntity);

                return ResponseItemDTO.changeDTO(saveItem);
            }
            throw new MemberException("회원이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new ItemException("상품 생성 실패 : " + e.getMessage());
        }
    }

    private List<ResponseItemImgDTO> uploadItemImages(List<MultipartFile> files) throws IOException {
        return s3ImageUploadService.upload("product", files, (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                ResponseItemImgDTO.builder()
                        .originalImgName(oriFileName)
                        .uploadImgName(uploadFileName)
                        .uploadImgPath(uploadFilePath)
                        .uploadImgUrl(uploadFileUrl)
                        .build()
        );
    }

    // 상품 수정 메서드
    @Override
    @TimeTrace
    public ResponseItemDTO updateItem(Long itemId,
                                      UpdateItemServiceDTO updateItemDTO,
                                      List<MultipartFile> files) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

            // 만약 남겨야 할 이미지 ID가 비어있다면, 모든 이미지를 삭제
            if (updateItemDTO.getRemainImgId().isEmpty()) {
                // s3에서 해당 상품의 이미지 모두 삭제
                findItem.getImages().forEach(
                        img -> s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName())
                );
                // 리스트에서 모든 이미지를 삭제
                requireNonNull(findItem).getImages().clear();
            } else {
                // 먼저 S3에서 삭제해야 할 이미지 처리
                findItem.getImages().forEach(img -> {
                    if (!updateItemDTO.getRemainImgId().contains(img.getItemImgId())) {
                        s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName()); // S3에서 삭제
                    }
                });

                // 이미지를 필터링하여 남겨야 할 이미지만 남김
                findItem.remainImgId(updateItemDTO.getRemainImgId());
            }

            // s3에 추가할 이미지
            List<ResponseItemImgDTO> responseImages = uploadItemImages(files);
            // 이미지들을 엔티티로 변환
            List<ItemImgEntity> imagesEntity = ItemImgEntity.createEntityList(responseImages, findItem);
            // 새로운 이미지들 추가
            findItem.updateItem(updateItemDTO, imagesEntity);
            ItemEntity saveItem = itemRepository.save(findItem);
            return ResponseItemDTO.changeDTO(saveItem);
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            throw new ItemException("Failed to update the item.");
        }
    }

    // 상품 조회 메서드
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public ResponseItemDTO getItem(Long itemId) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

            ResponseItemDTO response = ResponseItemDTO.changeDTO(findItem);
            log.info("response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch item with id {}: {}", itemId, e.getMessage());
            throw new ItemException("상품 조회 실패, 원인 :" + e.getMessage());
        }
    }

    // 상품 삭제 메서드
    @Override
    @TimeTrace
    public String deleteItem(Long itemId) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

            // s3에 이미지 삭제
            findItem.getImages().forEach(
                    img -> s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName())
            );
            // 상품 삭제
            itemRepository.deleteById(itemId);
            log.info("Successfully deleted item with id: {}", itemId);
            return "상품을 삭제했습니다.";
        } catch (Exception e) {
            throw new ItemException("상품 삭제하는데 실패했습니다. 원인 : " + e.getMessage());
        }
    }

    // 여러 상품을 페이징 처리해서 가져오는 메서드 : 여러 조건 검색 가능
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseItemDTO> searchItems(ItemSearchCondition condition, Pageable page) {
        try {

            Page<ItemEntity> itemsPage = itemRepository.itemsSearch(condition, page);
            log.info("itemsPage: {}", itemsPage.getContent());
            log.info("itemsPage.size: {}", itemsPage.getContent().size());
            log.info("itemsPage.number: {}", itemsPage.getNumber());

            if (itemsPage.isEmpty()) {
                // 데이터가 없는 경우 빈 페이지를 반환
                return new PageImpl<>(Collections.emptyList(), page, 0);
            }

            return itemsPage.map(ResponseItemDTO::changeDTO);
        } catch (Exception e) {
            log.error("error : " + e.getMessage());
            throw new EntityNotFoundException("상품 조회에 실패하였습니다.\n" + e.getMessage());
        }
    }
}
