package com.example.meettify.controller.community;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.service.community.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class CommunityController implements CommunityControllerDocs{
    private final CommunityService communityService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createCommunity(@RequestPart CreateBoardDTO community,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CreateServiceDTO changeServiceDTO = modelMapper.map(community, CreateServiceDTO.class);
            log.info("service DTO: {}", changeServiceDTO);
            ResponseBoardDTO response = communityService.saveBoard(changeServiceDTO, files, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
