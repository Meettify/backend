    package com.example.meettify.service.item;

    import com.example.meettify.config.metric.TimeTrace;
    import com.example.meettify.config.s3.S3ImageUploadService;
    import com.example.meettify.dto.item.*;
    import com.example.meettify.dto.meet.category.Category;
    import com.example.meettify.dto.search.SearchLog;
    import com.example.meettify.entity.item.ItemEntity;
    import com.example.meettify.entity.item.ItemImgEntity;
    import com.example.meettify.entity.member.MemberEntity;
    import com.example.meettify.exception.item.ItemException;
    import com.example.meettify.exception.member.MemberException;
    import com.example.meettify.repository.jpa.cart.CartItemRepository;
    import com.example.meettify.repository.jpa.item.ItemRepository;
    import com.example.meettify.repository.jpa.member.MemberRepository;
    import com.example.meettify.service.search.RedisSearchLogService;
    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageImpl;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Slice;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.util.*;
    import java.util.stream.Collectors;

    import static java.util.Objects.requireNonNull;

    @Transactional
    @RequiredArgsConstructor
    @Service
    @Slf4j
    public class ItemServiceImpl implements ItemService {
        private final MemberRepository memberRepository;
        private final ItemRepository itemRepository;
        private final S3ImageUploadService s3ImageUploadService;
        private final RedisSearchLogService redisSearchLogService;
        private final CartItemRepository cartItemRepository;
        private final RedisTemplate<String, Object> redisTemplate;

        // 상품 등록 메서드
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

        // 상품 수정 메서드
        @Override
        public ResponseItemDTO updateItem(Long itemId,
                                          UpdateItemServiceDTO updateItemDTO,
                                          List<MultipartFile> files) {
            try {
                ItemEntity findItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

                log.debug("남길 이미지 확인 : {}", updateItemDTO.getRemainImgId());

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
                    List<ItemImgEntity> imgList = findItem.getImages().stream()
                            .filter(img -> !updateItemDTO.getRemainImgId().contains(img.getItemImgId()))
                            .toList();

                    imgList.forEach(img -> {
                            s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName()); // S3에서 삭제
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
                return ResponseItemDTO.changeDTO(findItem);
            } catch (Exception e) {
                log.warn("Error updating item: ", e);
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
                log.debug("response: {}", response);
                return response;
            } catch (Exception e) {
                log.warn("Failed to fetch item with id {}: {}", itemId, e.getMessage());
                throw new ItemException("상품 조회 실패, 원인 :" + e.getMessage());
            }
        }

        // 상품 삭제 메서드
        @Override
        public String deleteItem(Long itemId) {
            try {
                ItemEntity findItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));

                // s3에 이미지 삭제
                findItem.getImages().forEach(
                        img -> s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName())
                );
                cartItemRepository.deleteByItem_ItemId(itemId);
                // 상품 삭제
                itemRepository.deleteById(itemId);
                log.debug("Successfully deleted item with id: {}", itemId);
                return "상품을 삭제했습니다.";
            } catch (Exception e) {
                throw new ItemException("상품 삭제하는데 실패했습니다. 원인 : " + e.getMessage());
            }
        }

        // 여러 상품을 페이징 처리해서 가져오는 메서드 : 여러 조건 검색 가능
        @Override
        @Transactional(readOnly = true)
        @TimeTrace
        public Slice<ResponseItemDTO> searchItems(ItemSearchCondition condition,
                                                 Long lastItemId,
                                                 int size,
                                                 String sort) {
            try {
                Slice<ItemEntity> itemsPage = itemRepository.itemsSearch(condition, lastItemId, size, sort);
                log.debug("itemsPage: {}", itemsPage.getContent());
                log.debug("itemsPage.size: {}", itemsPage.getContent().size());
                log.debug("itemsPage.number: {}", itemsPage.getNumber());


                return itemsPage.map(ResponseItemDTO::changeDTO);
            } catch (Exception e) {
                log.warn("exception : " + e.getMessage());
                throw new EntityNotFoundException("상품 조회에 실패하였습니다.\n" + e.getMessage());
            }
        }

        @Transactional(readOnly = true)
        @TimeTrace
        // 사용자 이메일을 통해 검색 기록을 조회하고, 그 기록에서 카테고리를 추출하여 추천 상품을 계산합니다.
        public List<ResponseItemDTO> recommendItemsBySearchHistory(String email) {
            // 사용자의 최근 검색 기록을 가져옵니다.
            // 검색 이름과 시간이 담긴 리스트
            List<SearchLog> userSearchLogs = redisSearchLogService.findRecentSearchLogs(email);
            log.debug("userSearchLogs: {}", userSearchLogs);

            // 검색 기록이 Redis에 존재하지 않거나 비정상적으로 삭제된 경우 대비
            if (userSearchLogs == null || userSearchLogs.isEmpty()) {
                return Collections.emptyList(); // 또는 기본 추천
            }

            // 검색 기록에서 카테고리를 추출합니다. 이 카테고리는 사용자가 자주 검색한 항목과 관련된 상품을 찾는 데 사용됩니다.
            Set<Category> categories = extractCategoriesFromSearchLogs(userSearchLogs);

            // 최근 검색 기록에서 키워드를 추출합니다.
            List<String> keywords = userSearchLogs.stream()
                    .map(SearchLog::getName) // SearchLog에서 name을 가져옵니다.
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .toList();
            log.debug("keywords : {}", keywords);

            List<ItemEntity> items = new ArrayList<>();
            // 키워드가 존재하는 경우, 각각의 키워드에 대해 상품을 조회합니다.
            for (String keyword : keywords) {
                // 카테고리와 키워드로 상품을 추천
                List<ItemEntity> findItems = itemRepository.findItemsByCategoriesAndKeyword(categories, keyword);
                items.addAll(sampleItems(findItems, 10));
            }
            return items.stream()
                    .map(ResponseItemDTO::changeDTO)
                    .collect(Collectors.toList());
        }

        // 검색 로그에서 카테고리를 추출합니다. 만약 카테고리가 null인 경우에는 추가하지 않도록 했습니다.
        private Set<Category> extractCategoriesFromSearchLogs(List<SearchLog> searchLogs) {
            Set<Category> categories = new HashSet<>();
            for (SearchLog log : searchLogs) {
                // 검색 기록에서 카테고리 추출
                if (log.getCategory() != null && !log.getCategory().isEmpty()) {
                    categories.addAll(log.getCategory()); // log에서 category를 가져와서 enum으로 변환
                }
            }
            log.debug("카테고리 확인 : {}", categories );
            return categories;
        }

        private List<ItemEntity> sampleItems(List<ItemEntity> items, int sampleSize) {
            Collections.shuffle(items); // 리스트를 무작위로 섞습니다.
            return items.stream()
                    .limit(sampleSize)  // 지정된 샘풀 크기만큼 반환
                    .toList();
        }

        // 대기중인 상품 리스트
        @Override
        @Transactional(readOnly = true)
        @TimeTrace
        public Slice<ResponseItemDTO> requestItemList(Long lastItemId,
                                                     int size,
                                                     String sort) {
            try {
                Slice<ItemEntity> findAllByWait = itemRepository.findAllByWait(lastItemId, size, sort);
                return findAllByWait.map(ResponseItemDTO::changeDTO);
            } catch (Exception e) {
                log.warn("exception : " + e.getMessage());
                throw new EntityNotFoundException("상품 조회에 실패하였습니다.\n" + e.getMessage());
            }
        }

        // 대기중인 상품을 판매가능하게 컨펌
        @Override
        public String changeStatusItem(Long itemId) {
            try {
                ItemEntity findItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemException("Item not found with id: " + itemId));
                // 상태를 SELL 상태로 바꾸기
                findItem.changeStatus();
                return "상품을 변경하였습니다.";
            } catch (Exception e) {
                throw new ItemException("상품의 상태를 변경하는데 실패했습니다.");
            }
        }

        @Override
        @Transactional(readOnly = true)
        @TimeTrace
        public long countItems() {
            try {
                long countAll = itemRepository.countAll();
                log.debug("countAll: {}", countAll);
                return countAll;
            } catch (Exception e) {
                throw new ItemException("상품의 수량을 가져오는데 실패했습니다.");
            }
        }

        @Override
        @Transactional(readOnly = true)
        public List<ResponseItemDTO> getTopItemIds(int topCount) {
            Set<Object> ids = redisTemplate.opsForZSet()
                    .reverseRange("item:order:zset", 0, topCount - 1);

            if(ids == null || ids.isEmpty()) return List.of();

            List<Long> itemIds = ids.stream()
                    .map(id -> Long.valueOf(String.valueOf(id)))
                    .toList();

            Map<Long, ItemEntity> entityMap = itemRepository.findByTopItemIds(itemIds).stream()
                    .collect(Collectors.toMap(ItemEntity::getItemId, e -> e));
            log.debug("조회한 엔티티 Map으로 바꾸기 {}", entityMap);

            List<ItemEntity> entityList = itemIds.stream()
                    .map(item -> {
                        ItemEntity itemEntity = entityMap.get(item);
                        log.debug("item check {}", itemEntity);
                        return itemEntity;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return entityList.stream()
                    .map(ResponseItemDTO::changeDTO)
                    .toList();
        }
    }
