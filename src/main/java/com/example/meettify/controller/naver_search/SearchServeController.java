package com.example.meettify.controller.naver_search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *   worker : 유요한
 *   work   : 네이버 검색 API를 통해 장소 정보를 검색하는 RestController입니다.
 *            네이버 API를 호출하여 지정된 이름 또는 동적으로 지정된 검색어를 기반으로 장소를 검색하고, 검색된 장소 목록을 반환합니다.
 *   date   : 2024/12/16
 * */
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/naver")
public class SearchServeController implements SearchServeControllerDocs{
    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;

    /**
     * 네이버 검색 API를 이용하여 지정된 이름으로 장소를 검색합니다.
     * @param name 검색할 장소의 이름
     * @return 검색된 장소 목록
     */
    @Override
    @GetMapping("/{name}")
    public List<Map<String, String>> naverSearch(@PathVariable String name) {
        return searchPlaceList(name);
    }



    @Override
    @GetMapping("")
    public List<Map<String, String>> naverSearchDynamic(@RequestParam String query) {
        return searchPlaceList(query);
    }

    /**
     * 네이버 검색 API를 이용하여 장소를 검색하는 메서드입니다.
     * @param query : 검색할 장소의 이름 또는 검색어
     * @return 검색된 장소 목록
     */
    private List<Map<String, String>> searchPlaceList(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("검색어가 유효하지 않습니다.");
        }

        List<Map<String, String>> placeList = new ArrayList<>();

        try {
            // UTF-8로 인코딩된 검색어 생성
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
            log.info("buffer: {}", buffer);
            String encode = StandardCharsets.UTF_8.decode(buffer).toString();
            log.info("encode: {}", encode);

            // 네이버 검색 API를 호출하기 위한 URI 생성
            URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local")
                    .queryParam("query", encode)
                    .queryParam("display", 10)
                    .queryParam("start",1)
                    .queryParam("sort", "random")
                    .encode()
                    .build()
                    .toUri();
            log.info("uri: {}", uri);

            // RestTemplate을 사용하여 네이버 API에 요청을 보냄
            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<Void> request = RequestEntity.get(uri).header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret).build();

            // API 응답 데이터를 JSON 형식으로 변환
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            log.info("jsonNode: {}", jsonNode);

            // 검색 결과 중에서 장소 정보를 추출하여 리스트에 저장
            JsonNode itemsNode = jsonNode.path("items");
            for (JsonNode itemNode : itemsNode) {
                Map<String, String> place = new HashMap<>();
                place.put("title", itemNode.path("title").asText());        // 장소 이름
                place.put("address", itemNode.path("address").asText());    // 장소 주소

                // 위도와 경도를 double 형식으로 변환하여 저장
                double latitude = Double.parseDouble(itemNode.path("mapy").asText()) / 1e7; // 위도
                double longitude = Double.parseDouble(itemNode.path("mapx").asText()) / 1e7;// 경도
                place.put("lat", String.valueOf(latitude));
                place.put("lng", String.valueOf(longitude));
                // 리스트에 추가
                placeList.add(place);
            }
        } catch (Exception e) {
            log.error("API 호출 실패", e);
            throw new RuntimeException("네이버 검색 API 호출에 실패했습니다.");
        }
        return placeList;
    }
}
