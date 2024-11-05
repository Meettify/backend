package com.example.meettify.dto.search;

import com.example.meettify.dto.meet.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SearchLog {
    private String name;
    private String createdAt;
    private Category category;
}
