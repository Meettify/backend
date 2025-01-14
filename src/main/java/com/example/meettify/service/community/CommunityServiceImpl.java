package com.example.meettify.service.community;

import com.example.meettify.config.cookie.CookieUtils;
import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.board.ResponseBoardImgDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.community.CommunityImgEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.repository.jpa.community.CommunityRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUploadService s3ImageUploadService;
    private final RedisCommunityService redisCommunityService;

    // 커뮤니티 생성
    @Override
    public ResponseCommunityDTO saveBoard(CreateServiceDTO board,
                                          List<MultipartFile> files,
                                          String memberEmail) {
        try {
            if(board != null) {
                // 회원 조회
                MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);
                // 커뮤니티 엔티티 생성
                CommunityEntity communityEntity = CommunityEntity.createEntity(board, findMember);
                // s3에 이미지 업로드
                List<ResponseBoardImgDTO> uploadImages = uploadCommunityImages(files);
                // 커뮤니티 이미지 엔티티 생성
                List<CommunityImgEntity> images = CommunityImgEntity.createEntityList(uploadImages, communityEntity);
                // 커뮤니티 엔티티에 이미지들 추가
                communityEntity.getImages().addAll(images);
                // DB에 저장
                CommunityEntity saveCommunity = communityRepository.save(communityEntity);
                return ResponseCommunityDTO.changeSaveCommunity(saveCommunity, findMember.getNickName());
            }
            throw new BoardException("게시글 생성 요총서헝이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 등록 실패 {}", e.getMessage());
            throw new BoardException("게시글 등록 실패 :" + e.getMessage());
        }
    }

    private List<ResponseBoardImgDTO> uploadCommunityImages(List<MultipartFile> files) throws IOException {
        return s3ImageUploadService.upload("community", files, (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                ResponseBoardImgDTO.builder()
                        .originalImgName(oriFileName)
                        .uploadImgName(uploadFileName)
                        .uploadImgPath(uploadFilePath)
                        .uploadImgUrl(uploadFileUrl)
                        .build()
        );
    }

    // 커뮤니티 수정
    @Override
    public ResponseCommunityDTO updateBoard(Long communityId,
                                            UpdateServiceDTO community,
                                            List<MultipartFile> files) {
        try {
            // 커뮤니티 조회
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new ItemException("Community not found with id: " + communityId));

            // 만약 남겨야 할 이미지 ID가 비어있다면, 모든 이미지를 삭제
            if (community.getRemainImgId().isEmpty()) {
                // s3에서 해당 상품의 이미지 모두 삭제
                findCommunity.getImages().forEach(
                        img -> s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName())
                );
                // 리스트에서 모든 이미지를 삭제
                requireNonNull(findCommunity).getImages().clear();
            } else {
                // 먼저 S3에서 삭제해야 할 이미지 처리
                findCommunity.getImages().forEach(img -> {
                    if (!community.getRemainImgId().contains(img.getItemImgId())) {
                        s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName()); // S3에서 삭제
                    }
                });

                // 이미지를 필터링하여 남겨야 할 이미지만 남김
                findCommunity.remainImgId(community.getRemainImgId());
            }

            // s3에 추가할 이미지
            List<ResponseBoardImgDTO> responseImages = uploadCommunityImages(files);
            // 이미지들을 엔티티로 변환
            List<CommunityImgEntity> images = CommunityImgEntity.createEntityList(responseImages, findCommunity);
            // 새로운 이미지들 추가
            findCommunity.updateCommunity(community, images);
            CommunityEntity saveItem = communityRepository.save(findCommunity);
            return ResponseCommunityDTO.changeCommunity(saveItem);
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            throw new ItemException("Failed to update the item.");
        }
    }

    // 커뮤니티 상세 페이지
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public ResponseCommunityDTO getBoard(Long communityId,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        try {
            String viewCountCookieValue = CookieUtils.getViewCountCookieValue(request, response);

            if(!redisCommunityService.isExistInSet(viewCountCookieValue, communityId)) {
                increaseViewCountAsync("viewCount_community" + communityId);
                redisCommunityService.addToSet(viewCountCookieValue, communityId);
            }

            // 조회수 증가 후 다시 엔티티 조회
            CommunityEntity findCommunity = communityRepository.findByCommunityId(communityId);
            log.info("findCommunity: {}", findCommunity);

            // 데이터베이스에서 조회한 조회수
            int dbViewCount = findCommunity.getViewCount();
            // Redis에서 조회수 가져오기
            Integer redisViewCount = null;
            try {
                redisViewCount = redisCommunityService.getViewCount("viewCount_community" + communityId);
            } catch (Exception e) {
                log.error("Error retrieving view count from Redis for communityId {}: {}", communityId, e.getMessage());
                // Redis 오류 발생 시 기본 조회수를 사용
            }
            // 조회수 합산
            int totalViewCount = dbViewCount + (redisViewCount != null ? redisViewCount : 0);


            // 조회수는 이미 증가했으므로 엔티티에서 바로 조회 가능
            return ResponseCommunityDTO.communityDetail(findCommunity, totalViewCount);
        } catch (Exception e) {
            log.error("Error retrieving community {} ", e.getMessage());
            throw new BoardException("상세 페이지를 조회하는데 실패했습니다.");
        }
    }

    // 이 메서드는 Redis에 비동기적으로 조회수를 증가시킵니다.
    // 이를 통해 사용자 요청 처리 시간에 영향을 주지 않고, 조회수를 빠르게 업데이트할 수 있습니다.
    @Async
    public void increaseViewCountAsync(String viewCountKey) {
        try {
            redisCommunityService.increaseData(viewCountKey);
        } catch (Exception e) {
            log.error("Error increasing view count in Redis for key {}: {}", viewCountKey, e.getMessage());
        }
    }

    // 커뮤니티 삭제
    @Override
    public String deleteBoard(Long communityId) {
        try {
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다. : " + communityId));
            log.info("findCommunity {}", findCommunity);
            if(findCommunity != null) {
                findCommunity.getImages().forEach(
                        img -> s3ImageUploadService.deleteFile(img.getUploadImgPath(), img.getUploadImgName())
                );
                communityRepository.delete(findCommunity);
                redisCommunityService.deleteData("viewCount_community" + findCommunity.getCommunityId());
                return "삭제가 완료되었습니다.";
            }
            throw new BoardException("커뮤니티 글이 존재하지 않습니다. 잘못된 id를 보냈습니다.");
        } catch (Exception e) {
            log.error("Error deleting community {} ", e.getMessage());
            throw new BoardException("커뮤니티 글을 삭제하는데 실패했습니다. :" + e.getMessage());
        }
    }

    // 페이징 처리해서 보여줄 커뮤니티 게시글
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseCommunityDTO> getBoards(Pageable pageable) {
        try {
            Page<CommunityEntity> findAllCommunity = communityRepository.findAll(pageable);
            log.info("조회된 커뮤니티 수 : {}", findAllCommunity.getTotalElements());
            log.info("조회된 커뮤니티 : {}", findAllCommunity);

            countRedisView(findAllCommunity);

            return findAllCommunity.map(ResponseCommunityDTO::changeCommunity);
        } catch (Exception e) {
            log.error("Error retrieving community {} ", e.getMessage());
            throw new BoardException("커뮤니티 글을 가져오는데 실패했습니다. : " + e.getMessage());
        }
    }

    private void countRedisView(Page<CommunityEntity> findAllCommunity) {
        // 각 커뮤니티 게시글에 대해 Redis 조회수를 가져와서 합산
        findAllCommunity.forEach(community -> {
            Integer redisViewCount = redisCommunityService.getViewCount("viewCount_community" + community.getCommunityId());
            int totalViewCount = community.getViewCount() + (redisViewCount != null ? redisViewCount : 0);
            community.setViewCount(totalViewCount);  // or update DTO to reflect this view count
        });
    }

    // 커뮤니티 제목 검색
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseCommunityDTO> searchTitle(Pageable pageable, String searchTitle) {
        try {
            Page<CommunityEntity> findAllByTitle = communityRepository.findBySearchTitle(pageable, searchTitle);
            log.info("조회된 커뮤니티 수  {}", findAllByTitle.getTotalElements());
            log.info("조회된 커뮤니티  {}", findAllByTitle);
            countRedisView(findAllByTitle);
            return findAllByTitle.map(ResponseCommunityDTO::changeCommunity);
        } catch (Exception e) {
            log.error("Error retrieving community {} ", e.getMessage());
            throw new BoardException("커뮤니티 글을 가져오는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 내 커뮤니티 보기
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseCommunityDTO> getMyBoards(Pageable pageable, String memberEmail) {
        try {
            Page<CommunityEntity> findAllCommunity = communityRepository.findAllByMemberEmail(memberEmail, pageable);
            countRedisView(findAllCommunity);
            return findAllCommunity.map(ResponseCommunityDTO::changeCommunity);
        } catch (Exception e) {
            log.error("Error retrieving community {}", e.getMessage());
            throw new BoardException("커뮤니티 글을 가져오는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 내 커뮤니티 수
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public long countMyCommunity(String memberEmail) {
        try {
            long count = communityRepository.countByMemberMemberEmail(memberEmail);
            log.info("countMyCommunity {}", count);
            return count;
        } catch (Exception e) {
            throw new BoardException("커뮤니티 수량을 가져오는데 실패");
        }
    }

    // 모든 커뮤니티 수
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public long countAllCommunity() {
        try {
            long count = communityRepository.countAllItems();
            log.info("countAllCommunity {}", count);
            return count;
        } catch (Exception e) {
            throw new BoardException("커뮤니티 수량을 가져오는데 실패");
        }
    }
}
