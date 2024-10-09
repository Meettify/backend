package com.example.meettify.service.item;

import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.item.CreateItemServiceDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.item.ResponseItemImgDTO;
import com.example.meettify.dto.item.UpdateItemServiceDTO;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.item.ItemImgEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.item.ItemImgRepository;
import com.example.meettify.repository.item.ItemRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    @Override
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

    @Override
    public ResponseItemDTO updateItem(Long itemId,
                                      UpdateItemServiceDTO updateItemDTO,
                                      List<MultipartFile> files,
                                      String memberEmail,
                                      String role) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));
            List<ItemImgEntity> findItemImg = itemImgRepository.findByItem_ItemId(itemId);

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

    @Override
    @Transactional(readOnly = true)
    public ResponseItemDTO getItem(Long itemId) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

            ResponseItemDTO response = ResponseItemDTO.changeDTO(findItem);
            log.info("response: {}", response);
            return response;
        } catch (Exception e) {
            throw new ItemException("상품 조회 실패, 원인 :" + e.getMessage());
        }
    }
}
