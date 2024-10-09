package com.example.meettify.service.community;

import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.item.ResponseItemImgDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.repository.community.CommunityRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUploadService s3ImageUploadService;

    @Override
    public ResponseBoardDTO saveBoard(CreateServiceDTO board,
                                      List<MultipartFile> files,
                                      String memberEmail) {
        try {
            if(board != null) {
                MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);
                CommunityEntity communityEntity = CommunityEntity.createEntity(board, findMember);
                CommunityEntity saveCommunity = communityRepository.save(communityEntity);
                return ResponseBoardDTO.changeCommunity(saveCommunity);
            }
            throw new BoardException("게시글 생성 요총서헝이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 등록 실패 {}", e.getMessage());
            throw new BoardException("게시글 등록 실패 :" + e.getMessage());
        }
    }

    private List<ResponseItemImgDTO> uploadItemImages(List<MultipartFile> files) throws IOException {
        return s3ImageUploadService.upload("community", files, (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                ResponseItemImgDTO.builder()
                        .originalImgName(oriFileName)
                        .uploadImgName(uploadFileName)
                        .uploadImgPath(uploadFilePath)
                        .uploadImgUrl(uploadFileUrl)
                        .build()
        );
    }


}
