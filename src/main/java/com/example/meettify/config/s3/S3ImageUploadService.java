package com.example.meettify.config.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.meettify.exception.file.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageUploadService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    // fileType : 파일의 타입에 따라 업로드 경로를 지정하는 문자열
    // factory : 파일 업로드 후 반환할 DTO 객체를 생성하는 팩토리 인터페이스
    public <T> List<T> upload(String fileType,
                              List<MultipartFile> multipartFiles,
                              FileDTOFactory<T> factory) throws IOException {
        // 새로운 이미지를 받지 않음
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            log.debug("업로드할 파일이 없습니다. S3 업로드 생략");
            return Collections.emptyList();
        }

        List<T> uploadFiles = new ArrayList<>();
        // 파일이 업로드될 경로를 생성하는 코드
        // ex) postImages/2024/09/25
        // S3에 파일을 업로드할 때, 파일이 업로드될 디렉터리를 지정할 수 있다.
        String uploadPath = fileType + "/" + getFolderName();
        log.debug("Uploading " + multipartFiles.size() + " files to " + uploadPath);

        for (MultipartFile multipartFile : multipartFiles) {
            // 파일의 원본 이름(파일명)을 가져오는 코드
            String originalName = multipartFile.getOriginalFilename();

            if (originalName == null || !originalName.contains(".")) {
                throw new FileUploadException("유효하지 않은 파일 이름입니다.");
            }

            // 업로드할 파일의 이름을 고유한 UUID 기반으로 변경
            // ex) profile.jpg가 123e4567-e89b-12d3-a456-426614174000.jpg로 변경
            // 이렇게 하는 이유는 충돌 방지와 서로 덮어쓰지 않도록 하기 위해서
            String uploadName = getUuidFileName(originalName);
            // S3에 파일을 업로드한 후, 해당 파일의 URL을 저장할 변수
            String uploadUrl = "";

            // S3에 업로드할 파일의 메타데이터를 설정하기 위한 객체
            // 파일 크기와 파일 타입을 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 파일의 크기(바이트 단위)를 가져와, 메타데이터에 파일 크기를 설정
            // 파일 크기를 S3에 알려주어 효율적인 저장 및 관리를 가능하게 합니다.
            objectMetadata.setContentLength(multipartFile.getSize());
            // 파일의 MIME 타입(컨텐츠 타입)을 설정하는 코드
            objectMetadata.setContentType(multipartFile.getContentType());

            // try-with-resources 구문을 사용하여 InputStream을 생성
            // multipartFile.getInputStream() 메서드를 호출하여 파일의 내용을 읽을 수 있는 InputStream 객체를 가져옵니다.
            try(InputStream inputStream = multipartFile.getInputStream()) {
                // images/2023/10/22/123e4567-e89b-12d3-a456-426614174000.jpg 이런식으로 생성
                String keyName = uploadPath + "/" + uploadName;
                // PutObjectRequest 객체를 생성하여 S3에 파일을 업로드
                amazonS3.putObject(new PutObjectRequest(bucket, keyName, inputStream, objectMetadata));
                // S3에 업로드한 파일의 URL을 가져옵니다.
                // 해당 파일에 접근할 수 있는 URL을 반환합니다.
                uploadUrl = amazonS3.getUrl(bucket, keyName).toString();
            } catch (Exception e) {
                log.error("파일 업로드 실패 : " + e.getMessage());
                throw new FileUploadException("파일 업로드 실패");
            }
            T fileDTO = factory.createFileDTO(originalName, uploadName, uploadPath, uploadUrl);
            uploadFiles.add(fileDTO);
        }
        return uploadFiles;
    }

    // 파일이 업로드될 S3 경로에 날짜별로 폴더를 생성하는 메서드
    // yyyy/MM/dd 형식의 날짜 폴더 이름을 반환
    private String getFolderName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // 파일의 원래 이름을 기반으로 고유한 UUID를 사용하여 새로운 파일명을 생성하는 메서드
    private String getUuidFileName(String oriFileName) {
        if (oriFileName == null || !oriFileName.contains(".")) {
            throw new FileUploadException("유효하지 않은 파일 이름: 확장자가 없습니다.");
        }
        String ext = oriFileName.substring(oriFileName.lastIndexOf(".") + 1).toLowerCase();

        return UUID.randomUUID().toString() + "." + ext;
    }

    // S3에 업로드된 파일 삭제
    public String deleteFile(String uploadFilePath, String uuidFileName) {
        log.debug("s3 이미지 삭제 로직 실행");
        String result = "success";
        try {
            // ex) 구분/년/월/일/파일.확장자
            String keyName = uploadFilePath + "/" + uuidFileName;
            log.debug("삭제 keyName {}", keyName);
            boolean isObjectExist = amazonS3.doesObjectExist(bucket, keyName);

            if (isObjectExist) {
                amazonS3.deleteObject(bucket, keyName);
            } else {
                result = "file not found";
            }
        } catch (AmazonS3Exception e) {
            // S3에서 파일 삭제 실패
            result = "S3 file deletion failed: " + e.getMessage();
            log.error("S3 file deletion failed", e);
        } catch (Exception e) {
            // 기타 예외 처리
            result = "file deletion failed: " + e.getMessage();
            log.error("File deletion failed", e);
        }
        log.debug("삭제 로직 결과 {}", result);
        return result;
    }




}
