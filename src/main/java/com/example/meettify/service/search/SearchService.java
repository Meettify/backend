package com.example.meettify.service.search;

import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchResponseDTO;

public interface SearchService {

    public SearchResponseDTO searchResponseDTO(SearchCondition searchCondition, String email);
}
