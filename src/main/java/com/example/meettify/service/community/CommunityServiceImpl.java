package com.example.meettify.service.community;

import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.ResponseBoardImgDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.item.ResponseItemImgDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.community.CommunityImgEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.item.ItemImgEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.repository.community.CommunityRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    // 커뮤니티 생성
    @Override
    public ResponseBoardDTO saveBoard(CreateServiceDTO board,
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
                return ResponseBoardDTO.changeCommunity(saveCommunity);
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
    public ResponseBoardDTO updateBoard(Long communityId,
                                        UpdateServiceDTO community,
                                        List<MultipartFile> files) {
        try {
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
            return ResponseBoardDTO.changeCommunity(saveItem);
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            throw new ItemException("Failed to update the item.");
        }
    }

    // 커뮤니티 상세 페이지
    @Override
    @Transactional(readOnly = true)
    public ResponseBoardDTO getBoard(Long communityId) {
        try {
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("Community not found with id: " + communityId));
            log.info("findCommunity: {}", findCommunity);
            return ResponseBoardDTO.changeCommunity(findCommunity);
        } catch (Exception e) {
            log.error("Error retrieving community: ", e.getMessage());
            throw new BoardException("상세 페이지를 조회하는데 실패했습니다.");
        }

    }
}
